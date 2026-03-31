package com.smartcourier.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID publicUserId;
    private String fullName;
    private String email;
    private String phone;
    private Set<String> roles;
    private Boolean isActive;
}
