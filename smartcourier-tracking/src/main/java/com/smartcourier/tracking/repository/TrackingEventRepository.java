package com.smartcourier.tracking.repository;

import com.smartcourier.tracking.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, UUID> {
    List<TrackingEvent> findByDeliveryIdOrderByEventTimeDesc(UUID deliveryId);
    List<TrackingEvent> findByTrackingNumberOrderByEventTimeDesc(String trackingNumber);
}
