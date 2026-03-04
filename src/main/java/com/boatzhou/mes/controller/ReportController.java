package com.boatzhou.mes.controller;

import com.boatzhou.mes.common.Result;
import com.boatzhou.mes.dto.report.WorkOrderReportResponse;
import com.boatzhou.mes.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 报表统计控制器。
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "报表统计")
@SecurityRequirement(name = "BearerAuth")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 工单统计报表接口（按日期 + 设备类型维度）。
     */
    @GetMapping("/work-orders")
    @Operation(summary = "工单统计报表", description = "仅 ADMIN 可访问，支持日期范围和设备类型筛选")
    public Result<WorkOrderReportResponse> workOrderReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String deviceType) {

        // 默认统计最近 7 天数据。
        LocalDate end = endDate == null ? LocalDate.now() : endDate;
        LocalDate start = startDate == null ? end.minusDays(6) : startDate;

        return Result.success(reportService.workOrderReport(start, end, deviceType));
    }
}
