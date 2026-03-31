package com.smartcourier.tracking.dto;

import com.smartcourier.tracking.entity.TrackingEvent;
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
public class TrackingEventResponse {
    private UUID id;
    private UUID deliveryId;
    private String trackingNumber;
    private TrackingEvent.EventCode eventCode;
    private TrackingEvent.EventStatus eventStatus;
    private LocalDateTime eventTime;
    private UUID locationHubId;
    private String locationText;
    private String remarks;
    private UUID createdBy;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
