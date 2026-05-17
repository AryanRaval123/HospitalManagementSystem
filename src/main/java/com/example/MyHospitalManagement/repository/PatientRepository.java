package com.example.MyHospitalManagement.repository;

import com.example.MyHospitalManagement.dto.BloodGroupCountResponseEntity;
import com.example.MyHospitalManagement.entity.Patient;
import com.example.MyHospitalManagement.entity.type.BloodGroupType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM Patient")
    Page<Patient> findAllPatient(Pageable pageable);

    List<Patient> findByNameContaining(String name);

    List<Patient> findByNameContainingOrderByIdDesc(String name);

    @Query("SELECT p FROM Patient p where p.bloodGroup = :blodGroup")
//    @Query("SELECT p FROM Patient p where p.bloodGroup = ?1")
    List<Patient> findByBloodGroup(@Param("blodGroup") BloodGroupType bloodGroupType);


    //    @Query("SELECT p from Patient p LEFT JOIN FETCH appointmentList a LEFT JOIN FETCH doctor d")
    @Query("SELECT p from Patient p LEFT JOIN FETCH appointmentList")
    List<Patient> findAllPatients();


    @Query("SELECT new com.example.MyHospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, count(p.bloodGroup)) FROM Patient p group by p.bloodGroup")
    List<BloodGroupCountResponseEntity> findBloodGroupCount();

    List<Patient> findByName(String name);

    List<Patient> findByBirthDateOrEmail(LocalDate birthDate, String email);

    @Query("SELECT p from Patient p ORDER BY p.name")
    List<Patient> findPatientsOrderByName();

    @Query("SELECT p FROM Patient p where p.birthDate > :birthDate")
    List<Patient> findPatientsBornAfter(LocalDate birthDate);

    @Transactional
    @Modifying
    @Query("UPDATE Patient p SET p.name = :name where p.id = :id")
    int updatePatientNameById(String name, Long id);
}