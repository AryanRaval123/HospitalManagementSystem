package com.example.MyHospitalManagement.error;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ──────────────────────────────────────────────────────

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "User not found: " + ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 400 Bad Request ────────────────────────────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Bug Fix: IllegalStateException also maps to 400 (e.g. duplicate state violations)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Bean Validation (@Valid) failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'");
    }

    // ── 401 Unauthorized ───────────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwt(ExpiredJwtException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Your session has expired. Please log in again.");
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiError> handleBadSignature(SignatureException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Token signature is invalid.");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwt(MalformedJwtException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Token is malformed.");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiError> handleUnsupportedJwt(UnsupportedJwtException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Token type is not supported.");
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwt(JwtException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + ex.getMessage());
    }

    // ── 403 Forbidden ──────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Access denied: you do not have permission to perform this action.");
    }

    // ── 500 Internal Server Error ──────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }

    // ── helper ─────────────────────────────────────────────────────────────

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        return new ResponseEntity<>(new ApiError(message, status), status);
    }
}
