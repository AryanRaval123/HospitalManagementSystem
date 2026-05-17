//package com.example.MyHospitalManagement;
//
//import com.example.MyHospitalManagement.dto.BloodGroupCountResponseEntity;
//import com.example.MyHospitalManagement.entity.Patient;
//import com.example.MyHospitalManagement.entity.type.BloodGroupType;
//import com.example.MyHospitalManagement.repository.PatientRepository;
//import com.example.MyHospitalManagement.service.PatientService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@SpringBootTest
//public class PatientTest {
//
//    @Autowired
//    private PatientService patientService;
//
//    @Autowired
//    private PatientRepository patientRepository;
//
////    @Test
////    public void finaAllPatientTest() {
////        List<Patient> patients = patientService.findAllPatient();
////        System.out.println(patients);
////    }
//
//    @Test
//    public void patientTest() {
////        List<BloodGroupCountResponseEntity> bloodGroupCount = patientService.findBloodGroupCount();
////
////        for(var obj : bloodGroupCount) {
////            System.out.println(obj);
////        }
//
////        List<Patient> patients = patientService.findPateintsByName("Aarav Sharma");
////        System.out.println(patients);
//
////        LocalDate birthDate = LocalDate.parse("1988-03-15");
////        String email = "aarav.sharma@example.com";
////        List<Patient> patients = patientService.findPateintsBybirthDateOrEmail(birthDate,email);
////        System.out.println(patients);
//
////        List<Patient> patients = patientService.findPateintsOrderByName();
////        for(var patient : patients) {
////            System.out.println(patient);
////        }
//
////        LocalDate birthDate = LocalDate.parse("1988-03-15");
////        List<Patient> patients = patientService.findPatientsBornAfter(birthDate);
////        System.out.println(patients);
//
//
////        int status = patientService.updatePatientNameById("Aryan Raval", 3L);
////        if(status > 0) {
////            System.out.println("Record updated successfully");
////        }
////        else {
////            System.out.println("Record isn't updated");
////        }
//
////        List<Patient> patients = patientRepository.findByNameContaining("Di");
////        System.out.println(patients);
//
////        List<Patient> patients = patientRepository.findByNameContainingOrderByIdDesc("Di");
////        for(var patient : patients) {
////            System.out.println(patient);
////        }
////        List<Patient> patients = patientRepository.findByBloodGroup(BloodGroupType.A_POSITIVE);
////        for(var patient : patients) {
////            System.out.println(patient);
////        }
////        Page<Patient> patients = patientRepository.findAllPatient(PageRequest.of(2,2,Sort.by("name")));
//            List<Patient> patients = patientRepository.findAllPatients();
//        for(var patient : patients) {
//            System.out.println(patient);
//        }
//
//
//    }
//}