package com.example.MyHospitalManagement.service;

import com.example.MyHospitalManagement.dto.BloodGroupCountResponseEntity;
import com.example.MyHospitalManagement.dto.PatientDto;
import com.example.MyHospitalManagement.entity.Insurance;
import com.example.MyHospitalManagement.entity.Patient;
import com.example.MyHospitalManagement.entity.User;
import com.example.MyHospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final InsuranceService insuranceService;
    private final ModelMapper modelMapper;

    // ── JWT-resolved methods (no path variable) ───────────────────────────────

    /**
     * Returns the PatientDto for the currently logged-in patient.
     * Called by GET /patients/me — identity comes from the JWT, not from a path variable.
     */
    @Transactional
    public PatientDto findCurrentPatientDto() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No patient profile found for the logged-in user."));
        return modelMapper.map(patient, PatientDto.class);
    }

    // ── ADMIN methods (use explicit id) ──────────────────────────────────────

    @Transactional(isolation = Isolation.DEFAULT)
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patient -> modelMapper.map(patient, PatientDto.class))
                .collect(Collectors.toList());
    }

    public String deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
        return "Patient has been deleted.";
    }

    @Transactional
    public PatientDto findPatientDto(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        return modelMapper.map(patient, PatientDto.class);
    }

    // kept for internal use by services that still need userId → Patient lookup
    @Transactional
    public PatientDto findPatientDtoByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No patient profile found for user id: " + userId));
        return modelMapper.map(patient, PatientDto.class);
    }

    @Transactional
    public Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
    }

    public List<Patient> findPatientsOrderByName() {
        return patientRepository.findPatientsOrderByName();
    }

    public List<Patient> findPatientsByName(String name) {
        return patientRepository.findByName(name);
    }

    public List<Patient> findPatientsByBirthDateOrEmail(LocalDate birthDate, String email) {
        return patientRepository.findByBirthDateOrEmail(birthDate, email);
    }

    @Transactional
    public Patient disassociateInsuranceFromPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));
        Insurance insurance = patient.getInsurance();
        if (insurance != null) {
            insurance.setPatient(null);
            patient.setInsurance(null);
        }
        return patient;
    }

    @Transactional
    public List<BloodGroupCountResponseEntity> findBloodGroupCount() {
        return patientRepository.findBloodGroupCount();
    }

    public List<Patient> findPatientsBornAfter(LocalDate birthDate) {
        return patientRepository.findPatientsBornAfter(birthDate);
    }

    @Transactional
    public int updatePatientNameById(String name, Long id) {
        return patientRepository.updatePatientNameById(name, id);
    }
}
