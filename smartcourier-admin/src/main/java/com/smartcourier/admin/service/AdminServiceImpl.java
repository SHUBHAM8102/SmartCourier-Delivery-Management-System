package com.smartcourier.admin.service;

import com.smartcourier.admin.client.DeliveryServiceClient;
import com.smartcourier.admin.dto.*;
import com.smartcourier.admin.entity.*;
import com.smartcourier.admin.exception.AdminException;
import com.smartcourier.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DeliveryServiceClient deliveryServiceClient;
    private final DeliveryExceptionRepository deliveryExceptionRepository;
    private final ReportDefinitionRepository reportDefinitionRepository;
    private final ReportRunRepository reportRunRepository;
    private final ServiceLocationRepository serviceLocationRepository;

    @Override
    public DashboardResponse getDashboardStats() {
        Map<String, Long> statusBreakdown = new HashMap<>();
        Map<String, Long> exceptionTypeBreakdown = new HashMap<>();

        try {
            ResponseEntity<List<Map<String, Object>>> response = deliveryServiceClient.getAllDeliveries();
            if (response.getBody() != null) {
                List<Map<String, Object>> deliveries = response.getBody();
                statusBreakdown = deliveries.stream()
                        .collect(Collectors.groupingBy(
                                d -> String.valueOf(d.getOrDefault("status", "UNKNOWN")),
                                Collectors.counting()
                        ));
            }
        } catch (Exception e) {
            // Log error and continue with empty status breakdown
        }

        long totalDeliveries = statusBreakdown.values().stream().mapToLong(Long::longValue).sum();
        long deliveredToday = 0;
        long inTransit = 0;
        long pendingDeliveries = 0;

        if (statusBreakdown.containsKey("DELIVERED")) {
            deliveredToday = statusBreakdown.get("DELIVERED");
        }
        if (statusBreakdown.containsKey("IN_TRANSIT")) {
            inTransit = statusBreakdown.get("IN_TRANSIT");
        }
        if (statusBreakdown.containsKey("BOOKED") || statusBreakdown.containsKey("PICKED_UP")) {
            pendingDeliveries = statusBreakdown.getOrDefault("BOOKED", 0L) + 
                    statusBreakdown.getOrDefault("PICKED_UP", 0L);
        }

        long delayedCount = deliveryExceptionRepository.countByExceptionType(DeliveryException.ExceptionType.DELAYED);
        long failedCount = deliveryExceptionRepository.countByExceptionType(DeliveryException.ExceptionType.FAILED);
        long returnedCount = deliveryExceptionRepository.countByExceptionType(DeliveryException.ExceptionType.RETURNED);

        long openExceptions = deliveryExceptionRepository.countByStatus(DeliveryException.ExceptionStatus.OPEN) +
                deliveryExceptionRepository.countByStatus(DeliveryException.ExceptionStatus.IN_PROGRESS);
        long resolvedExceptions = deliveryExceptionRepository.countByStatus(DeliveryException.ExceptionStatus.RESOLVED) +
                deliveryExceptionRepository.countByStatus(DeliveryException.ExceptionStatus.CLOSED);

        for (DeliveryException.ExceptionType type : DeliveryException.ExceptionType.values()) {
            exceptionTypeBreakdown.put(type.name(), deliveryExceptionRepository.countByExceptionType(type));
        }

        long totalHubs = serviceLocationRepository.count();
        long activeHubs = serviceLocationRepository.countByLocationTypeAndIsActive(ServiceLocation.LocationType.HUB);

        return DashboardResponse.builder()
                .totalDeliveries(totalDeliveries)
                .deliveredToday(deliveredToday)
                .inTransit(inTransit)
                .pendingDeliveries(pendingDeliveries)
                .failedDeliveries(failedCount)
                .delayedDeliveries(delayedCount)
                .returnedDeliveries(returnedCount)
                .openExceptions(openExceptions)
                .resolvedExceptions(resolvedExceptions)
                .totalHubs(totalHubs)
                .activeHubs(activeHubs)
                .deliveryStatusBreakdown(statusBreakdown)
                .exceptionTypeBreakdown(exceptionTypeBreakdown)
                .build();
    }

    @Override
    public List<Map<String, Object>> getAllDeliveries() {
        ResponseEntity<List<Map<String, Object>>> response = deliveryServiceClient.getAllDeliveries();
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getDeliveriesByStatus(String status) {
        ResponseEntity<List<Map<String, Object>>> response = deliveryServiceClient.getDeliveriesByStatus(status);
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    @Override
    public Map<String, Object> getDeliveryById(UUID id) {
        ResponseEntity<Map<String, Object>> response = deliveryServiceClient.getDeliveryById(id);
        if (response.getBody() == null) {
            throw new AdminException("Delivery not found with id: " + id);
        }
        return response.getBody();
    }

    @Override
    @Transactional
    public Map<String, Object> resolveDelivery(UUID deliveryId, String status, String remarks) {
        ResponseEntity<Map<String, Object>> response = deliveryServiceClient.updateDeliveryStatus(deliveryId, status, remarks);
        if (response.getBody() == null) {
            throw new AdminException("Failed to update delivery status for id: " + deliveryId);
        }
        return response.getBody();
    }

    @Override
    public List<ExceptionResponse> getAllExceptions() {
        return deliveryExceptionRepository.findAll().stream()
                .map(this::toExceptionResponse)
                .toList();
    }

    @Override
    public List<ExceptionResponse> getExceptionsByStatus(DeliveryException.ExceptionStatus status) {
        return deliveryExceptionRepository.findByStatus(status).stream()
                .map(this::toExceptionResponse)
                .toList();
    }

    @Override
    @Transactional
    public ExceptionResponse resolveException(UUID exceptionId, ResolveExceptionRequest request) {
        DeliveryException exception = deliveryExceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new AdminException("Exception not found with id: " + exceptionId));

        exception.setStatus(DeliveryException.ExceptionStatus.RESOLVED);
        exception.setResolvedAt(LocalDateTime.now());
        exception.setResolvedBy(request.getResolvedBy());
        exception.setResolutionNotes(request.getResolutionNotes());

        deliveryExceptionRepository.save(exception);

        return toExceptionResponse(exception);
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportRunRepository.findAll().stream()
                .map(this::toReportResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReportResponse generateReport(ReportRequest request) {
        ReportDefinition definition = reportDefinitionRepository.findByReportCode(request.getReportCode())
                .orElseThrow(() -> new AdminException("Report definition not found: " + request.getReportCode()));

        ReportRun reportRun = ReportRun.builder()
                .reportDefinition(definition)
                .requestedBy(request.getRequestedBy())
                .filters(request.getFilters())
                .outputFormat(request.getOutputFormat())
                .status(ReportRun.ReportStatus.COMPLETED)
                .generatedAt(LocalDateTime.now())
                .outputUrl("/reports/" + UUID.randomUUID())
                .build();

        reportRunRepository.save(reportRun);

        return toReportResponse(reportRun);
    }

    @Override
    public List<HubDto> getAllHubs() {
        return serviceLocationRepository.findAll().stream()
                .map(this::toHubDto)
                .toList();
    }

    @Override
    @Transactional
    public HubDto createHub(HubDto hubDto) {
        if (serviceLocationRepository.existsByLocationCode(hubDto.getLocationCode())) {
            throw new AdminException("Hub already exists with code: " + hubDto.getLocationCode());
        }

        ServiceLocation location = ServiceLocation.builder()
                .locationCode(hubDto.getLocationCode())
                .locationName(hubDto.getLocationName())
                .locationType(hubDto.getLocationType())
                .city(hubDto.getCity())
                .state(hubDto.getState())
                .countryCode(hubDto.getCountryCode())
                .isActive(hubDto.getIsActive())
                .build();

        serviceLocationRepository.save(location);
        return toHubDto(location);
    }

    @Override
    @Transactional
    public HubDto updateHub(UUID id, HubDto hubDto) {
        ServiceLocation location = serviceLocationRepository.findById(id)
                .orElseThrow(() -> new AdminException("Hub not found with id: " + id));

        location.setLocationCode(hubDto.getLocationCode());
        location.setLocationName(hubDto.getLocationName());
        location.setLocationType(hubDto.getLocationType());
        location.setCity(hubDto.getCity());
        location.setState(hubDto.getState());
        location.setCountryCode(hubDto.getCountryCode());
        location.setIsActive(hubDto.getIsActive());

        serviceLocationRepository.save(location);
        return toHubDto(location);
    }

    private ExceptionResponse toExceptionResponse(DeliveryException exception) {
        return ExceptionResponse.builder()
                .id(exception.getId())
                .deliveryId(exception.getDeliveryId())
                .exceptionType(exception.getExceptionType())
                .severity(exception.getSeverity())
                .title(exception.getTitle())
                .description(exception.getDescription())
                .status(exception.getStatus())
                .raisedAt(exception.getRaisedAt())
                .raisedBy(exception.getRaisedBy())
                .resolvedAt(exception.getResolvedAt())
                .resolvedBy(exception.getResolvedBy())
                .resolutionNotes(exception.getResolutionNotes())
                .createdAt(exception.getCreatedAt())
                .updatedAt(exception.getUpdatedAt())
                .build();
    }

    private ReportResponse toReportResponse(ReportRun reportRun) {
        return ReportResponse.builder()
                .id(reportRun.getId())
                .reportDefinitionId(reportRun.getReportDefinition().getId())
                .reportCode(reportRun.getReportDefinition().getReportCode())
                .reportName(reportRun.getReportDefinition().getReportName())
                .requestedBy(reportRun.getRequestedBy())
                .outputFormat(reportRun.getOutputFormat())
                .outputUrl(reportRun.getOutputUrl())
                .status(reportRun.getStatus())
                .generatedAt(reportRun.getGeneratedAt())
                .createdAt(reportRun.getCreatedAt())
                .build();
    }

    private HubDto toHubDto(ServiceLocation location) {
        return HubDto.builder()
                .id(location.getId())
                .locationCode(location.getLocationCode())
                .locationName(location.getLocationName())
                .locationType(location.getLocationType())
                .city(location.getCity())
                .state(location.getState())
                .countryCode(location.getCountryCode())
                .isActive(location.getIsActive())
                .build();
    }
}
