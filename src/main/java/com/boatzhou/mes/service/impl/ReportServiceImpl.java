package com.boatzhou.mes.service.impl;

import com.boatzhou.mes.common.BusinessException;
import com.boatzhou.mes.common.ErrorCode;
import com.boatzhou.mes.dto.report.WorkOrderReportItem;
import com.boatzhou.mes.dto.report.WorkOrderReportResponse;
import com.boatzhou.mes.mapper.WorkOrderMapper;
import com.boatzhou.mes.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * 报表服务实现。
 *
 * <p>设计说明：</p>
 * <p>1) 统计运算主要在 SQL 聚合层完成；</p>
 * <p>2) 按查询条件缓存报表结果，减少重复统计开销。</p>
 */
@Service
public class ReportServiceImpl implements ReportService {

    /** 报表缓存 key 前缀。 */
    private static final String WORK_ORDER_REPORT_CACHE_PREFIX = "mes:report:workorder:";

    private final WorkOrderMapper workOrderMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ReportServiceImpl(WorkOrderMapper workOrderMapper,
                             StringRedisTemplate stringRedisTemplate,
                             ObjectMapper objectMapper) {
        this.workOrderMapper = workOrderMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 构建工单统计报表（日期范围 + 可选设备类型）。
     */
    @Override
    public WorkOrderReportResponse workOrderReport(LocalDate startDate, LocalDate endDate, String deviceType) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "开始日期不能晚于结束日期");
        }

        String source = startDate + "|" + endDate + "|" + (StringUtils.hasText(deviceType) ? deviceType : "");
        String cacheKey = WORK_ORDER_REPORT_CACHE_PREFIX
                + DigestUtils.md5DigestAsHex(source.getBytes(StandardCharsets.UTF_8));

        // 先查缓存。
        String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cachedJson)) {
            try {
                return objectMapper.readValue(cachedJson, WorkOrderReportResponse.class);
            } catch (Exception ignored) {
                // 缓存反序列化失败时，回源数据库。
            }
        }

        // 执行聚合 SQL，按天 + 设备类型统计。
        List<WorkOrderReportItem> items = workOrderMapper.selectWorkOrderReport(startDate, endDate, deviceType);

        long totalCount = 0L;
        long completedCount = 0L;

        // 计算每行完成率，并汇总总量。
        for (WorkOrderReportItem item : items) {
            long itemTotal = item.getTotalCount() == null ? 0L : item.getTotalCount();
            long itemCompleted = item.getCompletedCount() == null ? 0L : item.getCompletedCount();

            totalCount += itemTotal;
            completedCount += itemCompleted;

            BigDecimal itemCompletionRate = itemTotal == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(itemCompleted)
                    .divide(BigDecimal.valueOf(itemTotal), 4, RoundingMode.HALF_UP);
            item.setCompletionRate(itemCompletionRate);
        }

        BigDecimal overallCompletionRate = totalCount == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(completedCount)
                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP);

        WorkOrderReportResponse response = new WorkOrderReportResponse();
        response.setTotalCount(totalCount);
        response.setCompletedCount(completedCount);
        response.setOverallCompletionRate(overallCompletionRate);
        response.setItems(items);

        // 缓存 10 分钟，平衡实时性与性能。
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(response), Duration.ofMinutes(10));
        } catch (Exception ignored) {
            // no-op
        }

        return response;
    }
}
