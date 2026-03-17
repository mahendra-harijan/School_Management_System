package com.school.database;

import com.school.model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private Connection conn;
    public AttendanceDAO() { conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean markAttendance(Attendance a) {
        String sql = "INSERT INTO attendance (student_id, class_id, attendance_date, status, subject_id, marked_by, remarks) VALUES (?,?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE status=VALUES(status), remarks=VALUES(remarks)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getStudentId());
            if (a.getClassId() > 0) ps.setInt(2, a.getClassId()); else ps.setNull(2, Types.INTEGER);
            ps.setDate(3, a.getAttendanceDate()); ps.setString(4, a.getStatus());
            if (a.getSubjectId() > 0) ps.setInt(5, a.getSubjectId()); else ps.setNull(5, Types.INTEGER);
            if (a.getMarkedBy() > 0) ps.setInt(6, a.getMarkedBy()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, a.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Mark attendance error: " + e.getMessage()); }
        return false;
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS student_name, s2.roll_number, " +
                     "CONCAT(c.class_name,' - ',c.section) AS class_name " +
                     "FROM attendance a JOIN students s2 ON a.student_id=s2.id JOIN users u ON s2.user_id=u.id " +
                     "LEFT JOIN classes c ON a.class_id=c.id WHERE a.student_id=? ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        } catch (SQLException e) { System.err.println("Get attendance by student error: " + e.getMessage()); }
        return list;
    }

    public List<Attendance> getAttendanceByClassAndDate(int classId, Date date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS student_name, s2.roll_number, " +
                     "CONCAT(c.class_name,' - ',c.section) AS class_name " +
                     "FROM attendance a JOIN students s2 ON a.student_id=s2.id JOIN users u ON s2.user_id=u.id " +
                     "LEFT JOIN classes c ON a.class_id=c.id WHERE a.class_id=? AND a.attendance_date=? ORDER BY s2.roll_number";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId); ps.setDate(2, date); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        } catch (SQLException e) { System.err.println("Get attendance by class/date error: " + e.getMessage()); }
        return list;
    }

    public double getAttendancePercentage(int studentId) {
        String sql = "SELECT COUNT(*) AS total, SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present FROM attendance WHERE student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total"); int present = rs.getInt("present");
                if (total == 0) return 0; return (double) present / total * 100;
            }
        } catch (SQLException e) { System.err.println("Get attendance percentage error: " + e.getMessage()); }
        return 0;
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id")); a.setStudentId(rs.getInt("student_id"));
        a.setAttendanceDate(rs.getDate("attendance_date")); a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        try { a.setStudentName(rs.getString("student_name")); a.setRollNumber(rs.getString("roll_number")); } catch (SQLException e) {}
        try { a.setClassId(rs.getInt("class_id")); a.setClassName(rs.getString("class_name")); } catch (SQLException e) {}
        return a;
    }
}
