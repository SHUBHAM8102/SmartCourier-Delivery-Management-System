package com.smartcourier.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {
    private String trackingNumber;
    private UUID deliveryId;
    private String currentStatus;
    private String currentLocation;
    private List<TrackingEventResponse> timeline;
    private List<DocumentResponse> documents;
    private DeliveryProofResponse deliveryProof;
}
