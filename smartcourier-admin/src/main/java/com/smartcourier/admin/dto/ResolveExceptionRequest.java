package com.smartcourier.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveExceptionRequest {
    private UUID resolvedBy;

    @NotBlank(message = "Resolution notes are required")
    private String resolutionNotes;
}
