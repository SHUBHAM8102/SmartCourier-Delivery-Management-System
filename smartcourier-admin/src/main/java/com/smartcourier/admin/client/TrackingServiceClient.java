package com.smartcourier.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {

    @GetMapping("/tracking/events/{deliveryId}")
    ResponseEntity<List<Map<String, Object>>> getTrackingHistory(@PathVariable("deliveryId") UUID deliveryId);
}
