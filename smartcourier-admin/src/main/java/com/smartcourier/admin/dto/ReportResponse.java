package com.smartcourier.admin.dto;

import com.smartcourier.admin.entity.ReportRun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private UUID id;
    private UUID reportDefinitionId;
    private String reportCode;
    private String reportName;
    private UUID requestedBy;
    private ReportRun.OutputFormat outputFormat;
    private String outputUrl;
    private ReportRun.ReportStatus status;
    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
}
