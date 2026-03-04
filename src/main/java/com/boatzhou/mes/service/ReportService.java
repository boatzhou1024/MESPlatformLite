package com.boatzhou.mes.service;

import com.boatzhou.mes.dto.report.WorkOrderReportResponse;

import java.time.LocalDate;

/**
 * 报表领域服务接口。
 */
public interface ReportService {

    /**
     * 构建工单统计报表。
     */
    WorkOrderReportResponse workOrderReport(LocalDate startDate, LocalDate endDate, String deviceType);
}
