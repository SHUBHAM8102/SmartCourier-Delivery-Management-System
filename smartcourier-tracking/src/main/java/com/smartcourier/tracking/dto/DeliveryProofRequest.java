package com.smartcourier.tracking.dto;

import com.smartcourier.tracking.entity.DeliveryProof;
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
public class DeliveryProofRequest {

    @NotNull(message = "Delivery ID is required")
    private UUID deliveryId;

    @NotNull(message = "Proof type is required")
    private DeliveryProof.ProofType proofType;

    private String receiverName;
    private String receiverContact;
    private String proofFileUrl;
    private Boolean otpVerified;
    private LocalDateTime deliveredAt;
    private UUID capturedBy;
    private Map<String, Object> metadata;
}
