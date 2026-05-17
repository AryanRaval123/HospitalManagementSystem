-- ============================================================
--  HOSPITAL MANAGEMENT SYSTEM — SEED DATA
--  Insert order matters — respect foreign key dependencies:
--  app_user → user_roles → insurance → doctor → patient
--  → department → my_dpt_doctors → appointment
-- ============================================================


-- ── 1. USERS ─────────────────────────────────────────────────────────────
--    Each row has the plain password commented so you always know it
--    Format of comment:  username | plain password | role

INSERT INTO app_user (id, username, password) VALUES
-- Username: admin     | Password: AdminPassword#2026 | ROLE_ADMIN
(1, 'admin',     '$2b$10$wK8W9G3BwE8PZ2B7v3u7OeQmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: dr.rakesh | Password: RakeshDoc!99       | ROLE_DOCTOR
(2, 'dr.rakesh', '$2b$10$7Z2NnXs1fX7m9g2bX3c4DeK9rMv/uDuxuX96s4fGg43A1n8yDGabc'),

-- Username: dr.sneha  | Password: SnehaSecureDoc#1   | ROLE_DOCTOR
(3, 'dr.sneha',  '$2b$10$m9X2bN3v4c5d6e7f8g9h0uQmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: dr.arjun  | Password: ArjunMedCare$77    | ROLE_DOCTOR
(4, 'dr.arjun',  '$2b$10$p1Q2wE3r4t5y6u7i8o9p0uJmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: aarav.s   | Password: AaravHealth@45     | ROLE_PATIENT
(5, 'aarav.s',   '$2b$10$a1b2c3d4e5f6g7h8i9j0keQmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: diya.p    | Password: DiyaPatient%88     | ROLE_PATIENT
(6, 'diya.p',    '$2b$10$z9x8c7v6b5n4m3k2j1h0geQmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: dishant.v | Password: DishantCare_12     | ROLE_PATIENT
(7, 'dishant.v', '$2b$10$q5w4e3r2t1y0u9i8o7p6ueQmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: neha.i    | Password: NehaCheckup$23     | ROLE_PATIENT
(8, 'neha.i',    '$2b$10$l1k2j3h4g5f6d7s8a9p0ueQmK6.rVv/uDuxuX96s4fGg43A1n8yDG'),

-- Username: kabir.s   | Password: KabirClinic#56     | ROLE_PATIENT
(9, 'kabir.s',   '$2b$10$v9b8n7m6g5f4d3s2a1q0ueQmK6.rVv/uDuxuX96s4fGg43A1n8yDG');

-- ── 2. USER ROLES ─────────────────────────────────────────────────────────

INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_DOCTOR'),
(3, 'ROLE_DOCTOR'),
(4, 'ROLE_DOCTOR'),
(5, 'ROLE_PATIENT'),
(6, 'ROLE_PATIENT'),
(7, 'ROLE_PATIENT'),
(8, 'ROLE_PATIENT'),
(9, 'ROLE_PATIENT');


-- ── 3. INSURANCE (5 policies — one per patient) ───────────────────────────

INSERT INTO insurance (id, policy_number, provider, valid_until, create_at) VALUES
(1, 'POL-2024-0001', 'Star Health',        '2026-12-31', NOW()),
(2, 'POL-2024-0002', 'HDFC Ergo',          '2027-06-30', NOW()),
(3, 'POL-2024-0003', 'Bajaj Allianz',      '2026-09-15', NOW()),
(4, 'POL-2024-0004', 'New India Assurance','2027-03-20', NOW()),
(5, 'POL-2024-0005', 'ICICI Lombard',      '2026-11-01', NOW());


-- ── 4. DOCTORS ────────────────────────────────────────────────────────────

INSERT INTO doctor (id, name, specialization, email, user_id) VALUES
(1, 'Dr. Rakesh Mehta', 'Cardiology',  'rakesh.mehta@hospital.com', 2),
(2, 'Dr. Sneha Kapoor', 'Dermatology', 'sneha.kapoor@hospital.com', 3),
(3, 'Dr. Arjun Nair',   'Orthopedics', 'arjun.nair@hospital.com',   4);


-- ── 5. PATIENTS ───────────────────────────────────────────────────────────

INSERT INTO patient (id, name, birth_date, email, gender, blood_group, user_id, patient_insurance_id) VALUES
(1, 'Aarav Sharma',  '1990-05-10', 'aarav.sharma@gmail.com',  'MALE',   'O_POSITIVE',  5, 1),
(2, 'Diya Patel',    '1995-08-20', 'diya.patel@gmail.com',    'FEMALE', 'A_POSITIVE',  6, 2),
(3, 'Dishant Verma', '1988-03-15', 'dishant.verma@gmail.com', 'MALE',   'A_POSITIVE',  7, 3),
(4, 'Neha Iyer',     '1992-12-01', 'neha.iyer@gmail.com',     'FEMALE', 'AB_POSITIVE', 8, 4),
(5, 'Kabir Singh',   '1993-07-11', 'kabir.singh@gmail.com',   'MALE',   'O_POSITIVE',  9, 5);


-- ── 6. DEPARTMENTS ───────────────────────────────────────────────────────

INSERT INTO department (id, name, head_doctor_id) VALUES
(1, 'Cardiology',  1),
(2, 'Dermatology', 2),
(3, 'Orthopedics', 3);


-- ── 7. DEPARTMENT ↔ DOCTOR (many-to-many join table) ─────────────────────
--    Dr. Rakesh also covers Dermatology
--    Dr. Arjun  also covers Cardiology

INSERT INTO my_dpt_doctors (dpt_id, doctor_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(2, 1),
(1, 3);


-- ── 8. APPOINTMENTS ──────────────────────────────────────────────────────

INSERT INTO appointment (id, reason, appointment_time, patient_id, doctor_id) VALUES
(1,  'Chest pain checkup',     '2025-07-01 10:30:00', 1, 1),
(2,  'Routine heart checkup',  '2025-07-02 09:00:00', 2, 1),
(3,  'Skin rash evaluation',   '2025-07-02 11:00:00', 2, 2),
(4,  'Acne treatment',         '2025-07-03 14:00:00', 4, 2),
(5,  'Knee pain',              '2025-07-03 09:45:00', 3, 3),
(6,  'Post-surgery follow-up', '2025-07-04 15:30:00', 5, 3),
(7,  'Follow-up visit',        '2025-07-05 10:00:00', 1, 1),
(8,  'Allergy consultation',   '2025-07-06 08:30:00', 4, 2),
(9,  'Blood pressure review',  '2025-07-07 11:15:00', 3, 1),
(10, 'Shoulder pain',          '2025-07-08 16:00:00', 2, 3);