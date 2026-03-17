# 🎓 SchoolPro — School Management System

A **full-featured desktop School Management System** built using **Java Swing** and **MySQL**, designed to streamline academic and administrative operations with **role-based access control** for Admin, Teacher, and Student users.

This application demonstrates **real-world software engineering practices**, including layered architecture (DAO pattern), modular UI design, and database integration using JDBC.

---

## 🚀 Project Description

**SchoolPro** is a robust desktop application that allows educational institutions to efficiently manage:

- Student records and academic performance
- Teacher assignments and subject management
- Attendance tracking and reporting
- Fee management and payment status
- Role-based dashboards with personalized data

The system is designed with a **clean UI**, **scalable backend structure**, and **efficient database handling**, making it suitable for both learning and real-world adaptation.

---

## ✨ Key Features

### 🔐 Role-Based Access
- **Admin** → Full system control
- **Teacher** → Manage marks & attendance
- **Student** → View performance & records

---

### 👨‍💼 Admin Features
- Manage students, teachers, classes, and subjects
- View dashboard analytics (total students, fees, etc.)
- Manage attendance and marks
- Handle fee records and reports
- Generate academic insights

---

### 👩‍🏫 Teacher Features
- View assigned classes and subjects
- Add/update student marks
- Mark daily attendance
- Monitor student performance

---

### 👨‍🎓 Student Features
- View marks and grades
- Track attendance percentage
- Access report cards
- Check fee payment status
- View profile details

---

## 🏗️ Project Structure
```
SchoolManagementSystem/
├── pom.xml                         # Maven build file
├── build.sh                        # Quick build script
├── src/main/
│   ├── resources/
│   │   └── schema.sql              # Database initialization script
│   └── java/com/school/
│       ├── Main.java               # Application entry point
│       ├── model/                  # Data models (POJOs)
│       │   ├── User.java
│       │   ├── Student.java
│       │   ├── Teacher.java
│       │   ├── SchoolClass.java
│       │   ├── Subject.java
│       │   ├── Marks.java
│       │   ├── Attendance.java
│       │   └── Fee.java
│       ├── database/               # DAO layer (JDBC)
│       │   ├── DatabaseConnection.java
│       │   ├── UserDAO.java
│       │   ├── StudentDAO.java
│       │   ├── TeacherDAO.java
│       │   ├── ClassDAO.java
│       │   ├── SubjectDAO.java
│       │   ├── MarksDAO.java
│       │   ├── AttendanceDAO.java
│       │   └── FeeDAO.java
│       ├── util/                   # Utilities & shared helpers
│       │   ├── UIConstants.java    # Colors, fonts, shared widgets
│       │   └── SessionManager.java # Logged-in user session
│       └── gui/                    # Swing UI layer
│           ├── auth/
│           │   ├── LoginFrame.java
│           │   └── RegisterFrame.java
│           ├── components/
│           │   └── BaseDashboard.java
│           ├── admin/
│           │   ├── AdminDashboard.java
│           │   ├── StudentManagementPanel.java
│           │   ├── TeacherManagementPanel.java
│           │   ├── ClassManagementPanel.java
│           │   ├── SubjectManagementPanel.java
│           │   ├── MarksManagementPanel.java
│           │   ├── AttendanceManagementPanel.java
│           │   ├── FeeManagementPanel.java
│           │   └── ReportsPanel.java
│           ├── teacher/
│           │   └── TeacherDashboard.java
│           └── student/
│               └── StudentDashboard.java
```

---

## ⚙️ Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17 or higher |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---
📸 Screenshots

Add screenshots here
Example:

![Dashboard](screenshots/dashboard.png)
