package com.example.MyHospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String specialization;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    // ✅ NEW: every doctor is linked to exactly one user account
    // unique = true enforces the 1:1 — one user cannot be two doctors
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "doctor", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    private List<Appointment> appointmentList;

    @ManyToMany(mappedBy = "doctors")
    private Set<Department> departments = new HashSet<>();

    public void addAppointment(Appointment appointment) {
        appointmentList.add(appointment);
        appointment.setDoctor(this);
    }

    public void removeAppointment(Appointment appointment) {
        appointmentList.remove(appointment);
        appointment.setDoctor(null);
    }
}