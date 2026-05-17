package com.example.MyHospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the random token string stored in DB and sent to client
    @Column(nullable = false, unique = true)
    private String token;

    // when this refresh token stops being valid
    @Column(nullable = false)
    private Instant expiresAt;

    // which user this refresh token belongs to
    // one user can only have one active refresh token at a time
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}