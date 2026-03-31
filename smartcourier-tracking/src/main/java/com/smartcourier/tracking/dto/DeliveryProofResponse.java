package com.smartcourier.tracking.dto;

import com.smartcourier.tracking.entity.DeliveryProof;
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
public class DeliveryProofResponse {
    private UUID id;
    private UUID deliveryId;
    private DeliveryProof.ProofType proofType;
    private String receiverName;
    private String receiverContact;
    private String proofFileUrl;
    private Boolean otpVerified;
    private LocalDateTime deliveredAt;
    private UUID capturedBy;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
