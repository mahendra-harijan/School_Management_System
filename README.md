# 🎓 SchoolPro — School Management System

A full-featured desktop application built with **Java Swing** and **MySQL**, implementing role-based access for **Admin**, **Teacher**, and **Student** users.

---

## 📦 Project Structure

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

## 🚀 Setup Instructions

### Step 1 — Clone / Download the Project

Place the project folder anywhere on your machine.

### Step 2 — Set Up the MySQL Database

1. Open MySQL Workbench or the MySQL CLI.
2. Run the schema script:
   ```sql
   source /path/to/SchoolManagementSystem/src/main/resources/schema.sql
   ```
   Or paste its contents into Workbench and execute.

This creates the `school_management` database with all tables and seed data.

### Step 3 — Configure Database Credentials

Edit `src/main/java/com/school/database/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/school_management?...";
private static final String USER = "root";           // ← your MySQL username
private static final String PASSWORD = "yourpass";   // ← your MySQL password
```

### Step 4 — Build the Project

```bash
cd SchoolManagementSystem
mvn clean package -q
```

This creates two JARs in `target/`:
- `school-management-system-1.0.0.jar` — standard JAR
- `school-management-system-1.0.0-fat.jar` — fat JAR with all dependencies bundled ✅

### Step 5 — Run the Application

```bash
java -jar target/school-management-system-1.0.0-fat.jar
```

Or double-click the fat JAR if your OS supports it.

---

## 🔐 Default Login

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |

After first login as admin, you can create Teacher and Student accounts from the Admin dashboard, or use the **Register** button on the login screen.

---

## ✨ Features by Role

### 👨‍💼 Administrator
- Full dashboard with live statistics (students, teachers, classes, fees)
- Add / Edit / Delete student records
- Add / Edit teacher accounts
- Manage classes and subjects
- View and update all marks
- Manage attendance records
- Full fee management (add, mark paid, view reports)
- Academic reports panel

### 👩‍🏫 Teacher
- Personal dashboard with subject and student counts
- View assigned subjects
- View students in their classes
- Enter and save marks per subject/exam type
- Mark daily attendance (Present / Absent / Late / Excused)
- View student performance with grade calculation

### 👨‍🎓 Student
- Personal dashboard with attendance %, average score, pending fees
- View all marks with colour-coded grades
- View attendance history with colour-coded status
- Full report card view
- Fee status and payment history
- Profile information

---

## 🎨 UI Design

- Dark blue sidebar navigation with icon buttons
- Blue gradient header with user info and logout
- Card-based stat panels on dashboards
- Styled JTable with blue headers and row highlighting
- Consistent color palette: Primary Blue, Success Green, Danger Red, Accent Orange

---

## 🗄️ Database Schema Overview

```
users          → all accounts (admin/teacher/student)
students       → student profile linked to users
teachers       → teacher profile linked to users
classes        → class/section/year combinations
subjects       → subjects linked to class and teacher
marks          → exam results per student/subject
attendance     → daily attendance records
fees           → student fee records and payment status
notifications  → system-wide or role/user-targeted messages
```

---

## 🛠️ Technologies Used

- **Java 17** — core language
- **Java Swing** — desktop GUI framework
- **MySQL 8** — relational database
- **JDBC** (mysql-connector-java 8.0.33) — database connectivity
- **Maven** — build and dependency management

---

## 📝 Notes

- Password storage uses plain text in this demo. For production, integrate BCrypt (e.g., `org.mindrot:jbcrypt`).
- The application requires MySQL to be running locally on port 3306.
- All GUI operations run DB queries on background `SwingWorker` threads to keep the UI responsive.
