package com.smartcourier.admin.dto;

import com.smartcourier.admin.entity.DeliveryException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRequest {
    @NotNull(message = "Delivery ID is required")
    private UUID deliveryId;

    @NotNull(message = "Exception type is required")
    private DeliveryException.ExceptionType exceptionType;

    @NotNull(message = "Severity is required")
    private DeliveryException.Severity severity;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private UUID raisedBy;
}
