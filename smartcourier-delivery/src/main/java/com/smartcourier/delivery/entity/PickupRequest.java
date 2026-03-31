package com.smartcourier.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pickup_requests", schema = "delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false, unique = true)
    private Delivery delivery;

    @Column(name = "requested_pickup_at", nullable = false)
    private LocalDateTime requestedPickupAt;

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    @Column(name = "pickup_window_start")
    private LocalDateTime pickupWindowStart;

    @Column(name = "pickup_window_end")
    private LocalDateTime pickupWindowEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private PickupStatus status = PickupStatus.REQUESTED;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum PickupStatus {
        REQUESTED, ASSIGNED, PICKED_UP, CANCELLED
    }
}
