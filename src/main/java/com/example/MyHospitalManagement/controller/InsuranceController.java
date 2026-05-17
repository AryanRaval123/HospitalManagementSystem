package com.example.MyHospitalManagement.controller;

import com.example.MyHospitalManagement.dto.InsuranceDto;
import com.example.MyHospitalManagement.service.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class InsuranceController {

    private final InsuranceService insuranceService;

    // ── POST /patients/me/insurance ───────────────────────────────────────────
    // PATIENT → assigns insurance to their own profile (patientId from JWT)
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/me/insurance")
    public ResponseEntity<String> assignMyInsurance(@Valid @RequestBody InsuranceDto insuranceDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(insuranceService.assignInsuranceToCurrentPatient(insuranceDto));
    }

    // ── PUT /patients/me/insurance ────────────────────────────────────────────
    // PATIENT → updates their own insurance (patientId from JWT)
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/me/insurance")
    public ResponseEntity<String> updateMyInsurance(@Valid @RequestBody InsuranceDto insuranceDto) {
        insuranceService.updateInsuranceForCurrentPatient(insuranceDto);
        return ResponseEntity.ok("Insurance updated successfully");
    }

    // ── POST /patients/{patientId}/insurance ─────────────────────────────────
    // ADMIN → assigns insurance to any patient by id
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{patientId}/insurance")
    public ResponseEntity<String> assignInsuranceByAdmin(
            @PathVariable Long patientId,
            @Valid @RequestBody InsuranceDto insuranceDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(insuranceService.assignInsuranceToPatient(insuranceDto, patientId));
    }

    // ── PUT /patients/{patientId}/insurance ──────────────────────────────────
    // ADMIN → updates insurance for any patient by id
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{patientId}/insurance")
    public ResponseEntity<String> updateInsuranceByAdmin(
            @PathVariable Long patientId,
            @Valid @RequestBody InsuranceDto insuranceDto) {
        insuranceService.updateInsurance(insuranceDto, patientId);
        return ResponseEntity.ok("Insurance updated successfully");
    }
}
