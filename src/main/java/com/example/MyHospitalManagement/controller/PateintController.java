package com.example.MyHospitalManagement.controller;

import com.example.MyHospitalManagement.dto.PatientDto;
import com.example.MyHospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PateintController {

    private final PatientService patientService;

    // ── GET /patients/me ──────────────────────────────────────────────────────
    // PATIENT → returns their own profile, resolved from JWT (no path variable)
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public ResponseEntity<PatientDto> getMyProfile() {
        return ResponseEntity.ok(patientService.findCurrentPatientDto());
    }

    // ── GET /patients ─────────────────────────────────────────────────────────
    // ADMIN only — full list of all patients
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // ── GET /patients/{patientId} ─────────────────────────────────────────────
    // ADMIN only — look up any patient by id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.findPatientDto(patientId));
    }

    // ── DELETE /patients/{patientId} ──────────────────────────────────────────
    // ADMIN only — removes patient and their linked data
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{patientId}")
    public ResponseEntity<String> deletePatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.deletePatient(patientId));
    }
}
