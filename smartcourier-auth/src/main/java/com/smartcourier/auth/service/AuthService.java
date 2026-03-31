package com.smartcourier.auth.service;

import com.smartcourier.auth.dto.*;
import com.smartcourier.auth.entity.RefreshToken;
import com.smartcourier.auth.entity.Role;
import com.smartcourier.auth.entity.User;

import java.util.UUID;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    UserResponse getUserById(UUID userId);
    void logout(String refreshToken);
}
