package com.school.database;

import com.school.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection conn;

    public StudentDAO() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    public boolean createStudent(Student s) {
        String sql = "INSERT INTO students (user_id, roll_number, class_id, date_of_birth, gender, address, guardian_name, guardian_phone) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getRollNumber());
            ps.setInt(3, s.getClassId());
            ps.setDate(4, s.getDateOfBirth());
            ps.setString(5, s.getGender());
            ps.setString(6, s.getAddress());
            ps.setString(7, s.getGuardianName());
            ps.setString(8, s.getGuardianPhone());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) s.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Create student error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET roll_number=?, class_id=?, date_of_birth=?, gender=?, address=?, guardian_name=?, guardian_phone=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRollNumber());
            ps.setInt(2, s.getClassId());
            ps.setDate(3, s.getDateOfBirth());
            ps.setString(4, s.getGender());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getGuardianName());
            ps.setString(7, s.getGuardianPhone());
            ps.setInt(8, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update student error: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteStudent(int studentId) {
        // First get user_id
        Student s = getStudentById(studentId);
        if (s == null) return false;
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete student error: " + e.getMessage());
        }
        return false;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT s.*, u.full_name, u.username, u.email, u.phone, " +
                     "CONCAT(c.class_name, ' - ', c.section) AS class_name " +
                     "FROM students s JOIN users u ON s.user_id=u.id " +
                     "LEFT JOIN classes c ON s.class_id=c.id WHERE s.id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStudent(rs);
        } catch (SQLException e) {
            System.err.println("Get student error: " + e.getMessage());
        }
        return null;
    }

    public Student getStudentByUserId(int userId) {
        String sql = "SELECT s.*, u.full_name, u.username, u.email, u.phone, " +
                     "CONCAT(c.class_name, ' - ', c.section) AS class_name " +
                     "FROM students s JOIN users u ON s.user_id=u.id " +
                     "LEFT JOIN classes c ON s.class_id=c.id WHERE s.user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStudent(rs);
        } catch (SQLException e) {
            System.err.println("Get student by user error: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name, u.username, u.email, u.phone, " +
                     "CONCAT(c.class_name, ' - ', c.section) AS class_name " +
                     "FROM students s JOIN users u ON s.user_id=u.id " +
                     "LEFT JOIN classes c ON s.class_id=c.id " +
                     "WHERE u.is_active=TRUE ORDER BY u.full_name";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) {
            System.err.println("Get all students error: " + e.getMessage());
        }
        return list;
    }

    public List<Student> getStudentsByClass(int classId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name, u.username, u.email, u.phone, " +
                     "CONCAT(c.class_name, ' - ', c.section) AS class_name " +
                     "FROM students s JOIN users u ON s.user_id=u.id " +
                     "LEFT JOIN classes c ON s.class_id=c.id " +
                     "WHERE s.class_id=? AND u.is_active=TRUE ORDER BY s.roll_number";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) {
            System.err.println("Get students by class error: " + e.getMessage());
        }
        return list;
    }

    public boolean rollNumberExists(String rollNumber) {
        String sql = "SELECT id FROM students WHERE roll_number=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setClassId(rs.getInt("class_id"));
        s.setDateOfBirth(rs.getDate("date_of_birth"));
        s.setGender(rs.getString("gender"));
        s.setAddress(rs.getString("address"));
        s.setGuardianName(rs.getString("guardian_name"));
        s.setGuardianPhone(rs.getString("guardian_phone"));
        s.setAdmissionDate(rs.getDate("admission_date"));
        s.setFullName(rs.getString("full_name"));
        s.setUsername(rs.getString("username"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        try { s.setClassName(rs.getString("class_name")); } catch (SQLException e) {}
        return s;
    }
}
