//package com.example.MyHospitalManagement;
//
//import com.example.MyHospitalManagement.entity.Appointment;
//import com.example.MyHospitalManagement.service.AppointmentService;
//import com.example.MyHospitalManagement.service.PatientService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest
//public class AppointmentTest {
//
//    @Autowired
//    private AppointmentService appointmentService;
//
//    @Autowired
//    private PatientService patientService;
//
//    @Test
//    public void appointmentTest() {
//        Appointment appointment1 = Appointment.builder()
//                .appointmentTime(LocalDateTime.of(2026,3,12,4,15,0))
//                .reason("mild fever")
//                .build();
//
//        Appointment appointment2 = Appointment.builder()
//                .appointmentTime(LocalDateTime.of(2026,4,13,8,15,0))
//                .reason("mild headache")
//                .build();
//
//        Appointment appointment3 = Appointment.builder()
//                .appointmentTime(LocalDateTime.of(2026,5,14,7,15,0))
//                .reason("mild pain")
//                .build();
//
//        Appointment newAppointment1 = appointmentService.createNewAppointment(appointment1, 1L, 2L);
//        System.out.println(appointment1);
//        Appointment newAppointment2 = appointmentService.createNewAppointment(appointment2, 2L, 2L);
//        System.out.println(appointment1);
//        Appointment newAppointment3 = appointmentService.createNewAppointment(appointment3, 3L, 2L);
//        System.out.println(appointment1);
//
//        System.out.println(newAppointment1);
//        System.out.println(newAppointment2);
//        System.out.println(newAppointment3);
//        patientService.deletePatient(2L);
////        var newappointment = appointmentService.reAssignAppointmentToAnotherDoctor(1L, 2L);
////        System.out.println(newappointment);
//    }
//
//
//
//
//}
