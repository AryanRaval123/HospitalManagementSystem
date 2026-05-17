package com.example.MyHospitalManagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupResponseDto {

    private Long id;
    private String username;

    public SignupResponseDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
