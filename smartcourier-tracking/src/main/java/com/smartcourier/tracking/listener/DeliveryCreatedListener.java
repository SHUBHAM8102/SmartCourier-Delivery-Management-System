package com.smartcourier.tracking.listener;

import com.smartcourier.tracking.config.RabbitMQConfig;
import com.smartcourier.tracking.dto.TrackingEventRequest;
import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCreatedListener {

    private final TrackingService trackingService;

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_CREATED_QUEUE)
    public void handleDeliveryCreated(Map<String, Object> message) {
        log.info("Received delivery created message: {}", message);
        
        try {
            String trackingNumber = (String) message.get("trackingNumber");
            UUID deliveryId = UUID.fromString((String) message.get("deliveryId"));
            
            TrackingEventRequest request = TrackingEventRequest.builder()
                    .deliveryId(deliveryId)
                    .trackingNumber(trackingNumber)
                    .eventCode(TrackingEvent.EventCode.PICKUP_SCHEDULED)
                    .eventStatus(TrackingEvent.EventStatus.PENDING)
                    .eventTime(LocalDateTime.now())
                    .locationText("Shipment Created")
                    .build();
            
            trackingService.createTrackingEvent(request);
            log.info("Created initial tracking event for delivery: {}", deliveryId);
            
        } catch (Exception e) {
            log.error("Error processing delivery created message: {}", e.getMessage(), e);
        }
    }
}
