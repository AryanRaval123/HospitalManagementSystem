package com.example.MyHospitalManagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request -> request

                // ── Auth (public) ──────────────────────────────────────────────────────────
                .requestMatchers("/auth/**").permitAll()

                // ── Doctors ───────────────────────────────────────────────────────────────
                // PUBLIC: browse all doctors
                .requestMatchers(HttpMethod.GET, "/doctors").permitAll()

                // DOCTOR: own profile & own appointments — LITERAL paths first, before any wildcard
                .requestMatchers(HttpMethod.GET, "/doctors/my").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.GET, "/doctors/my/appointments").hasRole("DOCTOR")

                // ADMIN: any doctor's appointments by id — wildcard AFTER literals
                .requestMatchers(HttpMethod.GET, "/doctors/*/appointments").hasRole("ADMIN")

                // ── Patients ──────────────────────────────────────────────────────────────
                // PATIENT: own profile — LITERAL "/patients/me" MUST come before "/patients/*"
                .requestMatchers(HttpMethod.GET,  "/patients/me").hasRole("PATIENT")

                // PATIENT: own insurance — LITERAL "/patients/me/insurance" before "/patients/*/insurance"
                .requestMatchers(HttpMethod.POST, "/patients/me/insurance").hasRole("PATIENT")
                .requestMatchers(HttpMethod.PUT,  "/patients/me/insurance").hasRole("PATIENT")

                // ADMIN: insurance for any patient by id — wildcard AFTER literals
                .requestMatchers(HttpMethod.POST, "/patients/*/insurance").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/patients/*/insurance").hasRole("ADMIN")

                // ADMIN: full patient list
                .requestMatchers(HttpMethod.GET,    "/patients").hasRole("ADMIN")

                // ADMIN: any patient by id — wildcard AFTER all literals above
                .requestMatchers(HttpMethod.GET,    "/patients/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/patients/*").hasRole("ADMIN")

                // ── Appointments ──────────────────────────────────────────────────────────
                // PATIENT+ADMIN: book an appointment
                .requestMatchers(HttpMethod.POST, "/appointments").hasAnyRole("PATIENT", "ADMIN")

                // PATIENT: own appointments — LITERAL "/appointments/my" MUST come before "/appointments/*"
                .requestMatchers(HttpMethod.GET, "/appointments/my").hasRole("PATIENT")

                // ADMIN: all appointments of a specific patient — LITERAL before wildcard
                .requestMatchers(HttpMethod.GET,    "/appointments/patients/*").hasRole("ADMIN")

                // ADMIN: bulk-cancel all appointments of a patient — LITERAL before wildcard
                .requestMatchers(HttpMethod.DELETE, "/appointments/patients/*/appointments").hasRole("ADMIN")

                // ADMIN: reassign doctor on an appointment
                .requestMatchers(HttpMethod.PUT, "/appointments/*/doctor").hasRole("ADMIN")

                // PATIENT+ADMIN: cancel a single appointment — wildcard AFTER all literals
                .requestMatchers(HttpMethod.DELETE, "/appointments/*").hasAnyRole("PATIENT", "ADMIN")

                .anyRequest().authenticated()
        );

        httpSecurity.exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        httpSecurity.csrf(csrf -> csrf.disable());
        httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
