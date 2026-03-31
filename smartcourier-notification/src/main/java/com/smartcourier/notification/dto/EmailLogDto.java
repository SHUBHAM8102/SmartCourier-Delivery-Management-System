package com.smartcourier.notification.dto;

import com.smartcourier.notification.entity.EmailLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLogDto {
    private UUID id;
    private String recipientEmail;
    private String subject;
    private String body;
    private EmailLog.EmailStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;

    public static EmailLogDto fromEntity(EmailLog emailLog) {
        return EmailLogDto.builder()
                .id(emailLog.getId())
                .recipientEmail(emailLog.getRecipientEmail())
                .subject(emailLog.getSubject())
                .body(emailLog.getBody())
                .status(emailLog.getStatus())
                .sentAt(emailLog.getSentAt())
                .errorMessage(emailLog.getErrorMessage())
                .metadata(emailLog.getMetadata())
                .createdAt(emailLog.getCreatedAt())
                .build();
    }
}
