package com.smartcourier.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private UUID id;
    private String deliveryNumber;
    private String trackingNumber;
    private UUID customerId;
    private String serviceType;
    private String status;
    private String exceptionStatus;
    private String paymentStatus;
    private BigDecimal quotedAmount;
    private BigDecimal finalAmount;
    private String currencyCode;
    private AddressDto senderAddress;
    private AddressDto receiverAddress;
    private List<PackageDto> packages;
    private String notes;
    private LocalDateTime scheduledPickupAt;
    private LocalDateTime bookedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}
