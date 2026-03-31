package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.TrackingEventRequest;
import com.smartcourier.tracking.dto.TrackingEventResponse;
import com.smartcourier.tracking.dto.TrackingResponse;

import java.util.List;
import java.util.UUID;

public interface TrackingService {
    TrackingEventResponse createTrackingEvent(TrackingEventRequest request);
    List<TrackingEventResponse> getEventsByDeliveryId(UUID deliveryId);
    TrackingResponse getTrackingByNumber(String trackingNumber);
}
