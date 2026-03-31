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
public class ExceptionRaisedListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.EXCEPTION_RAISED_QUEUE)
    public void handleExceptionRaised(Map<String, Object> message) {
        log.info("Received exception.raised event: {}", message);
        
        String email = (String) message.get("email");
        String trackingNumber = (String) message.get("trackingNumber");
        String exceptionMessage = (String) message.get("exceptionMessage");
        
        if (email != null && trackingNumber != null) {
            notificationService.sendExceptionEmail(email, trackingNumber, exceptionMessage != null ? exceptionMessage : "Unknown exception");
        } else {
            log.warn("Email or tracking number not found in exception.raised message");
        }
    }
}
