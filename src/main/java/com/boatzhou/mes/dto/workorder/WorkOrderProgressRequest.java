package com.boatzhou.mes.dto.workorder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新工单进度请求参数。
 */
@Data
public class WorkOrderProgressRequest {

    /** 进度值，范围 [0,100]。 */
    @NotNull(message = "进度不能为空")
    @Min(value = 0, message = "进度不能小于 0")
    @Max(value = 100, message = "进度不能大于 100")
    private Integer progress;

    /** 工单当前版本号（用于乐观锁）。 */
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
