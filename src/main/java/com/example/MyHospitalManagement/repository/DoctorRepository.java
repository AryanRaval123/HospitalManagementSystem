package com.example.MyHospitalManagement.repository;

import com.example.MyHospitalManagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ✅ NEW: look up a doctor by their linked user account
    Optional<Doctor> findByUserId(Long userId);
}