package com.example.MyHospitalManagement.entity;

import com.example.MyHospitalManagement.entity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column
    private LocalDate birthDate;

    @Column(unique = true, nullable = false)
    private String email;

    private String gender;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;

    // ✅ NEW: every patient is linked to exactly one user account
    // unique = true enforces the 1:1 — one user cannot be two patients
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "patient_insurance_id")
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointmentList;

    public void addAppointment(Appointment appointment) {
        appointmentList.add(appointment);
        appointment.setPatient(this);
    }

    public void removeAppointment(Appointment appointment) {
        appointmentList.remove(appointment);
        appointment.setPatient(null);
    }
}