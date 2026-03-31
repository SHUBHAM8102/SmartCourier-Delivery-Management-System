package com.smartcourier.admin.service;

import com.smartcourier.admin.dto.*;
import com.smartcourier.admin.entity.DeliveryException;
import com.smartcourier.admin.entity.ServiceLocation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
    DashboardResponse getDashboardStats();

    List<Map<String, Object>> getAllDeliveries();

    List<Map<String, Object>> getDeliveriesByStatus(String status);

    Map<String, Object> getDeliveryById(UUID id);

    Map<String, Object> resolveDelivery(UUID deliveryId, String status, String remarks);

    List<ExceptionResponse> getAllExceptions();

    List<ExceptionResponse> getExceptionsByStatus(DeliveryException.ExceptionStatus status);

    ExceptionResponse resolveException(UUID exceptionId, ResolveExceptionRequest request);

    List<ReportResponse> getAllReports();

    ReportResponse generateReport(ReportRequest request);

    List<HubDto> getAllHubs();

    HubDto createHub(HubDto hubDto);

    HubDto updateHub(UUID id, HubDto hubDto);
}
