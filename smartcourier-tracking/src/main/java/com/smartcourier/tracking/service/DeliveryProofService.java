package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.DeliveryProofRequest;
import com.smartcourier.tracking.dto.DeliveryProofResponse;

import java.util.List;
import java.util.UUID;

public interface DeliveryProofService {
    DeliveryProofResponse createDeliveryProof(DeliveryProofRequest request);
    List<DeliveryProofResponse> getProofsByDeliveryId(UUID deliveryId);
}
