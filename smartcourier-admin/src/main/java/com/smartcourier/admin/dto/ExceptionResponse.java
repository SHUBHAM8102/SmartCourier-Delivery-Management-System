package com.smartcourier.admin.dto;

import com.smartcourier.admin.entity.DeliveryException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    private UUID id;
    private UUID deliveryId;
    private DeliveryException.ExceptionType exceptionType;
    private DeliveryException.Severity severity;
    private String title;
    private String description;
    private DeliveryException.ExceptionStatus status;
    private LocalDateTime raisedAt;
    private UUID raisedBy;
    private LocalDateTime resolvedAt;
    private UUID resolvedBy;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
