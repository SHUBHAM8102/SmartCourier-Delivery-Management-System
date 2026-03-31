package com.smartcourier.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    @NotBlank(message = "Contact name is required")
    private String contactName;

    private String contactPhone;

    private String email;

    @NotBlank(message = "Address line 1 is required")
    private String line1;

    private String line2;

    private String landmark;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    private String countryCode = "IN";

    private BigDecimal latitude;

    private BigDecimal longitude;
}
