package com.example.MyHospitalManagement.dto;


import com.example.MyHospitalManagement.entity.type.BloodGroupType;
import com.example.MyHospitalManagement.entity.type.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupRequestDto {

    // ── credentials (required for all roles) ──────────────────────
    private String username;
    private String password;
    private Role role;              // ROLE_PATIENT | ROLE_DOCTOR | ROLE_ADMIN

    // ── shared profile fields (required for PATIENT and DOCTOR) ───
    private String name;
    private String email;

    // ── patient-only fields ───────────────────────────────────────
    private LocalDate birthDate;
    private String gender;
    private BloodGroupType bloodGroup;

    // ── doctor-only fields ────────────────────────────────────────
    private String specialization;
}