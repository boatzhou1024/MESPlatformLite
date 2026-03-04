package com.boatzhou.mes.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 拓扑配置（用于工单通知事件）。
 */
@Configuration
public class RabbitMqConfig {

    /** 工单事件交换机。 */
    public static final String WORK_ORDER_EXCHANGE = "mes.workorder.exchange";

    /** 通知消费者监听队列。 */
    public static final String WORK_ORDER_QUEUE = "mes.workorder.notification.queue";

    /** 生产者投递消息时使用的 routing key。 */
    public static final String WORK_ORDER_ROUTING_KEY = "workorder.notification";

    /**
     * 声明持久化 Direct 交换机。
     */
    @Bean
    public DirectExchange workOrderExchange() {
        return new DirectExchange(WORK_ORDER_EXCHANGE, true, false);
    }

    /**
     * 声明持久化队列。
     */
    @Bean
    public Queue workOrderQueue() {
        return new Queue(WORK_ORDER_QUEUE, true);
    }

    /**
     * 绑定队列到交换机，并指定 routing key。
     */
    @Bean
    public Binding workOrderBinding(DirectExchange workOrderExchange, Queue workOrderQueue) {
        return BindingBuilder.bind(workOrderQueue).to(workOrderExchange).with(WORK_ORDER_ROUTING_KEY);
    }

    /**
     * 使用 JSON 消息转换器，使 POJO 可自动序列化/反序列化。
     */
    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
