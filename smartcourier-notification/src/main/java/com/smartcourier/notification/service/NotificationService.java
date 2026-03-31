package com.smartcourier.notification.service;

import com.smartcourier.notification.entity.EmailLog;
import com.smartcourier.notification.dto.EmailLogDto;

import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    EmailLogDto sendEmail(String to, String subject, String body, Map<String, Object> metadata);
    EmailLogDto sendRegistrationEmail(String email, String name);
    EmailLogDto sendDeliveryCreatedEmail(String email, String trackingNumber);
    EmailLogDto sendStatusChangedEmail(String email, String trackingNumber, String status);
    EmailLogDto sendExceptionEmail(String email, String trackingNumber, String exceptionMessage);
}
