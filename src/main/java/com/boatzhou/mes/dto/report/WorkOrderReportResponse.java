package com.boatzhou.mes.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工单报表汇总响应体。
 */
@Data
public class WorkOrderReportResponse {

    /** 总工单数。 */
    private Long totalCount;

    /** 已完成工单数。 */
    private Long completedCount;

    /** 总体完成率。 */
    private BigDecimal overallCompletionRate;

    /** 维度明细列表。 */
    private List<WorkOrderReportItem> items;
}
