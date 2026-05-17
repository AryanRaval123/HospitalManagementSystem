package com.example.MyHospitalManagement.dto;

import com.example.MyHospitalManagement.entity.type.BloodGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BloodGroupCountResponseEntity {
    private BloodGroupType bloodGroupType;
    private Long count;
}

