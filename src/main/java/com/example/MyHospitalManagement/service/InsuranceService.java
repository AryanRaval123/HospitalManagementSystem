package com.example.MyHospitalManagement.service;

import com.example.MyHospitalManagement.dto.InsuranceDto;
import com.example.MyHospitalManagement.entity.Insurance;
import com.example.MyHospitalManagement.entity.Patient;
import com.example.MyHospitalManagement.entity.User;
import com.example.MyHospitalManagement.repository.InsuranceRepository;
import com.example.MyHospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final PatientRepository patientRepository;
    private final InsuranceRepository insuranceRepository;
    private final ModelMapper modelMapper;

    // ── helper ────────────────────────────────────────────────────────────────

    /** Resolves the Patient entity for the currently logged-in user. */
    private Patient resolveCurrentPatient() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No patient profile found for the logged-in user."));
    }

    // ── PATIENT endpoints (identity from JWT) ─────────────────────────────────

    /**
     * Assigns insurance to the currently logged-in patient.
     * Called by POST /patients/me/insurance — no patientId path variable.
     */
    @Transactional
    public String assignInsuranceToCurrentPatient(InsuranceDto insuranceDto) {
        Patient patient = resolveCurrentPatient();
        return doAssignInsurance(insuranceDto, patient);
    }

    /**
     * Updates insurance for the currently logged-in patient.
     * Called by PUT /patients/me/insurance — no patientId path variable.
     */
    @Transactional
    public Patient updateInsuranceForCurrentPatient(InsuranceDto insuranceDto) {
        Patient patient = resolveCurrentPatient();
        return doUpdateInsurance(insuranceDto, patient);
    }

    // ── ADMIN endpoints (explicit patientId) ──────────────────────────────────

    /**
     * Assigns insurance to any patient by id.
     * Called by POST /patients/{patientId}/insurance — ADMIN only.
     */
    @Transactional
    public String assignInsuranceToPatient(InsuranceDto insuranceDto, Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with Id: " + patientId));
        return doAssignInsurance(insuranceDto, patient);
    }

    /**
     * Updates insurance for any patient by id.
     * Called by PUT /patients/{patientId}/insurance — ADMIN only.
     */
    @Transactional
    public Patient updateInsurance(InsuranceDto insuranceDto, Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with Id: " + patientId));
        return doUpdateInsurance(insuranceDto, patient);
    }

    // ── shared logic ──────────────────────────────────────────────────────────

    private String doAssignInsurance(InsuranceDto insuranceDto, Patient patient) {
        if (patient.getInsurance() != null) {
            throw new IllegalArgumentException(
                    "Patient already has an insurance policy. " +
                    "Use the update endpoint to replace it.");
        }

        if (insuranceRepository.existsByPolicyNumber(insuranceDto.getPolicyNumber())) {
            throw new IllegalArgumentException(
                    "Insurance policy number '" + insuranceDto.getPolicyNumber() + "' already exists.");
        }

        Insurance insurance = modelMapper.map(insuranceDto, Insurance.class);
        insurance.setId(null);  // never trust a client-supplied PK

        patient.setInsurance(insurance);
        insurance.setPatient(patient);

        return "Insurance added successfully";
    }

    private Patient doUpdateInsurance(InsuranceDto insuranceDto, Patient patient) {
        String newPolicyNumber = insuranceDto.getPolicyNumber();
        Insurance existing = patient.getInsurance();

        // Allow the same policy number if it belongs to this patient (update in-place)
        boolean samePolicy = existing != null && existing.getPolicyNumber().equals(newPolicyNumber);
        if (!samePolicy && insuranceRepository.existsByPolicyNumber(newPolicyNumber)) {
            throw new IllegalArgumentException(
                    "Insurance policy number '" + newPolicyNumber +
                    "' is already used by another patient.");
        }

        if (existing != null) {
            existing.setPatient(null);
        }

        Insurance newInsurance = modelMapper.map(insuranceDto, Insurance.class);
        newInsurance.setId(null);  // always generate a new PK

        patient.setInsurance(newInsurance);
        newInsurance.setPatient(patient);

        return patient;
    }
}
