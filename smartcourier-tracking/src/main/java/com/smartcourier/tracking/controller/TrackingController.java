package com.smartcourier.tracking.controller;

import com.smartcourier.tracking.dto.*;
import com.smartcourier.tracking.service.DeliveryProofService;
import com.smartcourier.tracking.service.DocumentService;
import com.smartcourier.tracking.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Tracking Management APIs")
public class TrackingController {

    private final TrackingService trackingService;
    private final DocumentService documentService;
    private final DeliveryProofService deliveryProofService;

    @GetMapping("/{trackingNumber}")
    @Operation(summary = "Get tracking by tracking number", description = "Returns full tracking information for a shipment")
    public ResponseEntity<TrackingResponse> getTrackingByNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(trackingService.getTrackingByNumber(trackingNumber));
    }

    @GetMapping("/events/{deliveryId}")
    @Operation(summary = "Get tracking events by delivery ID", description = "Returns all tracking events for a delivery")
    public ResponseEntity<List<TrackingEventResponse>> getEventsByDeliveryId(@PathVariable UUID deliveryId) {
        return ResponseEntity.ok(trackingService.getEventsByDeliveryId(deliveryId));
    }

    @PostMapping("/events")
    @Operation(summary = "Create tracking event", description = "Creates a new tracking event for a delivery")
    public ResponseEntity<TrackingEventResponse> createTrackingEvent(
            @Valid @RequestBody TrackingEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackingService.createTrackingEvent(request));
    }

    @PostMapping(value = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Uploads a document for a delivery (invoice, label, ID proof)")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("deliveryId") UUID deliveryId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID uploadedBy = userId != null ? UUID.fromString(userId) : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadDocument(deliveryId, documentType, file, uploadedBy));
    }

    @GetMapping("/documents/{deliveryId}")
    @Operation(summary = "Get documents by delivery ID", description = "Returns all documents for a delivery")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByDeliveryId(@PathVariable UUID deliveryId) {
        return ResponseEntity.ok(documentService.getDocumentsByDeliveryId(deliveryId));
    }

    @PostMapping("/proof")
    @Operation(summary = "Create delivery proof", description = "Creates delivery proof (signature, photo)")
    public ResponseEntity<DeliveryProofResponse> createDeliveryProof(
            @Valid @RequestBody DeliveryProofRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryProofService.createDeliveryProof(request));
    }

    @GetMapping("/proof/{deliveryId}")
    @Operation(summary = "Get delivery proofs by delivery ID", description = "Returns all delivery proofs for a delivery")
    public ResponseEntity<List<DeliveryProofResponse>> getProofsByDeliveryId(@PathVariable UUID deliveryId) {
        return ResponseEntity.ok(deliveryProofService.getProofsByDeliveryId(deliveryId));
    }
}
