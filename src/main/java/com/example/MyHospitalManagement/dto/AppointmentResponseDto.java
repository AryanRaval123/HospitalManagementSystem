package com.example.MyHospitalManagement.dto;

import com.example.MyHospitalManagement.entity.Doctor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponseDto {

    private Long id;
    private LocalDateTime appointmentTime;
    private String reason;
    private DoctorResponseDto doctor;

}
