package com.example.MyHospitalManagement.repository;

import com.example.MyHospitalManagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Bug Fix #1: Detect duplicate slot — same doctor, same time, different (or same) patient
    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    // Bug Fix #3: Detect duplicate booking — same patient, same doctor, same time
    boolean existsByPatientIdAndDoctorIdAndAppointmentTime(Long patientId, Long doctorId, LocalDateTime appointmentTime);

    // Helper: find all appointments for a patient (used in ownership check)
    List<Appointment> findByPatientId(Long patientId);

    void deleteAllByPatientId(Long patientId);
}
