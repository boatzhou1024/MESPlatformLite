package com.boatzhou.mes.dto.workorder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建工单请求参数。
 */
@Data
public class WorkOrderCreateRequest {

    /** 工单标题。 */
    @NotBlank(message = "工单标题不能为空")
    private String title;

    /** 问题描述。 */
    private String description;

    /** 关联设备 ID（可选）。 */
    private Long deviceId;

    /** 初始处理人 ID（可选）。 */
    private Long assigneeId;

    /** 优先级（1 最高，5 最低）。 */
    @Min(value = 1, message = "优先级不能小于 1")
    @Max(value = 5, message = "优先级不能大于 5")
    private Integer priority;
}
