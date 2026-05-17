package com.example.MyHospitalManagement.dto;

import com.example.MyHospitalManagement.entity.type.BloodGroupType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientDto {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String gender;
    private BloodGroupType bloodGroup;
    private InsuranceDto insurance;

}
