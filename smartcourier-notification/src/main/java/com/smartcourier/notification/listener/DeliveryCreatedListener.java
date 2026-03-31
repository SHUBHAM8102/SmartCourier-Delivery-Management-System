package com.smartcourier.notification.listener;

import com.smartcourier.notification.config.RabbitMQConfig;
import com.smartcourier.notification.dto.EmailLogDto;
import com.smartcourier.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCreatedListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_DELIVERY_QUEUE)
    public void handleDeliveryCreated(Map<String, Object> message) {
        log.info("Received notification.delivery.created event: {}", message);
        
        String email = (String) message.get("email");
        String trackingNumber = (String) message.get("trackingNumber");
        
        if (email != null && trackingNumber != null) {
            notificationService.sendDeliveryCreatedEmail(email, trackingNumber);
        } else {
            log.warn("Email or tracking number not found in notification message");
        }
    }
}
