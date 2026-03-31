package com.smartcourier.admin.dto;

import com.smartcourier.admin.entity.ReportRun;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    @NotBlank(message = "Report code is required")
    private String reportCode;

    private Map<String, Object> filters;

    @NotNull(message = "Output format is required")
    @Builder.Default
    private ReportRun.OutputFormat outputFormat = ReportRun.OutputFormat.JSON;

    private UUID requestedBy;
}
