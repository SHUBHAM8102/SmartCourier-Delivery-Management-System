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
public class UserRegisteredListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTERED_QUEUE)
    public void handleUserRegistered(Map<String, Object> message) {
        log.info("Received user.registered event: {}", message);
        
        String email = (String) message.get("email");
        String name = (String) message.get("name");
        
        if (email != null) {
            notificationService.sendRegistrationEmail(email, name != null ? name : "User");
        } else {
            log.warn("Email not found in user.registered message");
        }
    }
}
