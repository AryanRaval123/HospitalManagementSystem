//package com.example.MyHospitalManagement;
//
//import com.example.MyHospitalManagement.entity.Insurance;
//import com.example.MyHospitalManagement.entity.Patient;
//import com.example.MyHospitalManagement.service.InsuranceService;
//import com.example.MyHospitalManagement.service.PatientService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//
//@SpringBootTest
//public class InsuranceTest {
//
//    @Autowired
//    private InsuranceService insuranceService;
//
//    @Autowired
//    private PatientService patientService;
//
//    @Test
//    public void TestInsurance() {
//
//        Insurance oldInsurance = Insurance.builder()
//                .policyNumber("HDFC_1234")
//                    .provider("HDFC")
//                .validUntil(LocalDate.of(2027,5,5))
//                .build();
//
////        Insurance newInsurance = Insurance.builder()
////                .policyNumber("LIC_234")
////                .provider("LIC")
////                .validUntil(LocalDate.of(2027,6,6))
////                .build();
//
//        Patient patient = insuranceService.assignInsuranceToPatient(oldInsurance, 1L);
//
//        Patient patient2 = patientService.disassociateInsuranceFromPatient(1L);
//        System.out.println(patient2);
//
////        insuranceService.updateInsurance(newInsurance,1L);
//    }
//
//
//}
