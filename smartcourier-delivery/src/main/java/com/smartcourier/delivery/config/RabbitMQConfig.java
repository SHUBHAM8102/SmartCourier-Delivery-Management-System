package com.smartcourier.delivery.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartcourier.exchange";
    public static final String DELIVERY_CREATED_QUEUE = "delivery.created.queue";
    public static final String DELIVERY_CREATED_ROUTING_KEY = "delivery.created";
    public static final String NOTIFICATION_DELIVERY_QUEUE = "notification.delivery.queue";
    public static final String NOTIFICATION_DELIVERY_ROUTING_KEY = "notification.delivery.created";
    public static final String STATUS_CHANGED_QUEUE = "status.changed.queue";
    public static final String STATUS_CHANGED_ROUTING_KEY = "status.changed";

    @Bean
    public TopicExchange smartcourierExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue deliveryCreatedQueue() {
        return QueueBuilder.durable(DELIVERY_CREATED_QUEUE).build();
    }

    @Bean
    public Queue statusChangedQueue() {
        return QueueBuilder.durable(STATUS_CHANGED_QUEUE).build();
    }

    @Bean
    public Binding deliveryCreatedBinding(Queue deliveryCreatedQueue, TopicExchange smartcourierExchange) {
        return BindingBuilder.bind(deliveryCreatedQueue)
                .to(smartcourierExchange)
                .with(DELIVERY_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding statusChangedBinding(Queue statusChangedQueue, TopicExchange smartcourierExchange) {
        return BindingBuilder.bind(statusChangedQueue)
                .to(smartcourierExchange)
                .with(STATUS_CHANGED_ROUTING_KEY);
    }

    @Bean
    public Queue notificationDeliveryQueue() {
        return QueueBuilder.durable(NOTIFICATION_DELIVERY_QUEUE).build();
    }

    @Bean
    public Binding notificationDeliveryBinding(Queue notificationDeliveryQueue, TopicExchange smartcourierExchange) {
        return BindingBuilder.bind(notificationDeliveryQueue)
                .to(smartcourierExchange)
                .with(NOTIFICATION_DELIVERY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
