package com.boatzhou.mes.dto.workorder;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分配工单请求参数。
 */
@Data
public class WorkOrderAssignRequest {

    /** 处理人 ID。 */
    @NotNull(message = "处理人不能为空")
    private Long assigneeId;

    /** 工单当前版本号（用于乐观锁）。 */
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
