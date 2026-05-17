package com.example.MyHospitalManagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InsuranceDto {

    // Bug Fix: id should NOT be accepted from client (prevents spoofing an existing insurance record)
    // private Long id; ← REMOVED

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotNull(message = "Valid-until date is required")
    @Future(message = "Insurance must be valid in the future")
    private LocalDate validUntil;
}
