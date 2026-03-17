-- ============================================================
-- School Management System - Database Schema
-- Compatible with Java DAO layer
-- ============================================================

CREATE DATABASE IF NOT EXISTS school_management;
USE school_management;

-- ──────────────── USERS ────────────────
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ──────────────── CLASSES ────────────────
CREATE TABLE IF NOT EXISTS classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    section VARCHAR(10) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    teacher_id INT DEFAULT NULL,
    UNIQUE KEY unique_class (class_name, section, academic_year)
);

-- ──────────────── SUBJECTS ────────────────
CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) UNIQUE NOT NULL,
    class_id INT DEFAULT NULL,
    teacher_id INT DEFAULT NULL,
    max_marks INT DEFAULT 100
);

-- ──────────────── STUDENTS ────────────────
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    class_id INT DEFAULT NULL,
    date_of_birth DATE DEFAULT NULL,
    gender ENUM('Male', 'Female', 'Other') DEFAULT NULL,
    address TEXT,
    guardian_name VARCHAR(100),
    guardian_phone VARCHAR(20),
    admission_date DATE DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id)
);

-- ──────────────── TEACHERS ────────────────
CREATE TABLE IF NOT EXISTS teachers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(100),
    qualification VARCHAR(100),
    joining_date DATE DEFAULT NULL,
    salary DECIMAL(10,2) DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ──────────────── MARKS ────────────────
CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    exam_type VARCHAR(50) NOT NULL,
    marks_obtained DECIMAL(5,2) DEFAULT NULL,
    max_marks INT DEFAULT 100,
    exam_date DATE DEFAULT NULL,
    remarks VARCHAR(255),
    entered_by INT DEFAULT NULL,
    entered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (entered_by) REFERENCES users(id)
);

-- ──────────────── ATTENDANCE ────────────────
CREATE TABLE IF NOT EXISTS attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    class_id INT DEFAULT NULL,
    subject_id INT DEFAULT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL DEFAULT 'Present',
    remarks VARCHAR(255),
    marked_by INT DEFAULT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (marked_by) REFERENCES users(id),
    UNIQUE KEY unique_attendance (student_id, attendance_date)
);

-- ──────────────── FEES ────────────────
CREATE TABLE IF NOT EXISTS fees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    fee_type VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE DEFAULT NULL,
    paid_date DATE DEFAULT NULL,
    status ENUM('Pending', 'Paid', 'Overdue', 'Waived') DEFAULT 'Pending',
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    academic_year VARCHAR(20),
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- ──────────────── NOTIFICATIONS ────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    target_role ENUM('ALL', 'ADMIN', 'TEACHER', 'STUDENT') DEFAULT 'ALL',
    target_user_id INT DEFAULT NULL,
    created_by INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ──────────────── SEED DATA ────────────────
-- Default admin account  (username: admin / password: admin123)
INSERT IGNORE INTO users (username, password, role, full_name, email)
VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@school.edu');

-- Sample classes
INSERT IGNORE INTO classes (class_name, section, academic_year) VALUES
('Grade 9',  'A', '2024-2025'),
('Grade 9',  'B', '2024-2025'),
('Grade 10', 'A', '2024-2025'),
('Grade 10', 'B', '2024-2025'),
('Grade 11', 'A', '2024-2025'),
('Grade 12', 'A', '2024-2025');

-- Sample subjects (linked to Grade 10-A  = class id 3)
INSERT IGNORE INTO subjects (subject_name, subject_code, class_id, max_marks) VALUES
('Mathematics',      'MATH-10A', 3, 100),
('Science',          'SCI-10A',  3, 100),
('English',          'ENG-10A',  3, 100),
('Social Studies',   'SOC-10A',  3, 100),
('Computer Science', 'CS-10A',   3, 100);

-- Sample notifications
INSERT IGNORE INTO notifications (title, message, target_role, created_by)
VALUES ('Welcome!', 'Welcome to SchoolPro Management System. Please update your profile.', 'ALL', 1);
