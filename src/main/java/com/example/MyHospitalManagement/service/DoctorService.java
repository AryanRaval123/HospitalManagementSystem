package com.example.MyHospitalManagement.service;

import com.example.MyHospitalManagement.dto.DoctorResponseDto;
import com.example.MyHospitalManagement.entity.Doctor;
import com.example.MyHospitalManagement.entity.User;
import com.example.MyHospitalManagement.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    // Public — returns all doctors
    public List<DoctorResponseDto> findAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }

    // DOCTOR role — returns the currently logged-in doctor's own profile
    // Identity is resolved from the JWT (no path variable needed)
    public DoctorResponseDto findCurrentDoctorDto() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No doctor profile found for the logged-in user."));
        return modelMapper.map(doctor, DoctorResponseDto.class);
    }
}
