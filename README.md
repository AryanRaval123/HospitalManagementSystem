# 🏥 Hospital Management System

A production-ready **REST API** for managing hospital operations — patients, doctors, appointments, and insurance — built with **Spring Boot 3** and **Java 21**.

Authentication is fully stateless using **JWT access tokens + refresh tokens**, with role-based access control enforced at both the security filter chain and method level.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.4 |
| Security | Spring Security 6 + JWT (jjwt) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Mapping | ModelMapper 3.2 |
| Boilerplate | Lombok |
| Build | Maven |

---

## 📁 Project Structure

```
src/main/java/com/example/MyHospitalManagement/
├── controller/        # REST controllers (Patient, Doctor, Appointment, Insurance, Auth)
├── service/           # Business logic
├── repository/        # Spring Data JPA repositories
├── entity/            # JPA entities (Patient, Doctor, User, Appointment, Insurance)
├── dto/               # Request / Response DTOs
├── security/          # JWT filter, auth service, token utilities, WebSecurityConfig
├── config/            # ModelMapper bean
└── error/             # Global exception handler, ApiError
```

---

## ⚙️ Setup & Configuration

### Prerequisites

- Java 21+
- PostgreSQL (running locally or remote)
- Redis (running locally or remote)
- Maven (or use the included `./mvnw` wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/your-username/MyHospitalManagement.git
cd MyHospitalManagement
```

### 2. Configure `application.properties`

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/hospitalDB
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=create   # use 'update' after first run

# JWT
spring.app.jwtSecretKey=YOUR_BASE64_SECRET_KEY
spring.app.jwtExpirationMs=1800000          # 30 minutes
spring.app.refreshTokenExpirationMs=604800000  # 7 days
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will start at `http://localhost:8080`.

---

## 🔐 Authentication Flow

This API uses a **stateless JWT** strategy. No sessions are stored on the server.

```
POST /auth/signup    →  Create account (PATIENT / DOCTOR / ADMIN)
POST /auth/login     →  Returns { accessToken, refreshToken }
POST /auth/refresh   →  Exchange refresh token for a new access token
POST /auth/logout    →  Invalidates the refresh token
```

All protected requests require the access token in the `Authorization` header:

```
Authorization: Bearer <accessToken>
```

---

## 👥 Roles

| Role | Description |
|---|---|
| `ROLE_PATIENT` | Can view own profile, manage own appointments and insurance |
| `ROLE_DOCTOR` | Can view own profile and own appointments |
| `ROLE_ADMIN` | Full access — manage all patients, doctors, appointments, insurance |

---

## 📡 API Endpoints

### 🔑 Auth — `/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/signup` | Public | Register a new user |
| POST | `/auth/login` | Public | Login and receive tokens |
| POST | `/auth/refresh` | Public | Refresh access token |
| POST | `/auth/logout` | Authenticated | Invalidate refresh token |

**Signup request body example (PATIENT):**
```json
{
  "username": "john_doe",
  "password": "secret123",
  "role": "ROLE_PATIENT",
  "name": "John Doe",
  "email": "john@example.com",
  "birthDate": "1995-06-15",
  "gender": "Male",
  "bloodGroup": "A_POSITIVE"
}
```

---

### 🧑‍⚕️ Patients — `/patients`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/patients/me` | PATIENT | Get own profile (resolved from JWT) |
| GET | `/patients` | ADMIN | Get all patients |
| GET | `/patients/{id}` | ADMIN | Get patient by ID |
| DELETE | `/patients/{id}` | ADMIN | Delete patient by ID |

---

### 💊 Insurance — `/patients/.../insurance`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/patients/me/insurance` | PATIENT | Assign insurance to own profile |
| PUT | `/patients/me/insurance` | PATIENT | Update own insurance |
| POST | `/patients/{id}/insurance` | ADMIN | Assign insurance to any patient |
| PUT | `/patients/{id}/insurance` | ADMIN | Update insurance for any patient |

---

### 🩺 Doctors — `/doctors`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/doctors` | Public | List all doctors |
| GET | `/doctors/my` | DOCTOR | Get own profile (resolved from JWT) |
| GET | `/doctors/my/appointments` | DOCTOR | Get own appointments |
| GET | `/doctors/{id}/appointments` | ADMIN | Get any doctor's appointments |

---

### 📅 Appointments — `/appointments`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/appointments` | PATIENT, ADMIN | Book an appointment |
| GET | `/appointments/my` | PATIENT | Get own appointments |
| DELETE | `/appointments/{id}` | PATIENT, ADMIN | Cancel a single appointment |
| GET | `/appointments/patients/{id}` | ADMIN | Get all appointments for a patient |
| DELETE | `/appointments/patients/{id}/appointments` | ADMIN | Cancel all appointments for a patient |
| PUT | `/appointments/{id}/doctor?doctorId=X` | ADMIN | Reassign appointment to another doctor |

**Book appointment request body:**
```json
{
  "doctorId": 2,
  "reason": "Routine checkup",
  "appointmentTime": "2025-06-20T10:30:00"
}
```

> **Note:** PATIENT role books for themselves (patientId resolved from JWT). ADMIN must pass `?patientId=X` as a query parameter.

---

## 🛡️ Security Design

- **JWT filter** (`AuthTokenFilter`) runs on every request — validates the token and sets the authenticated user into the `SecurityContext`
- **Method-level security** (`@PreAuthorize`) is enabled via `@EnableMethodSecurity`
- **Literal URL paths** are registered before wildcard paths in `WebSecurityConfig` to prevent `/patients/me` being caught by `/patients/*`
- **Refresh tokens** are stored in the database and validated on each refresh request
- **Custom handlers** for 401 (unauthenticated) and 403 (forbidden) return structured JSON error responses

<img width="1919" height="1124" alt="Screenshot 2026-05-17 120540" src="https://github.com/user-attachments/assets/5e4305e1-73ed-4b09-b22a-da6fc96cb524" />
<br/>
<br/>
<hr/>
<img width="1322" height="840" alt="Screenshot 2026-05-17 121139" src="https://github.com/user-attachments/assets/4e7d2410-aac0-43fc-9e04-3dd90919083e" />


