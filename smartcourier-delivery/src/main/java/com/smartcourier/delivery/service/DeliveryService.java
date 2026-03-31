package com.smartcourier.delivery.service;

import com.smartcourier.delivery.dto.CreateDeliveryRequest;
import com.smartcourier.delivery.dto.DeliveryResponse;
import com.smartcourier.delivery.dto.UpdateStatusRequest;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    DeliveryResponse createDelivery(CreateDeliveryRequest request, UUID customerId);
    DeliveryResponse getDeliveryById(UUID id);
    DeliveryResponse getDeliveryByTrackingNumber(String trackingNumber);
    List<DeliveryResponse> getCustomerDeliveries(UUID customerId);
    List<DeliveryResponse> getAllDeliveries();
    List<DeliveryResponse> getDeliveriesByStatus(String status);
    DeliveryResponse updateStatus(UUID id, UpdateStatusRequest request, UUID changedBy, String changeSource);
}
