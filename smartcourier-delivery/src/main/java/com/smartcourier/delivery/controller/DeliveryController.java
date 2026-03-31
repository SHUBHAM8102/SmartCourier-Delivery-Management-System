package com.smartcourier.delivery.controller;

import com.smartcourier.delivery.dto.CreateDeliveryRequest;
import com.smartcourier.delivery.dto.DeliveryResponse;
import com.smartcourier.delivery.dto.UpdateStatusRequest;
import com.smartcourier.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "Delivery Management APIs")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @Operation(summary = "Create delivery", description = "Creates a new delivery request")
    public ResponseEntity<DeliveryResponse> createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.createDelivery(request, UUID.fromString(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery by ID", description = "Returns delivery details by ID")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable UUID id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get delivery by tracking number", description = "Returns delivery details by tracking number")
    public ResponseEntity<DeliveryResponse> getDeliveryByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(deliveryService.getDeliveryByTrackingNumber(trackingNumber));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my deliveries", description = "Returns all deliveries for the logged-in customer")
    public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(deliveryService.getCustomerDeliveries(UUID.fromString(userId)));
    }

    @GetMapping
    @Operation(summary = "Get all deliveries", description = "Returns all deliveries (Admin only)")
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
        }
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update delivery status", description = "Updates the status of a delivery")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        UUID changedBy = userId != null ? UUID.fromString(userId) : null;
        String changeSource = userRole != null ? userRole : "SYSTEM";
        return ResponseEntity.ok(deliveryService.updateStatus(id, request, changedBy, changeSource));
    }
}
