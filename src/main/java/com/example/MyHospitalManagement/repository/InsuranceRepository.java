package com.example.MyHospitalManagement.repository;

import com.example.MyHospitalManagement.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    // Bug Fix: Prevent duplicate policy numbers
    boolean existsByPolicyNumber(String policyNumber);

    Optional<Insurance> findByPolicyNumber(String policyNumber);
}
