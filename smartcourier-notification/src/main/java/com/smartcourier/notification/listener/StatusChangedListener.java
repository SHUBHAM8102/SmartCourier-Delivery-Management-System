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
public class StatusChangedListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.STATUS_CHANGED_QUEUE)
    public void handleStatusChanged(Map<String, Object> message) {
        log.info("Received status.changed event: {}", message);
        
        String email = (String) message.get("email");
        String trackingNumber = (String) message.get("trackingNumber");
        String status = (String) message.get("status");
        
        if (email != null && trackingNumber != null) {
            notificationService.sendStatusChangedEmail(email, trackingNumber, status != null ? status : "Updated");
        } else {
            log.warn("Email or tracking number not found in status.changed message");
        }
    }
}
