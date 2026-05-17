package com.example.MyHospitalManagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // AuthTokenFilter wraps JWT exceptions inside AuthenticationException,
        // so unwrap the cause to give a specific message instead of a generic one
        Throwable cause = authException.getCause() != null
                ? authException.getCause()
                : authException;

        String message = resolveMessage(request, cause);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getRequestURI());
        body.put("timestamp", LocalDateTime.now().toString());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private String resolveMessage(HttpServletRequest request, Throwable cause) {

        // ── JWT-specific errors (from JwtUtils.validateJwtToken) ─────────────

        // token was valid but its expiry date has passed
        if (cause instanceof ExpiredJwtException) {
            return "Your session has expired. Please log in again.";
        }

        // token signature does not match — could be tampered or wrong secret
        if (cause instanceof SignatureException) {
            return "Token signature is invalid. The token may have been tampered with.";
        }

        // token string is structurally broken — not a valid JWT format
        if (cause instanceof MalformedJwtException) {
            return "Token is malformed. Please provide a valid JWT.";
        }

        // token is a JWT type the server doesn't support (e.g. unsigned token sent to signed endpoint)
        if (cause instanceof UnsupportedJwtException) {
            return "Token type is not supported by this server.";
        }

        // token string was null or completely empty
        if (cause instanceof IllegalArgumentException) {
            return "Token is empty or null. Please provide a Bearer token.";
        }

        // ── Post-validation errors ────────────────────────────────────────────

        // token was valid but the username inside it no longer exists in the DB
        // happens if an account was deleted while their token was still active
        if (cause instanceof UsernameNotFoundException) {
            return "Authenticated user no longer exists. Please log in again.";
        }

        // ── Header-level errors (no exception, just missing/wrong header) ─────

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            return "Authorization header is missing. Please include a Bearer token.";
        }

        if (!authHeader.startsWith("Bearer ")) {
            return "Invalid Authorization format. Use: Authorization: Bearer <token>";
        }

        // ── Fallback ──────────────────────────────────────────────────────────
        return "Authentication failed: " + cause.getMessage();
    }
}