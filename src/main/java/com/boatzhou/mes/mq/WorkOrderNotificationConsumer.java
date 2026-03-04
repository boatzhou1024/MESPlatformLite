package com.boatzhou.mes.mq;

import com.boatzhou.mes.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 工单通知消息消费者。
 */
@Slf4j
@Component
public class WorkOrderNotificationConsumer {

    /**
     * 消费队列中的工单事件。
     */
    @RabbitListener(queues = RabbitMqConfig.WORK_ORDER_QUEUE)
    public void onWorkOrderEvent(WorkOrderEvent event) {
        log.info("收到工单事件: orderNo={}, eventType={}, message={}",
                event.getOrderNo(), event.getEventType(), event.getMessage());
    }
}
