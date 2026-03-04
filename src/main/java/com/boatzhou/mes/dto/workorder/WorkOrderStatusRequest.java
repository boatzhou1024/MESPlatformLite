package com.boatzhou.mes.dto.workorder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新工单状态请求参数。
 */
@Data
public class WorkOrderStatusRequest {

    /** 目标状态值。 */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "PENDING|PROCESSING|COMPLETED", message = "状态必须是 PENDING/PROCESSING/COMPLETED")
    private String status;

    /** 工单当前版本号（用于乐观锁）。 */
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
