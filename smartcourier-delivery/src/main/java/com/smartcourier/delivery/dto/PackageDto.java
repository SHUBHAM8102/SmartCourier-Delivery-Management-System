package com.smartcourier.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDto {

    private String packageType;

    @NotBlank(message = "Description is required")
    private String description;

    private Integer quantity = 1;

    @NotNull(message = "Weight is required")
    private BigDecimal weightKg;

    private BigDecimal lengthCm;

    private BigDecimal widthCm;

    private BigDecimal heightCm;

    private BigDecimal declaredValue;

    private Boolean fragile = false;

    private Boolean hazardous = false;
}
