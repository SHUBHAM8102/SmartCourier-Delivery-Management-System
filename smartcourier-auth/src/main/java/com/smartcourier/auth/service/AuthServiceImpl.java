package com.smartcourier.auth.service;

import com.smartcourier.auth.config.RabbitMQConfig;
import com.smartcourier.auth.dto.*;
import com.smartcourier.auth.entity.RefreshToken;
import com.smartcourier.auth.entity.Role;
import com.smartcourier.auth.entity.User;
import com.smartcourier.auth.exception.AuthException;
import com.smartcourier.auth.repository.RefreshTokenRepository;
import com.smartcourier.auth.repository.RoleRepository;
import com.smartcourier.auth.repository.UserRepository;
import com.smartcourier.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already registered");
        }

        Role defaultRole = roleRepository.findByRoleCode(request.getRole() != null ? request.getRole() : CUSTOMER_ROLE)
                .orElseGet(() -> roleRepository.findByRoleCode(CUSTOMER_ROLE)
                        .orElseThrow(() -> new AuthException("Default role not found")));

        User user = User.builder()
                .publicUserId(UUID.randomUUID())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .build();

        user.getRoles().add(defaultRole);
        user = userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getPublicUserId(), user.getEmail(), defaultRole.getRoleCode());
        String refreshToken = createRefreshToken(user);

        publishUserRegisteredEvent(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(jwtService.getExpiration())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new AuthException("Account is deactivated");
        }

        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getRoleCode)
                .orElse(CUSTOMER_ROLE);

        String accessToken = jwtService.generateToken(user.getPublicUserId(), user.getEmail(), role);
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(jwtService.getExpiration())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hashToken(request.getRefreshToken()))
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (!storedToken.isValid()) {
            throw new AuthException("Refresh token expired or revoked");
        }

        storedToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getRoleCode)
                .orElse(CUSTOMER_ROLE);

        String newAccessToken = jwtService.generateToken(user.getPublicUserId(), user.getEmail(), role);
        String newRefreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(jwtService.getExpiration())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findByPublicUserId(userId)
                .orElseThrow(() -> new AuthException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(hashToken(refreshToken))
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    private String createRefreshToken(User user) {
        String token = generateSecureToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(token))
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    private void publishUserRegisteredEvent(User user) {
        Map<String, Object> event = Map.of(
                "userId", user.getPublicUserId().toString(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRoles().stream()
                        .findFirst()
                        .map(Role::getRoleCode)
                        .orElse(CUSTOMER_ROLE)
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.USER_REGISTERED_ROUTING_KEY,
                event
        );
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .publicUserId(user.getPublicUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getRoles().stream()
                        .map(Role::getRoleCode)
                        .collect(Collectors.toSet()))
                .isActive(user.getIsActive())
                .build();
    }
}
