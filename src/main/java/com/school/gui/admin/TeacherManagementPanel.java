package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class TeacherManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TeacherDAO teacherDAO;
    private UserDAO userDAO;

    public TeacherManagementPanel() {
        teacherDAO = new TeacherDAO(); userDAO = new UserDAO();
        initUI(); loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout()); setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false); header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.add(UIConstants.createTitleLabel("Teacher Management"), BorderLayout.WEST);
        JButton addBtn = UIConstants.createPrimaryButton("+ Add Teacher");
        addBtn.addActionListener(e -> UIConstants.showError(this, "Use the Register page to add new teachers."));
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Emp ID", "Name", "Department", "Qualification", "Email", "Phone", "Salary"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = UIConstants.createStyledTable(); table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIConstants.BORDER_COLOR, 1));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionPanel.setOpaque(false);
        JButton editBtn = UIConstants.createSecondaryButton("✏ Edit");
        JButton deleteBtn = UIConstants.createDangerButton("🗑 Delete");
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        actionPanel.add(editBtn); actionPanel.add(deleteBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Teacher t : teacherDAO.getAllTeachers()) {
            tableModel.addRow(new Object[]{ t.getId(), t.getEmployeeId(), t.getFullName(),
                t.getDepartment(), t.getQualification(), t.getEmail(), t.getPhone(),
                t.getSalary() > 0 ? String.format("₹%.2f", t.getSalary()) : "-" });
        }
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) { UIConstants.showError(this, "Please select a teacher."); return -1; }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void editSelected() {
        int id = getSelectedId(); if (id < 0) return;
        Teacher t = teacherDAO.getAllTeachers().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (t == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Teacher", true);
        dialog.setSize(420, 380); dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 6, 6, 6);

        JTextField nameField = UIConstants.createStyledTextField(); nameField.setText(t.getFullName()); nameField.setEditable(false);
        JTextField emailField = UIConstants.createStyledTextField(); emailField.setText(t.getEmail());
        JTextField phoneField = UIConstants.createStyledTextField(); phoneField.setText(t.getPhone());
        JTextField deptField = UIConstants.createStyledTextField(); deptField.setText(t.getDepartment());
        JTextField qualField = UIConstants.createStyledTextField(); qualField.setText(t.getQualification());
        JTextField salaryField = UIConstants.createStyledTextField(); salaryField.setText(String.valueOf(t.getSalary()));

        String[][] rows = {{"Name", null}, {"Email", null}, {"Phone", null}, {"Department", null}, {"Qualification", null}, {"Salary", null}};
        JComponent[] comps = {nameField, emailField, phoneField, deptField, qualField, salaryField};
        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.3;
            JLabel lbl = new JLabel(rows[i][0]); lbl.setFont(UIConstants.FONT_SUBHEADING); panel.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 0.7; panel.add(comps[i], gc);
        }

        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = UIConstants.createPrimaryButton("Save");
        save.addActionListener(e -> {
            t.setDepartment(deptField.getText().trim()); t.setQualification(qualField.getText().trim());
            try { t.setSalary(Double.parseDouble(salaryField.getText().trim())); } catch (NumberFormatException ex) {}
            User u = userDAO.getUserById(t.getUserId());
            if (u != null) { u.setEmail(emailField.getText().trim()); u.setPhone(phoneField.getText().trim()); userDAO.updateUser(u); }
            if (teacherDAO.updateTeacher(t)) { UIConstants.showSuccess(dialog, "Teacher updated."); loadData(); dialog.dispose(); }
            else UIConstants.showError(dialog, "Update failed.");
        });
        btnPnl.add(UIConstants.createSecondaryButton("Cancel")); btnPnl.getComponent(0); 
        ((JButton)btnPnl.getComponent(0)).addActionListener(e2 -> dialog.dispose());
        btnPnl.add(save);
        gc.gridy = rows.length; gc.gridx = 0; gc.gridwidth = 2; panel.add(btnPnl, gc);
        dialog.add(panel); dialog.setVisible(true);
    }

    private void deleteSelected() {
        int id = getSelectedId(); if (id < 0) return;
        Teacher t = teacherDAO.getAllTeachers().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (t == null) return;
        if (UIConstants.confirmDialog(this, "Delete teacher " + t.getFullName() + "?")) {
            if (userDAO.deleteUser(t.getUserId())) { UIConstants.showSuccess(this, "Teacher deleted."); loadData(); }
            else UIConstants.showError(this, "Failed to delete teacher.");
        }
    }
}
