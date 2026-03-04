package com.boatzhou.mes.mq;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单通知事件消息体。
 */
@Data
public class WorkOrderEvent implements Serializable {

    /** 工单 ID。 */
    private Long orderId;

    /** 工单编号。 */
    private String orderNo;

    /** 事件类型（如 CREATED / ASSIGNED / STATUS_UPDATED）。 */
    private String eventType;

    /** 事件描述。 */
    private String message;

    /** 事件发生时间。 */
    private LocalDateTime eventTime;
}
