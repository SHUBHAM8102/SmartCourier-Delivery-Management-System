package com.smartcourier.notification.config;

import com.smartcourier.notification.entity.EmailLog;
import com.smartcourier.notification.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmailLogRepository emailLogRepository;

    @Override
    public void run(String... args) {
        if (emailLogRepository.count() == 0) {
            log.info("Seeding test email logs...");
            
            List<EmailLog> testLogs = List.of(
                EmailLog.builder()
                    .recipientEmail("john.doe@example.com")
                    .subject("Welcome to SmartCourier!")
                    .body("Dear John, Welcome to SmartCourier Delivery Management System!")
                    .status(EmailLog.EmailStatus.SENT)
                    .sentAt(LocalDateTime.now().minusDays(2))
                    .metadata(Map.of("type", "REGISTRATION", "name", "John Doe"))
                    .build(),
                EmailLog.builder()
                    .recipientEmail("jane.smith@example.com")
                    .subject("Delivery Created - SC123456789")
                    .body("Your shipment has been created successfully. Tracking Number: SC123456789")
                    .status(EmailLog.EmailStatus.SENT)
                    .sentAt(LocalDateTime.now().minusDays(1))
                    .metadata(Map.of("type", "DELIVERY_CREATED", "trackingNumber", "SC123456789"))
                    .build(),
                EmailLog.builder()
                    .recipientEmail("john.doe@example.com")
                    .subject("Delivery Status Updated - SC123456789")
                    .body("Your shipment status has been updated. Current Status: In Transit")
                    .status(EmailLog.EmailStatus.SENT)
                    .sentAt(LocalDateTime.now().minusHours(6))
                    .metadata(Map.of("type", "STATUS_CHANGED", "trackingNumber", "SC123456789", "status", "IN_TRANSIT"))
                    .build(),
                EmailLog.builder()
                    .recipientEmail("support@example.com")
                    .subject("Delivery Exception Alert - SC987654321")
                    .body("An exception has occurred with your shipment. Exception: Address not found")
                    .status(EmailLog.EmailStatus.SENT)
                    .sentAt(LocalDateTime.now().minusHours(2))
                    .metadata(Map.of("type", "EXCEPTION", "trackingNumber", "SC987654321", "exception", "Address not found"))
                    .build(),
                EmailLog.builder()
                    .recipientEmail("test@example.com")
                    .subject("Test Email")
                    .body("This is a test email")
                    .status(EmailLog.EmailStatus.FAILED)
                    .errorMessage("SMTP connection refused")
                    .metadata(Map.of("type", "TEST"))
                    .build()
            );

            emailLogRepository.saveAll(testLogs);
            log.info("Seeded {} test email logs", testLogs.size());
        }
    }
}
