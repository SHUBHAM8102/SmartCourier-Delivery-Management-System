package com.smartcourier.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryServiceClient {

    @GetMapping("/deliveries")
    ResponseEntity<List<Map<String, Object>>> getAllDeliveries();

    @GetMapping("/deliveries/{id}")
    ResponseEntity<Map<String, Object>> getDeliveryById(@PathVariable("id") UUID id);

    @GetMapping("/deliveries/status/{status}")
    ResponseEntity<List<Map<String, Object>>> getDeliveriesByStatus(@PathVariable("status") String status);

    @PutMapping("/deliveries/{id}/status")
    ResponseEntity<Map<String, Object>> updateDeliveryStatus(
            @PathVariable("id") UUID id,
            @RequestParam("status") String status,
            @RequestParam(value = "remarks", required = false) String remarks);
}
