package com.example.MyHospitalManagement.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable = false, length = 50)
    private String policyNumber;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(nullable = false)
    private LocalDate validUntil;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createAt;

    @OneToOne(mappedBy = "insurance")
    private Patient patient;


}
