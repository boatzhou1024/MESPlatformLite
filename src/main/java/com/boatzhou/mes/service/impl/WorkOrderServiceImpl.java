package com.boatzhou.mes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boatzhou.mes.common.BusinessException;
import com.boatzhou.mes.common.ErrorCode;
import com.boatzhou.mes.dto.workorder.WorkOrderAssignRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderCreateRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderProgressRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderStatusRequest;
import com.boatzhou.mes.entity.Device;
import com.boatzhou.mes.entity.WorkOrder;
import com.boatzhou.mes.enums.WorkOrderStatus;
import com.boatzhou.mes.mapper.DeviceMapper;
import com.boatzhou.mes.mapper.WorkOrderMapper;
import com.boatzhou.mes.mq.WorkOrderEventProducer;
import com.boatzhou.mes.service.WorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 工单服务实现。
 *
 * <p>核心点：</p>
 * <p>1) 使用乐观锁（version 字段）避免并发覆盖；</p>
 * <p>2) 状态变化后通过 RabbitMQ 异步发送通知事件；</p>
 * <p>3) 内置状态流转规则，防止非法回退。</p>
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    /** 工单号中的时间格式片段。 */
    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final WorkOrderMapper workOrderMapper;
    private final DeviceMapper deviceMapper;
    private final WorkOrderEventProducer workOrderEventProducer;

    public WorkOrderServiceImpl(WorkOrderMapper workOrderMapper,
                                DeviceMapper deviceMapper,
                                WorkOrderEventProducer workOrderEventProducer) {
        this.workOrderMapper = workOrderMapper;
        this.deviceMapper = deviceMapper;
        this.workOrderEventProducer = workOrderEventProducer;
    }

    /**
     * 创建工单，并发送 CREATED 事件。
     */
    @Override
    public WorkOrder create(WorkOrderCreateRequest request) {
        // 可选校验：如果传了 deviceId，需保证设备存在。
        if (request.getDeviceId() != null) {
            Device device = deviceMapper.selectById(request.getDeviceId());
            if (device == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "关联设备不存在");
            }
        }

        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setDeviceId(request.getDeviceId());
        workOrder.setAssigneeId(request.getAssigneeId());
        workOrder.setPriority(request.getPriority() == null ? 3 : request.getPriority());
        workOrder.setStatus(WorkOrderStatus.PENDING.name());
        workOrder.setProgress(0);

        if (workOrderMapper.insert(workOrder) <= 0) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "工单创建失败");
        }

        workOrderEventProducer.sendEvent(workOrder.getId(), workOrder.getOrderNo(), "CREATED", "工单已创建");
        return workOrder;
    }

    /**
     * 分配工单处理人，并发送 ASSIGNED 事件。
     */
    @Override
    public WorkOrder assign(Long id, WorkOrderAssignRequest request) {
        getRequiredById(id);

        // 乐观锁校验由 MyBatis-Plus 结合 @Version 自动完成。
        WorkOrder update = new WorkOrder();
        update.setId(id);
        update.setAssigneeId(request.getAssigneeId());
        update.setVersion(request.getVersion());

        if (workOrderMapper.updateById(update) <= 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "工单版本冲突，请刷新后重试");
        }

        WorkOrder latest = getRequiredById(id);
        workOrderEventProducer.sendEvent(latest.getId(), latest.getOrderNo(), "ASSIGNED", "工单已分配处理人");
        return latest;
    }

    /**
     * 更新工单进度，并按规则联动状态。
     */
    @Override
    public WorkOrder updateProgress(Long id, WorkOrderProgressRequest request) {
        WorkOrder existed = getRequiredById(id);

        // 业务规则：已完成工单不允许回退进度。
        if (WorkOrderStatus.COMPLETED.name().equals(existed.getStatus()) && request.getProgress() < 100) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已完成工单不允许回退进度");
        }

        WorkOrder update = new WorkOrder();
        update.setId(id);
        update.setVersion(request.getVersion());
        update.setProgress(request.getProgress());

        // 进度与状态联动规则。
        if (request.getProgress() >= 100) {
            update.setStatus(WorkOrderStatus.COMPLETED.name());
            update.setCompletedAt(LocalDateTime.now());
        } else if (request.getProgress() > 0 && WorkOrderStatus.PENDING.name().equals(existed.getStatus())) {
            update.setStatus(WorkOrderStatus.PROCESSING.name());
        }

        if (workOrderMapper.updateById(update) <= 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "工单版本冲突，请刷新后重试");
        }

        WorkOrder latest = getRequiredById(id);
        workOrderEventProducer.sendEvent(latest.getId(), latest.getOrderNo(), "PROGRESS_UPDATED", "工单进度已更新");
        return latest;
    }

    /**
     * 更新工单状态，并发送 STATUS_UPDATED 事件。
     */
    @Override
    public WorkOrder updateStatus(Long id, WorkOrderStatusRequest request) {
        WorkOrder existed = getRequiredById(id);
        String targetStatus = request.getStatus();

        // 业务规则：已完成状态不允许回退。
        if (WorkOrderStatus.COMPLETED.name().equals(existed.getStatus())
                && !WorkOrderStatus.COMPLETED.name().equals(targetStatus)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已完成工单不允许回退状态");
        }

        WorkOrder update = new WorkOrder();
        update.setId(id);
        update.setVersion(request.getVersion());
        update.setStatus(targetStatus);

        // 保证状态与进度一致。
        if (WorkOrderStatus.COMPLETED.name().equals(targetStatus)) {
            update.setProgress(100);
            update.setCompletedAt(LocalDateTime.now());
        }

        if (workOrderMapper.updateById(update) <= 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "工单版本冲突，请刷新后重试");
        }

        WorkOrder latest = getRequiredById(id);
        workOrderEventProducer.sendEvent(latest.getId(), latest.getOrderNo(), "STATUS_UPDATED", "工单状态已更新");
        return latest;
    }

    /**
     * 按 ID 查询工单。
     */
    @Override
    public WorkOrder getById(Long id) {
        return getRequiredById(id);
    }

    /**
     * 条件查询工单列表。
     */
    @Override
    public List<WorkOrder> list(String status, Long assigneeId, Long deviceId) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), WorkOrder::getStatus, status);
        wrapper.eq(assigneeId != null, WorkOrder::getAssigneeId, assigneeId);
        wrapper.eq(deviceId != null, WorkOrder::getDeviceId, deviceId);
        wrapper.orderByDesc(WorkOrder::getCreatedAt);
        return workOrderMapper.selectList(wrapper);
    }

    /**
     * 必须存在的查询辅助方法，不存在则抛异常。
     */
    private WorkOrder getRequiredById(Long id) {
        WorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "工单不存在");
        }
        return workOrder;
    }

    /**
     * 生成工单号：WO + 时间戳 + 4 位随机数。
     */
    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "WO" + timePart + randomPart;
    }
}
