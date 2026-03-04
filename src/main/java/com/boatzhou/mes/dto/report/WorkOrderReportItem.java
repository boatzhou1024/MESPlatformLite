package com.boatzhou.mes.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工单报表明细行。
 */
@Data
public class WorkOrderReportItem {

    /** 统计日期。 */
    private LocalDate statDate;

    /** 设备类型维度。 */
    private String deviceType;

    /** 该维度下工单总数。 */
    private Long totalCount;

    /** 该维度下已完成工单数。 */
    private Long completedCount;

    /** 完成率（completedCount / totalCount）。 */
    private BigDecimal completionRate;
}
