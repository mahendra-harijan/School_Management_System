package com.school.database;

import com.school.model.Fee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {
    private Connection conn;
    public FeeDAO() { conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean addFee(Fee f) {
        String sql = "INSERT INTO fees (student_id, fee_type, amount, due_date, status, remarks) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getStudentId()); ps.setString(2, f.getFeeType());
            ps.setDouble(3, f.getAmount());
            if (f.getDueDate() != null) ps.setDate(4, f.getDueDate()); else ps.setNull(4, Types.DATE);
            ps.setString(5, f.getStatus() != null ? f.getStatus() : "Pending");
            ps.setString(6, f.getRemarks());
            int rows = ps.executeUpdate();
            if (rows > 0) { ResultSet k = ps.getGeneratedKeys(); if (k.next()) f.setId(k.getInt(1)); return true; }
        } catch (SQLException e) { System.err.println("Add fee error: " + e.getMessage()); }
        return false;
    }

    public boolean updateFee(Fee f) {
        String sql = "UPDATE fees SET fee_type=?, amount=?, due_date=?, paid_date=?, status=?, payment_method=?, transaction_id=?, remarks=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getFeeType()); ps.setDouble(2, f.getAmount());
            if (f.getDueDate() != null) ps.setDate(3, f.getDueDate()); else ps.setNull(3, Types.DATE);
            if (f.getPaidDate() != null) ps.setDate(4, f.getPaidDate()); else ps.setNull(4, Types.DATE);
            ps.setString(5, f.getStatus()); ps.setString(6, f.getPaymentMethod());
            ps.setString(7, f.getTransactionId()); ps.setString(8, f.getRemarks());
            ps.setInt(9, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Update fee error: " + e.getMessage()); }
        return false;
    }

    public List<Fee> getFeesByStudent(int studentId) {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT f.*, u.full_name AS student_name, s2.roll_number FROM fees f " +
                     "JOIN students s2 ON f.student_id=s2.id JOIN users u ON s2.user_id=u.id " +
                     "WHERE f.student_id=? ORDER BY f.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapFee(rs));
        } catch (SQLException e) { System.err.println("Get fees by student error: " + e.getMessage()); }
        return list;
    }

    public List<Fee> getAllFees() {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT f.*, u.full_name AS student_name, s2.roll_number FROM fees f " +
                     "JOIN students s2 ON f.student_id=s2.id JOIN users u ON s2.user_id=u.id ORDER BY f.created_at DESC";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql); while (rs.next()) list.add(mapFee(rs));
        } catch (SQLException e) { System.err.println("Get all fees error: " + e.getMessage()); }
        return list;
    }

    public double getTotalPendingFees() {
        String sql = "SELECT SUM(amount) FROM fees WHERE status='Pending' OR status='Overdue'";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql); if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println("Get pending fees error: " + e.getMessage()); }
        return 0;
    }

    private Fee mapFee(ResultSet rs) throws SQLException {
        Fee f = new Fee();
        f.setId(rs.getInt("id")); f.setStudentId(rs.getInt("student_id"));
        f.setFeeType(rs.getString("fee_type")); f.setAmount(rs.getDouble("amount"));
        f.setDueDate(rs.getDate("due_date")); f.setPaidDate(rs.getDate("paid_date"));
        f.setStatus(rs.getString("status")); f.setPaymentMethod(rs.getString("payment_method"));
        f.setTransactionId(rs.getString("transaction_id")); f.setRemarks(rs.getString("remarks"));
        f.setCreatedAt(rs.getTimestamp("created_at"));
        try { f.setStudentName(rs.getString("student_name")); f.setRollNumber(rs.getString("roll_number")); } catch (SQLException e) {}
        return f;
    }
}
