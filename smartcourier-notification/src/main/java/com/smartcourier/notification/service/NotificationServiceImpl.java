package com.smartcourier.notification.service;

import com.smartcourier.notification.dto.EmailLogDto;
import com.smartcourier.notification.entity.EmailLog;
import com.smartcourier.notification.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final String TRACKING_NUMBER_KEY = "trackingNumber";
    private static final String EMAIL_SIGNATURE = """
            Best regards,
            SmartCourier Team""";

    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;

    @Override
    public EmailLogDto sendEmail(String to, String subject, String body, Map<String, Object> metadata) {
        EmailLog emailLog = EmailLog.builder()
                .recipientEmail(to)
                .subject(subject)
                .body(body)
                .status(EmailLog.EmailStatus.PENDING)
                .metadata(metadata)
                .build();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            emailLog.setStatus(EmailLog.EmailStatus.SENT);
            emailLog.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            emailLog.setStatus(EmailLog.EmailStatus.FAILED);
            emailLog.setErrorMessage(e.getMessage());
            log.error("Failed to send email to: {}, error: {}", to, e.getMessage());
        }

        EmailLog saved = emailLogRepository.save(emailLog);
        return EmailLogDto.fromEntity(saved);
    }

    @Override
    public EmailLogDto sendRegistrationEmail(String email, String name) {
        String subject = "Welcome to SmartCourier!";
        String body = String.format("""
                Dear %s,

                Welcome to SmartCourier Delivery Management System!

                Your account has been successfully created.

                %s""", name, EMAIL_SIGNATURE);
        return sendEmail(email, subject, body, Map.of("type", "REGISTRATION", "name", name));
    }

    @Override
    public EmailLogDto sendDeliveryCreatedEmail(String email, String trackingNumber) {
        String subject = "Delivery Created - " + trackingNumber;
        String body = String.format("""
                Your shipment has been created successfully.

                Tracking Number: %s

                You can track your delivery using this number.

                %s""", trackingNumber, EMAIL_SIGNATURE);
        return sendEmail(email, subject, body, Map.of("type", "DELIVERY_CREATED", TRACKING_NUMBER_KEY, trackingNumber));
    }

    @Override
    public EmailLogDto sendStatusChangedEmail(String email, String trackingNumber, String status) {
        String subject = "Delivery Status Updated - " + trackingNumber;
        String body = String.format("""
                Your shipment status has been updated.

                Tracking Number: %s
                Current Status: %s

                Track your delivery for more details.

                %s""", trackingNumber, status, EMAIL_SIGNATURE);
        return sendEmail(email, subject, body, Map.of("type", "STATUS_CHANGED", TRACKING_NUMBER_KEY, trackingNumber, "status", status));
    }

    @Override
    public EmailLogDto sendExceptionEmail(String email, String trackingNumber, String exceptionMessage) {
        String subject = "Delivery Exception Alert - " + trackingNumber;
        String body = String.format("""
                An exception has occurred with your shipment.

                Tracking Number: %s
                Exception: %s

                Please contact support for assistance.

                %s""", trackingNumber, exceptionMessage, EMAIL_SIGNATURE);
        return sendEmail(email, subject, body, Map.of("type", "EXCEPTION", TRACKING_NUMBER_KEY, trackingNumber, "exception", exceptionMessage));
    }
}
