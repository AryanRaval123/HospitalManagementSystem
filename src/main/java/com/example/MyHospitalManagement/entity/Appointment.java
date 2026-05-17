package com.example.MyHospitalManagement.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    @ManyToOne
    @JoinColumn(nullable = false) // owning side(dictates F.K)
    @ToString.Exclude
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    private Doctor doctor;

}