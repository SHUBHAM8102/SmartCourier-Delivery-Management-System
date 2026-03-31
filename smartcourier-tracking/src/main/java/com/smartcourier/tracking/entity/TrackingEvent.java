package com.smartcourier.tracking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tracking_events", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "tracking_number", nullable = false, length = 40)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_code", nullable = false, length = 50)
    private EventCode eventCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false, length = 30)
    private EventStatus eventStatus;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "location_hub_id")
    private UUID locationHubId;

    @Column(name = "location_text", length = 255)
    private String locationText;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_by")
    private UUID createdBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum EventCode {
        PICKUP_SCHEDULED,
        PICKED_UP,
        IN_TRANSIT,
        ARRIVED_AT_HUB,
        DEPARTED_FROM_HUB,
        OUT_FOR_DELIVERY,
        DELIVERY_ATTEMPTED,
        DELIVERED,
        DELIVERY_FAILED,
        RETURNED_TO_SENDER,
        ON_HOLD,
        CUSTOMER_NOT_AVAILABLE
    }

    public enum EventStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
