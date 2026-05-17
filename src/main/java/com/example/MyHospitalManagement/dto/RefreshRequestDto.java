package com.example.MyHospitalManagement.dto;

import lombok.Data;

@Data
public class RefreshRequestDto {
    // client sends only the refresh token string
    private String refreshToken;
}