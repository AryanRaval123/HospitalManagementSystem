package com.example.MyHospitalManagement.controller;

import com.example.MyHospitalManagement.dto.LoginRequestDto;
import com.example.MyHospitalManagement.dto.LoginResponseDto;
import com.example.MyHospitalManagement.dto.RefreshRequestDto;
import com.example.MyHospitalManagement.dto.SignupRequestDto;
import com.example.MyHospitalManagement.dto.SignupResponseDto;
import com.example.MyHospitalManagement.entity.User;
import com.example.MyHospitalManagement.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequestDto));
    }

    // returns both accessToken and refreshToken
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    // ✅ NEW: client calls this when access token expires
    // sends { "refreshToken": "..." } → gets back a new accessToken
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody RefreshRequestDto refreshRequestDto) {
        return ResponseEntity.ok(authService.refresh(refreshRequestDto.getRefreshToken()));
    }

    // ✅ NEW: deletes refresh token from DB — user is fully logged out
    // @AuthenticationPrincipal pulls the logged-in User from SecurityContext automatically
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal User user) {
        authService.logout(user);
        return ResponseEntity.ok("Logged out successfully.");
    }
}