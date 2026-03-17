package com.school.database;

import com.school.model.SchoolClass;
import com.school.model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {
    private Connection conn;
    public ClassDAO() { conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean createClass(SchoolClass c) {
        String sql = "INSERT INTO classes (class_name, section, academic_year, teacher_id) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getClassName()); ps.setString(2, c.getSection());
            ps.setString(3, c.getAcademicYear());
            if (c.getTeacherId() > 0) ps.setInt(4, c.getTeacherId()); else ps.setNull(4, Types.INTEGER);
            int rows = ps.executeUpdate();
            if (rows > 0) { ResultSet k = ps.getGeneratedKeys(); if (k.next()) c.setId(k.getInt(1)); return true; }
        } catch (SQLException e) { System.err.println("Create class error: " + e.getMessage()); }
        return false;
    }

    public boolean updateClass(SchoolClass c) {
        String sql = "UPDATE classes SET class_name=?, section=?, academic_year=?, teacher_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClassName()); ps.setString(2, c.getSection());
            ps.setString(3, c.getAcademicYear());
            if (c.getTeacherId() > 0) ps.setInt(4, c.getTeacherId()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Update class error: " + e.getMessage()); }
        return false;
    }

    public boolean deleteClass(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM classes WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Delete class error: " + e.getMessage()); return false; }
    }

    public List<SchoolClass> getAllClasses() {
        List<SchoolClass> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS teacher_name FROM classes c LEFT JOIN users u ON c.teacher_id=u.id ORDER BY c.class_name, c.section";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapClass(rs));
        } catch (SQLException e) { System.err.println("Get all classes error: " + e.getMessage()); }
        return list;
    }

    public SchoolClass getClassById(int id) {
        String sql = "SELECT c.*, u.full_name AS teacher_name FROM classes c LEFT JOIN users u ON c.teacher_id=u.id WHERE c.id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapClass(rs);
        } catch (SQLException e) { System.err.println("Get class error: " + e.getMessage()); }
        return null;
    }

    private SchoolClass mapClass(ResultSet rs) throws SQLException {
        SchoolClass c = new SchoolClass();
        c.setId(rs.getInt("id")); c.setClassName(rs.getString("class_name"));
        c.setSection(rs.getString("section")); c.setAcademicYear(rs.getString("academic_year"));
        try { c.setTeacherId(rs.getInt("teacher_id")); c.setTeacherName(rs.getString("teacher_name")); } catch (SQLException e) {}
        return c;
    }
}
