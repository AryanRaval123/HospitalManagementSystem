package com.example.MyHospitalManagement.repository;

import com.example.MyHospitalManagement.entity.RefreshToken;
import com.example.MyHospitalManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}