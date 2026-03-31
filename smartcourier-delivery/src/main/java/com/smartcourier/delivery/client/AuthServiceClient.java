package com.smartcourier.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${feign.client.config.auth-service.url:http://auth-service:8081}")
public interface AuthServiceClient {

    @GetMapping("/auth/users/{userId}/email")
    Map<String, String> getUserEmail(@PathVariable("userId") String userId);
}
