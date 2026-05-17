package com.example.MyHospitalManagement.controller;

import com.example.MyHospitalManagement.dto.AppointmentResponseDto;
import com.example.MyHospitalManagement.dto.DoctorResponseDto;
import com.example.MyHospitalManagement.service.AppointmentService;
import com.example.MyHospitalManagement.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    // ── GET /doctors ──────────────────────────────────────────────────────────
    // Public — anyone can browse the doctors list
    @GetMapping
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAllDoctors());
    }

    // ── GET /doctors/my ───────────────────────────────────────────────────────
    // DOCTOR only → returns the currently logged-in doctor's own profile
    // No path variable — identity resolved from JWT
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my")
    public ResponseEntity<DoctorResponseDto> getMyProfile() {
        return ResponseEntity.ok(doctorService.findCurrentDoctorDto());
    }

    // ── GET /doctors/my/appointments ─────────────────────────────────────────
    // DOCTOR only → returns the currently logged-in doctor's own appointments
    // No path variable — identity resolved from JWT
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointmentsAsDoctor());
    }

    // ── GET /doctors/{doctorId}/appointments ─────────────────────────────────
    // ADMIN only → view any doctor's appointments by their id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentOfDoctor(doctorId));
    }
}
