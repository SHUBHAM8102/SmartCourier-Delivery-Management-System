package com.smartcourier.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeliveryRequest {

    @NotNull(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Sender address is required")
    @Valid
    private AddressDto senderAddress;

    @NotNull(message = "Receiver address is required")
    @Valid
    private AddressDto receiverAddress;

    @NotEmpty(message = "At least one package is required")
    @Valid
    private List<PackageDto> packages;

    private String notes;
}
