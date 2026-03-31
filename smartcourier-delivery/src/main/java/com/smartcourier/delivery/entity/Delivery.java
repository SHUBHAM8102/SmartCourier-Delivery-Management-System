package com.smartcourier.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "deliveries", schema = "delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "delivery_number", nullable = false, unique = true, length = 30)
    private String deliveryNumber;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 40)
    private String trackingNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "exception_status", length = 30)
    private ExceptionStatus exceptionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "quoted_amount", precision = 12, scale = 2)
    private BigDecimal quotedAmount;

    @Column(name = "final_amount", precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    @Builder.Default
    private String currencyCode = "INR";

    @Column(name = "source_hub_id")
    private UUID sourceHubId;

    @Column(name = "destination_hub_id")
    private UUID destinationHubId;

    @Column(name = "scheduled_pickup_at")
    private LocalDateTime scheduledPickupAt;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Version
    @Column(name = "version_no", nullable = false)
    @Builder.Default
    private Integer versionNo = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Package> packages = new ArrayList<>();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryStatusHistory> statusHistory = new ArrayList<>();

    @OneToOne(mappedBy = "delivery", cascade = CascadeType.ALL)
    private PickupRequest pickupRequest;

    public enum ServiceType {
        DOMESTIC, EXPRESS, INTERNATIONAL
    }

    public enum DeliveryStatus {
        DRAFT, BOOKED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED
    }

    public enum ExceptionStatus {
        DELAYED, FAILED, RETURNED
    }

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }
}
