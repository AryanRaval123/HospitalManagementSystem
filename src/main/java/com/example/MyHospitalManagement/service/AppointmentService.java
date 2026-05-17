package com.example.MyHospitalManagement.service;

import com.example.MyHospitalManagement.dto.AppointmentRequestDto;
import com.example.MyHospitalManagement.dto.AppointmentResponseDto;
import com.example.MyHospitalManagement.entity.Appointment;
import com.example.MyHospitalManagement.entity.Doctor;
import com.example.MyHospitalManagement.entity.Patient;
import com.example.MyHospitalManagement.entity.User;
import com.example.MyHospitalManagement.repository.AppointmentRepository;
import com.example.MyHospitalManagement.repository.DoctorRepository;
import com.example.MyHospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Returns true if the currently authenticated user is an ADMIN. */
    private boolean currentUserIsAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /** Returns the Patient entity that belongs to the currently logged-in user. */
    private Patient resolveCurrentPatient() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No patient profile found for the logged-in user."));
    }

    /** Returns the Doctor entity that belongs to the currently logged-in user. */
    private Doctor resolveCurrentDoctor() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No doctor profile found for the logged-in user."));
    }

    // ── create appointment ───────────────────────────────────────────────────

    /**
     * Books an appointment.
     *
     * PATIENT role → patientId is resolved from the JWT; adminPatientId is ignored.
     * ADMIN role   → patientId comes from adminPatientId parameter.
     */
    @Transactional
    public String createNewAppointment(AppointmentRequestDto appointmentDto, Long adminPatientId) {
        Long doctorId = appointmentDto.getDoctorId();
        LocalDateTime appointmentTime = appointmentDto.getAppointmentTime();

        // Resolve the patient — from JWT for PATIENT, from param for ADMIN
        Patient patient;
        if (currentUserIsAdmin()) {
            if (adminPatientId == null) {
                throw new IllegalArgumentException(
                        "Admin must supply patientId as a request parameter when booking an appointment.");
            }
            patient = patientRepository.findById(adminPatientId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Patient not found with Id: " + adminPatientId));
        } else {
            patient = resolveCurrentPatient();
        }

        Long patientId = patient.getId();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with Id: " + doctorId));

        // duplicate exact booking
        if (appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentTime(patientId, doctorId, appointmentTime)) {
            throw new IllegalStateException(
                    "You already have an appointment with this doctor at " + appointmentTime + ".");
        }

        // doctor already booked by another patient at this slot
        if (appointmentRepository.existsByDoctorIdAndAppointmentTime(doctorId, appointmentTime)) {
            throw new IllegalStateException(
                    "Dr. " + doctor.getName() + " is already booked at " + appointmentTime
                    + ". Please choose a different time.");
        }

        Appointment appointment = new Appointment();
        appointment.setReason(appointmentDto.getReason());
        appointment.setAppointmentTime(appointmentTime);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        patient.addAppointment(appointment);
        doctor.addAppointment(appointment);

        appointmentRepository.save(appointment);
        return "Appointment confirmed!";
    }

    // ── get own appointments (PATIENT) ───────────────────────────────────────

    /**
     * Returns all appointments for the currently logged-in patient.
     * Patients call GET /appointments/my — no path variable needed.
     */
    @Transactional
    public List<AppointmentResponseDto> getMyAppointmentsAsPatient() {
        Patient patient = resolveCurrentPatient();
        return appointmentRepository.findByPatientId(patient.getId()).stream()
                .map(a -> modelMapper.map(a, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }

    // ── get own appointments (DOCTOR) ────────────────────────────────────────

    /**
     * Returns all appointments for the currently logged-in doctor.
     * Doctors call GET /doctors/my/appointments — no path variable needed.
     */
    @Transactional
    public List<AppointmentResponseDto> getMyAppointmentsAsDoctor() {
        Doctor doctor = resolveCurrentDoctor();
        return doctor.getAppointmentList().stream()
                .map(a -> modelMapper.map(a, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }

    // ── ADMIN: get all appointments for any doctor (by id) ───────────────────

    @Transactional
    public List<AppointmentResponseDto> getAllAppointmentOfDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("No Doctor found with Id: " + doctorId));
        return doctor.getAppointmentList().stream()
                .map(a -> modelMapper.map(a, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }

    // ── ADMIN: get all appointments for any patient (by id) ──────────────────

    @Transactional
    public List<AppointmentResponseDto> getAllAppointmentOfPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with Id: " + patientId));
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(a -> modelMapper.map(a, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }

    // ── reassign (ADMIN) ─────────────────────────────────────────────────────

    @Transactional
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Appointment not found with Id: " + appointmentId));
        Doctor newDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with Id: " + doctorId));
        Doctor oldDoctor = appointment.getDoctor();

        if (oldDoctor != null && oldDoctor.getId().equals(doctorId)) {
            throw new IllegalArgumentException(
                    "Appointment is already assigned to Dr. " + oldDoctor.getName() + ".");
        }

        if (oldDoctor != null) oldDoctor.removeAppointment(appointment);
        newDoctor.addAppointment(appointment);
        return appointment;
    }

    // ── cancel one appointment ───────────────────────────────────────────────

    /**
     * Cancels one appointment.
     * PATIENT role → can only cancel their own appointments.
     * ADMIN role   → can cancel any appointment.
     */
    @Transactional
    public String deleteSpecificAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Appointment not found with Id: " + appointmentId));

        // PATIENT ownership check
        if (!currentUserIsAdmin()) {
            Patient currentPatient = resolveCurrentPatient();
            if (!appointment.getPatient().getId().equals(currentPatient.getId())) {
                throw new AccessDeniedException(
                        "You are not allowed to cancel another patient's appointment.");
            }
        }

        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        if (patient != null) patient.removeAppointment(appointment);
        if (doctor != null) doctor.removeAppointment(appointment);

        appointmentRepository.delete(appointment);
        return "Appointment with Id " + appointmentId + " has been cancelled.";
    }

    // ── cancel all appointments for a patient (ADMIN) ────────────────────────

    @Transactional
    public String deleteAllAppointmentsByPatientId(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with Id: " + patientId));
        appointmentRepository.deleteAllByPatientId(patientId);
        return "All appointments for patient Id " + patientId + " have been cancelled.";
    }
}
