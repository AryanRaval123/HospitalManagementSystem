package com.example.MyHospitalManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String jwt;
    private String refreshToken;

    public LoginResponseDto(Long userId, String jwt, String refreshToken) {
        this.userId = userId;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
    }
}
