package com.smartcourier.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartcourier.exchange";
    public static final String USER_REGISTERED_QUEUE = "user.registered.queue";
    public static final String NOTIFICATION_DELIVERY_QUEUE = "notification.delivery.queue";
    public static final String NOTIFICATION_DELIVERY_ROUTING_KEY = "notification.delivery.created";
    public static final String STATUS_CHANGED_QUEUE = "status.changed.queue";
    public static final String EXCEPTION_RAISED_QUEUE = "exception.raised.queue";

    @Bean
    public TopicExchange smartcourierExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(USER_REGISTERED_QUEUE, true);
    }

    @Bean
    public Queue notificationDeliveryQueue() {
        return new Queue(NOTIFICATION_DELIVERY_QUEUE, true);
    }

    @Bean
    public Queue statusChangedQueue() {
        return new Queue(STATUS_CHANGED_QUEUE, true);
    }

    @Bean
    public Queue exceptionRaisedQueue() {
        return new Queue(EXCEPTION_RAISED_QUEUE, true);
    }

    @Bean
    public Binding notificationDeliveryBinding(Queue notificationDeliveryQueue, TopicExchange smartcourierExchange) {
        return BindingBuilder.bind(notificationDeliveryQueue)
                .to(smartcourierExchange)
                .with(NOTIFICATION_DELIVERY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
