package com.school.database;

import com.school.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    private Connection conn;
    public TeacherDAO() { conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean createTeacher(Teacher t) {
        String sql = "INSERT INTO teachers (user_id, employee_id, department, qualification, salary) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getEmployeeId());
            ps.setString(3, t.getDepartment());
            ps.setString(4, t.getQualification());
            ps.setDouble(5, t.getSalary());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) t.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("Create teacher error: " + e.getMessage()); }
        return false;
    }

    public boolean updateTeacher(Teacher t) {
        String sql = "UPDATE teachers SET department=?, qualification=?, salary=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getDepartment());
            ps.setString(2, t.getQualification());
            ps.setDouble(3, t.getSalary());
            ps.setInt(4, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Update teacher error: " + e.getMessage()); }
        return false;
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT t.*, u.full_name, u.username, u.email, u.phone FROM teachers t JOIN users u ON t.user_id=u.id WHERE u.is_active=TRUE ORDER BY u.full_name";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapTeacher(rs));
        } catch (SQLException e) { System.err.println("Get all teachers error: " + e.getMessage()); }
        return list;
    }

    public Teacher getTeacherByUserId(int userId) {
        String sql = "SELECT t.*, u.full_name, u.username, u.email, u.phone FROM teachers t JOIN users u ON t.user_id=u.id WHERE t.user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapTeacher(rs);
        } catch (SQLException e) { System.err.println("Get teacher error: " + e.getMessage()); }
        return null;
    }

    public boolean employeeIdExists(String empId) {
        String sql = "SELECT id FROM teachers WHERE employee_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId); return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private Teacher mapTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id")); t.setUserId(rs.getInt("user_id"));
        t.setEmployeeId(rs.getString("employee_id")); t.setDepartment(rs.getString("department"));
        t.setQualification(rs.getString("qualification")); t.setSalary(rs.getDouble("salary"));
        t.setFullName(rs.getString("full_name")); t.setUsername(rs.getString("username"));
        t.setEmail(rs.getString("email")); t.setPhone(rs.getString("phone"));
        try { t.setJoiningDate(rs.getDate("joining_date")); } catch (SQLException e) {}
        return t;
    }
}
