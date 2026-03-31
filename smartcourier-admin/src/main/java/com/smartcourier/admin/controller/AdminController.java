package com.smartcourier.admin.controller;

import com.smartcourier.admin.dto.*;
import com.smartcourier.admin.entity.DeliveryException;
import com.smartcourier.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for admin operations")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Returns KPI counts and monitoring data")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/deliveries")
    @Operation(summary = "Get all deliveries", description = "Retrieve all deliveries from delivery service")
    public ResponseEntity<List<Map<String, Object>>> getAllDeliveries() {
        return ResponseEntity.ok(adminService.getAllDeliveries());
    }

    @GetMapping("/deliveries/{id}")
    @Operation(summary = "Get delivery by ID", description = "Retrieve a specific delivery by ID")
    public ResponseEntity<Map<String, Object>> getDeliveryById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getDeliveryById(id));
    }

    @PutMapping("/deliveries/{id}/resolve")
    @Operation(summary = "Resolve delivery", description = "Update delivery status to resolve an issue")
    public ResponseEntity<Map<String, Object>> resolveDelivery(
            @PathVariable UUID id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(adminService.resolveDelivery(id, status, remarks));
    }

    @GetMapping("/exceptions")
    @Operation(summary = "Get all exceptions", description = "Retrieve all delivery exceptions")
    public ResponseEntity<List<ExceptionResponse>> getAllExceptions(
            @RequestParam(required = false) DeliveryException.ExceptionStatus status) {
        if (status != null) {
            return ResponseEntity.ok(adminService.getExceptionsByStatus(status));
        }
        return ResponseEntity.ok(adminService.getAllExceptions());
    }

    @PutMapping("/exceptions/{id}/resolve")
    @Operation(summary = "Resolve exception", description = "Mark an exception as resolved")
    public ResponseEntity<ExceptionResponse> resolveException(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveExceptionRequest request) {
        return ResponseEntity.ok(adminService.resolveException(id, request));
    }

    @GetMapping("/reports")
    @Operation(summary = "Get all reports", description = "Retrieve all generated reports")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(adminService.getAllReports());
    }

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate report", description = "Generate a new report based on definition")
    public ResponseEntity<ReportResponse> generateReport(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(adminService.generateReport(request));
    }

    @GetMapping("/hubs")
    @Operation(summary = "Get all hubs", description = "Retrieve all service locations/hubs")
    public ResponseEntity<List<HubDto>> getAllHubs() {
        return ResponseEntity.ok(adminService.getAllHubs());
    }

    @PostMapping("/hubs")
    @Operation(summary = "Create hub", description = "Create a new service location/hub")
    public ResponseEntity<HubDto> createHub(@Valid @RequestBody HubDto hubDto) {
        return ResponseEntity.ok(adminService.createHub(hubDto));
    }
}
