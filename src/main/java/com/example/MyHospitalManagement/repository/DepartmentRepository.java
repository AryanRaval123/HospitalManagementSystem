package com.example.MyHospitalManagement.repository;

import com.example.MyHospitalManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}