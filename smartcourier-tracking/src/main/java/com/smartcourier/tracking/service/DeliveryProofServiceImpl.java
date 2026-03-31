package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.DeliveryProofRequest;
import com.smartcourier.tracking.dto.DeliveryProofResponse;
import com.smartcourier.tracking.entity.DeliveryProof;
import com.smartcourier.tracking.repository.DeliveryProofRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryProofServiceImpl implements DeliveryProofService {

    private final DeliveryProofRepository deliveryProofRepository;

    @Override
    @Transactional
    public DeliveryProofResponse createDeliveryProof(DeliveryProofRequest request) {
        DeliveryProof proof = DeliveryProof.builder()
                .deliveryId(request.getDeliveryId())
                .proofType(request.getProofType())
                .receiverName(request.getReceiverName())
                .receiverContact(request.getReceiverContact())
                .proofFileUrl(request.getProofFileUrl())
                .otpVerified(Boolean.TRUE.equals(request.getOtpVerified()))
                .deliveredAt(request.getDeliveredAt() != null ? request.getDeliveredAt() : LocalDateTime.now())
                .capturedBy(request.getCapturedBy())
                .metadata(request.getMetadata())
                .build();

        DeliveryProof saved = deliveryProofRepository.save(proof);
        return mapToProofResponse(saved);
    }

    @Override
    public List<DeliveryProofResponse> getProofsByDeliveryId(UUID deliveryId) {
        return deliveryProofRepository.findByDeliveryId(deliveryId)
                .stream()
                .map(this::mapToProofResponse)
                .toList();
    }

    private DeliveryProofResponse mapToProofResponse(DeliveryProof proof) {
        return DeliveryProofResponse.builder()
                .id(proof.getId())
                .deliveryId(proof.getDeliveryId())
                .proofType(proof.getProofType())
                .receiverName(proof.getReceiverName())
                .receiverContact(proof.getReceiverContact())
                .proofFileUrl(proof.getProofFileUrl())
                .otpVerified(proof.getOtpVerified())
                .deliveredAt(proof.getDeliveredAt())
                .capturedBy(proof.getCapturedBy())
                .metadata(proof.getMetadata())
                .createdAt(proof.getCreatedAt())
                .build();
    }
}
