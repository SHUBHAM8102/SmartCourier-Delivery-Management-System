package com.smartcourier.tracking.dto;

import com.smartcourier.tracking.entity.TrackingEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TrackingEventRequest {

    @NotNull(message = "Delivery ID is required")
    private UUID deliveryId;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotNull(message = "Event code is required")
    private TrackingEvent.EventCode eventCode;

    @NotNull(message = "Event status is required")
    private TrackingEvent.EventStatus eventStatus;

    @NotNull(message = "Event time is required")
    private LocalDateTime eventTime;

    private UUID locationHubId;
    private String locationText;
    private String remarks;
    private UUID createdBy;
    private Map<String, Object> metadata;
}
