package com.school.database;

import com.school.model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    private Connection conn;
    public SubjectDAO() { conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean createSubject(Subject s) {
        String sql = "INSERT INTO subjects (subject_name, subject_code, class_id, teacher_id, max_marks) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getSubjectName()); ps.setString(2, s.getSubjectCode());
            if (s.getClassId() > 0) ps.setInt(3, s.getClassId()); else ps.setNull(3, Types.INTEGER);
            if (s.getTeacherId() > 0) ps.setInt(4, s.getTeacherId()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, s.getMaxMarks());
            int rows = ps.executeUpdate();
            if (rows > 0) { ResultSet k = ps.getGeneratedKeys(); if (k.next()) s.setId(k.getInt(1)); return true; }
        } catch (SQLException e) { System.err.println("Create subject error: " + e.getMessage()); }
        return false;
    }

    public boolean updateSubject(Subject s) {
        String sql = "UPDATE subjects SET subject_name=?, class_id=?, teacher_id=?, max_marks=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectName());
            if (s.getClassId() > 0) ps.setInt(2, s.getClassId()); else ps.setNull(2, Types.INTEGER);
            if (s.getTeacherId() > 0) ps.setInt(3, s.getTeacherId()); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, s.getMaxMarks()); ps.setInt(5, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Update subject error: " + e.getMessage()); }
        return false;
    }

    public boolean deleteSubject(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM subjects WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT sub.*, CONCAT(c.class_name,' - ',c.section) AS class_name, u.full_name AS teacher_name " +
                     "FROM subjects sub LEFT JOIN classes c ON sub.class_id=c.id LEFT JOIN users u ON sub.teacher_id=u.id ORDER BY sub.subject_name";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql); while (rs.next()) list.add(mapSubject(rs));
        } catch (SQLException e) { System.err.println("Get subjects error: " + e.getMessage()); }
        return list;
    }

    public List<Subject> getSubjectsByClass(int classId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT sub.*, CONCAT(c.class_name,' - ',c.section) AS class_name, u.full_name AS teacher_name " +
                     "FROM subjects sub LEFT JOIN classes c ON sub.class_id=c.id LEFT JOIN users u ON sub.teacher_id=u.id " +
                     "WHERE sub.class_id=? ORDER BY sub.subject_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSubject(rs));
        } catch (SQLException e) { System.err.println("Get subjects by class error: " + e.getMessage()); }
        return list;
    }

    public List<Subject> getSubjectsByTeacher(int teacherId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT sub.*, CONCAT(c.class_name,' - ',c.section) AS class_name, u.full_name AS teacher_name " +
                     "FROM subjects sub LEFT JOIN classes c ON sub.class_id=c.id LEFT JOIN users u ON sub.teacher_id=u.id " +
                     "WHERE sub.teacher_id=? ORDER BY sub.subject_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSubject(rs));
        } catch (SQLException e) { System.err.println("Get subjects by teacher error: " + e.getMessage()); }
        return list;
    }

    private Subject mapSubject(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setId(rs.getInt("id")); s.setSubjectName(rs.getString("subject_name"));
        s.setSubjectCode(rs.getString("subject_code")); s.setMaxMarks(rs.getInt("max_marks"));
        try { s.setClassId(rs.getInt("class_id")); s.setClassName(rs.getString("class_name")); } catch (SQLException e) {}
        try { s.setTeacherId(rs.getInt("teacher_id")); s.setTeacherName(rs.getString("teacher_name")); } catch (SQLException e) {}
        return s;
    }
}
