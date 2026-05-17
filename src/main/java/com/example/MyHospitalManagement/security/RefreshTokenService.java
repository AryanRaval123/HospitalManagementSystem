package com.example.MyHospitalManagement.security;

import com.example.MyHospitalManagement.entity.RefreshToken;
import com.example.MyHospitalManagement.entity.User;
import com.example.MyHospitalManagement.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // 7 days in milliseconds — configure in application.properties if you want
    @Value("${spring.app.refreshTokenExpirationMs:604800000}")
    private Long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // if user already has a refresh token, delete it first
        // this ensures only ONE active refresh token per user at all times
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())   // random, unguessable string
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // called during POST /auth/refresh
    // throws exception if token not found or expired — caught by GlobalExceptionHandler

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found. Please log in again."));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            // expired — delete it from DB so it can't be reused
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired. Please log in again.");
        }
        return refreshToken;
    }

    // called during POST /auth/logout
    @Transactional
    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}