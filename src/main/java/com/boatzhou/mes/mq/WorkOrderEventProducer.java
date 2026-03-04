package com.boatzhou.mes.mq;

import com.boatzhou.mes.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 工单事件消息生产者。
 */
@Component
public class WorkOrderEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public WorkOrderEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送工单事件消息到 RabbitMQ。
     */
    public void sendEvent(Long orderId, String orderNo, String eventType, String message) {
        WorkOrderEvent event = new WorkOrderEvent();
        event.setOrderId(orderId);
        event.setOrderNo(orderNo);
        event.setEventType(eventType);
        event.setMessage(message);
        event.setEventTime(LocalDateTime.now());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.WORK_ORDER_EXCHANGE,
                RabbitMqConfig.WORK_ORDER_ROUTING_KEY,
                event
        );
    }
}
