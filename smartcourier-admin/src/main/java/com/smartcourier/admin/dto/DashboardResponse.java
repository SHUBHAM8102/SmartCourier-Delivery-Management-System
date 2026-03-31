package com.smartcourier.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalDeliveries;
    private Long deliveredToday;
    private Long inTransit;
    private Long pendingDeliveries;
    private Long failedDeliveries;
    private Long delayedDeliveries;
    private Long returnedDeliveries;
    private Long openExceptions;
    private Long resolvedExceptions;
    private Long totalHubs;
    private Long activeHubs;
    private Map<String, Long> deliveryStatusBreakdown;
    private Map<String, Long> exceptionTypeBreakdown;
}
