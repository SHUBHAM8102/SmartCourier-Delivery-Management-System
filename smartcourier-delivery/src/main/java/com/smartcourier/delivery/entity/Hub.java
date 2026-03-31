package com.smartcourier.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hubs", schema = "delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hub_code", nullable = false, unique = true, length = 20)
    private String hubCode;

    @Column(name = "hub_name", nullable = false, length = 120)
    private String hubName;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(length = 120)
    private String state;

    @Column(name = "country_code", nullable = false, length = 2)
    @Builder.Default
    private String countryCode = "IN";

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
