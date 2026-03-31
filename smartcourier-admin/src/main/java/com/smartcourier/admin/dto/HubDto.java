package com.smartcourier.admin.dto;

import com.smartcourier.admin.entity.ServiceLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubDto {
    private java.util.UUID id;

    @NotBlank(message = "Location code is required")
    private String locationCode;

    @NotBlank(message = "Location name is required")
    private String locationName;

    @NotNull(message = "Location type is required")
    private ServiceLocation.LocationType locationType;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Builder.Default
    private String countryCode = "INR";

    @Builder.Default
    private Boolean isActive = true;
}
