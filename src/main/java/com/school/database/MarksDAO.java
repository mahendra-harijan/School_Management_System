package com.school.database;

import com.school.model.Marks;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {
    private Connection conn;
    public MarksDAO() { conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean addMarks(Marks m) {
        String sql = "INSERT INTO marks (student_id, subject_id, exam_type, marks_obtained, max_marks, exam_date, remarks, entered_by) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getStudentId()); ps.setInt(2, m.getSubjectId());
            ps.setString(3, m.getExamType()); ps.setDouble(4, m.getMarksObtained());
            ps.setInt(5, m.getMaxMarks());
            if (m.getExamDate() != null) ps.setDate(6, m.getExamDate()); else ps.setNull(6, Types.DATE);
            ps.setString(7, m.getRemarks()); ps.setInt(8, m.getEnteredBy());
            int rows = ps.executeUpdate();
            if (rows > 0) { ResultSet k = ps.getGeneratedKeys(); if (k.next()) m.setId(k.getInt(1)); return true; }
        } catch (SQLException e) { System.err.println("Add marks error: " + e.getMessage()); }
        return false;
    }

    public boolean updateMarks(Marks m) {
        String sql = "UPDATE marks SET marks_obtained=?, max_marks=?, exam_date=?, remarks=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, m.getMarksObtained()); ps.setInt(2, m.getMaxMarks());
            if (m.getExamDate() != null) ps.setDate(3, m.getExamDate()); else ps.setNull(3, Types.DATE);
            ps.setString(4, m.getRemarks()); ps.setInt(5, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Update marks error: " + e.getMessage()); }
        return false;
    }

    public boolean deleteMarks(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM marks WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Marks> getMarksByStudent(int studentId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name AS student_name, s2.roll_number, sub.subject_name " +
                     "FROM marks m JOIN students s2 ON m.student_id=s2.id JOIN users u ON s2.user_id=u.id " +
                     "JOIN subjects sub ON m.subject_id=sub.id WHERE m.student_id=? ORDER BY m.exam_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMarks(rs));
        } catch (SQLException e) { System.err.println("Get marks by student error: " + e.getMessage()); }
        return list;
    }

    public List<Marks> getMarksByClassAndSubject(int classId, int subjectId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name AS student_name, s2.roll_number, sub.subject_name " +
                     "FROM marks m JOIN students s2 ON m.student_id=s2.id JOIN users u ON s2.user_id=u.id " +
                     "JOIN subjects sub ON m.subject_id=sub.id " +
                     "WHERE s2.class_id=? AND m.subject_id=? ORDER BY s2.roll_number";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId); ps.setInt(2, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMarks(rs));
        } catch (SQLException e) { System.err.println("Get marks by class/subject error: " + e.getMessage()); }
        return list;
    }

    public List<Marks> getAllMarks() {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name AS student_name, s2.roll_number, sub.subject_name " +
                     "FROM marks m JOIN students s2 ON m.student_id=s2.id JOIN users u ON s2.user_id=u.id " +
                     "JOIN subjects sub ON m.subject_id=sub.id ORDER BY m.entered_at DESC";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql); while (rs.next()) list.add(mapMarks(rs));
        } catch (SQLException e) { System.err.println("Get all marks error: " + e.getMessage()); }
        return list;
    }

    private Marks mapMarks(ResultSet rs) throws SQLException {
        Marks m = new Marks();
        m.setId(rs.getInt("id")); m.setStudentId(rs.getInt("student_id"));
        m.setSubjectId(rs.getInt("subject_id")); m.setExamType(rs.getString("exam_type"));
        m.setMarksObtained(rs.getDouble("marks_obtained")); m.setMaxMarks(rs.getInt("max_marks"));
        m.setExamDate(rs.getDate("exam_date")); m.setRemarks(rs.getString("remarks"));
        try { m.setStudentName(rs.getString("student_name")); m.setRollNumber(rs.getString("roll_number")); } catch (SQLException e) {}
        try { m.setSubjectName(rs.getString("subject_name")); } catch (SQLException e) {}
        try { m.setEnteredAt(rs.getTimestamp("entered_at")); } catch (SQLException e) {}
        return m;
    }
}
