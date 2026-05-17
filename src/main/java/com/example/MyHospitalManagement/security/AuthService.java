package com.example.MyHospitalManagement.security;

import com.example.MyHospitalManagement.dto.LoginRequestDto;
import com.example.MyHospitalManagement.dto.LoginResponseDto;
import com.example.MyHospitalManagement.dto.SignupRequestDto;
import com.example.MyHospitalManagement.dto.SignupResponseDto;
import com.example.MyHospitalManagement.entity.*;
import com.example.MyHospitalManagement.entity.type.Role;
import com.example.MyHospitalManagement.repository.DoctorRepository;
import com.example.MyHospitalManagement.repository.PatientRepository;
import com.example.MyHospitalManagement.repository.UserRepository;
import com.example.MyHospitalManagement.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;   // ✅ NEW

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        // ✅ issue both tokens on login
        String accessToken = jwtUtils.generateJwtToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    // ✅ NEW: client calls this when access token expires
    // verifies refresh token → issues a brand new access token
    public LoginResponseDto refresh(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenString);
        User user = refreshToken.getUser();
        String newAccessToken = jwtUtils.generateJwtToken(user);

        // return the same refresh token — it is still valid until its own expiry
        return new LoginResponseDto(user.getId(), newAccessToken, refreshTokenString);
    }

    // ✅ NEW: deletes the refresh token — user is fully logged out
    @Transactional
    public void logout(User user) {
        refreshTokenService.deleteRefreshToken(user);
    }

    @Transactional
    public SignupResponseDto signup(SignupRequestDto dto) {

        userRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already taken: " + dto.getUsername());
        });

        Role role = dto.getRole() != null ? dto.getRole() : Role.ROLE_PATIENT;

        User user = userRepository.save(User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(Set.of(role))
                .build());

        switch (role) {
            case ROLE_PATIENT -> {
                validateRequiredFields(dto.getName(), dto.getEmail(), "Patient");
                Patient patient = new Patient();
                patient.setUser(user);
                patient.setName(dto.getName());
                patient.setEmail(dto.getEmail());
                patient.setBirthDate(dto.getBirthDate());
                patient.setGender(dto.getGender());
                patient.setBloodGroup(dto.getBloodGroup());
                patientRepository.save(patient);
            }
            case ROLE_DOCTOR -> {
                validateRequiredFields(dto.getName(), dto.getEmail(), "Doctor");
                if (dto.getSpecialization() == null || dto.getSpecialization().isBlank())
                    throw new IllegalArgumentException("Specialization is required for a Doctor account");
                Doctor doctor = new Doctor();
                doctor.setUser(user);
                doctor.setName(dto.getName());
                doctor.setEmail(dto.getEmail());
                doctor.setSpecialization(dto.getSpecialization());
                doctorRepository.save(doctor);
            }
            case ROLE_ADMIN -> { }
        }

        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    private void validateRequiredFields(String name, String email, String profileType) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required for a " + profileType + " account");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required for a " + profileType + " account");
    }
}