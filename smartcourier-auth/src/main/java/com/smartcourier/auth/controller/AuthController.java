package com.smartcourier.auth.controller;

import com.smartcourier.auth.dto.*;
import com.smartcourier.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and Authorization APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register new user", description = "Creates a new user account and returns JWT tokens")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refreshes access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID", description = "Returns user details by public user ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates refresh token")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}/email")
    @Operation(summary = "Get user email by ID", description = "Returns email for a given user ID (for service-to-service communication)")
    public ResponseEntity<java.util.Map<String, String>> getUserEmail(@PathVariable UUID userId) {
        UserResponse user = authService.getUserById(userId);
        return ResponseEntity.ok(java.util.Map.of("email", user.getEmail()));
    }
}
