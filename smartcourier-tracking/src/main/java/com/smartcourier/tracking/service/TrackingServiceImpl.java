package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.*;
import com.smartcourier.tracking.entity.DeliveryProof;
import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.exception.TrackingException;
import com.smartcourier.tracking.repository.DeliveryProofRepository;
import com.smartcourier.tracking.repository.DocumentRepository;
import com.smartcourier.tracking.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final TrackingEventRepository trackingEventRepository;
    private final DocumentRepository documentRepository;
    private final DeliveryProofRepository deliveryProofRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public TrackingEventResponse createTrackingEvent(TrackingEventRequest request) {
        TrackingEvent event = TrackingEvent.builder()
                .deliveryId(request.getDeliveryId())
                .trackingNumber(request.getTrackingNumber())
                .eventCode(request.getEventCode())
                .eventStatus(request.getEventStatus())
                .eventTime(request.getEventTime())
                .locationHubId(request.getLocationHubId())
                .locationText(request.getLocationText())
                .remarks(request.getRemarks())
                .createdBy(request.getCreatedBy())
                .metadata(request.getMetadata())
                .build();

        TrackingEvent saved = trackingEventRepository.save(event);
        
        return mapToEventResponse(saved);
    }

    @Override
    public List<TrackingEventResponse> getEventsByDeliveryId(UUID deliveryId) {
        return trackingEventRepository.findByDeliveryIdOrderByEventTimeDesc(deliveryId)
                .stream()
                .map(this::mapToEventResponse)
                .toList();
    }

    @Override
    public TrackingResponse getTrackingByNumber(String trackingNumber) {
        List<TrackingEvent> events = trackingEventRepository.findByTrackingNumberOrderByEventTimeDesc(trackingNumber);
        
        if (events.isEmpty()) {
            throw new TrackingException("No tracking events found for tracking number: " + trackingNumber);
        }

        TrackingEvent latestEvent = events.get(0);
        
        List<DocumentResponse> documents = documentRepository.findByDeliveryId(latestEvent.getDeliveryId())
                .stream()
                .map(this::mapToDocumentResponse)
                .toList();

        List<DeliveryProofResponse> proofs = deliveryProofRepository.findByDeliveryId(latestEvent.getDeliveryId())
                .stream()
                .map(this::mapToProofResponse)
                .toList();

        DeliveryProofResponse deliveryProof = proofs.isEmpty() ? null : proofs.get(0);

        return TrackingResponse.builder()
                .trackingNumber(trackingNumber)
                .deliveryId(latestEvent.getDeliveryId())
                .currentStatus(latestEvent.getEventCode().name())
                .currentLocation(latestEvent.getLocationText())
                .timeline(events.stream().map(this::mapToEventResponse).toList())
                .documents(documents)
                .deliveryProof(deliveryProof)
                .build();
    }

    private TrackingEventResponse mapToEventResponse(TrackingEvent event) {
        return TrackingEventResponse.builder()
                .id(event.getId())
                .deliveryId(event.getDeliveryId())
                .trackingNumber(event.getTrackingNumber())
                .eventCode(event.getEventCode())
                .eventStatus(event.getEventStatus())
                .eventTime(event.getEventTime())
                .locationHubId(event.getLocationHubId())
                .locationText(event.getLocationText())
                .remarks(event.getRemarks())
                .createdBy(event.getCreatedBy())
                .metadata(event.getMetadata())
                .createdAt(event.getCreatedAt())
                .build();
    }

    private com.smartcourier.tracking.dto.DocumentResponse mapToDocumentResponse(com.smartcourier.tracking.entity.Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .deliveryId(document.getDeliveryId())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileMimeType(document.getFileMimeType())
                .fileSizeBytes(document.getFileSizeBytes())
                .uploadedBy(document.getUploadedBy())
                .checksumSha256(document.getChecksumSha256())
                .metadata(document.getMetadata())
                .createdAt(document.getCreatedAt())
                .build();
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
