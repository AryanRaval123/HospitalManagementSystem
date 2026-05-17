package com.example.MyHospitalManagement.controller;

import com.example.MyHospitalManagement.dto.AppointmentRequestDto;
import com.example.MyHospitalManagement.dto.AppointmentResponseDto;
import com.example.MyHospitalManagement.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    // ── POST /appointments ────────────────────────────────────────────────────
    // PATIENT → books for themselves (patientId resolved from JWT, no path variable needed)
    // ADMIN   → must supply ?patientId=X as a request param
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<String> bookAppointment(
            @Valid @RequestBody AppointmentRequestDto dto,
            @RequestParam(required = false) Long patientId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createNewAppointment(dto, patientId));
    }

    // ── GET /appointments/my ──────────────────────────────────────────────────
    // PATIENT → returns only their own appointments (no path variable, resolved from JWT)
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponseDto>> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointmentsAsPatient());
    }

    // ── PUT /appointments/{appointmentId}/doctor?doctorId=X ──────────────────
    // ADMIN only — reassigns an appointment to a different doctor
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{appointmentId}/doctor")
    public ResponseEntity<String> reassignDoctor(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId) {
        appointmentService.reAssignAppointmentToAnotherDoctor(appointmentId, doctorId);
        return ResponseEntity.ok("Appointment reassigned successfully.");
    }

    // ── DELETE /appointments/{appointmentId} ──────────────────────────────────
    // PATIENT → can only cancel their OWN appointment (ownership enforced in service)
    // ADMIN   → can cancel any appointment
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.deleteSpecificAppointment(appointmentId));
    }

    // ── DELETE /appointments/patients/{patientId}/appointments ───────────────
    // ADMIN only — cancels ALL appointments of a patient at once
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/patients/{patientId}/appointments")
    public ResponseEntity<String> deleteAllAppointmentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.deleteAllAppointmentsByPatientId(patientId));
    }

    // ── GET /appointments/patients/{patientId} ───────────────────────────────
    // ADMIN only — view all appointments of any patient by id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointmentsByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentOfPatient(patientId));
    }
}
