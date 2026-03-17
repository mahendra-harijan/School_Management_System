package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudentManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private JTextField searchField;

    public StudentManagementPanel() {
        studentDAO = new StudentDAO(); userDAO = new UserDAO(); classDAO = new ClassDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        JLabel title = UIConstants.createTitleLabel("Student Management");
        header.add(title, BorderLayout.WEST);
        JButton addBtn = UIConstants.createPrimaryButton("+ Add Student");
        addBtn.addActionListener(e -> showStudentDialog(null));
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setOpaque(false);
        searchField = UIConstants.createStyledTextField();
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.putClientProperty("JTextField.placeholderText", "Search students...");
        JButton searchBtn = UIConstants.createSecondaryButton("Search");
        JButton refreshBtn = UIConstants.createSecondaryButton("↻ Refresh");
        searchPanel.add(new JLabel("Search: ")); searchPanel.add(searchField);
        searchPanel.add(searchBtn); searchPanel.add(refreshBtn);
        searchField.addActionListener(e -> filterTable());
        searchBtn.addActionListener(e -> filterTable());
        refreshBtn.addActionListener(e -> loadData());

        // Table
        String[] cols = {"ID", "Roll No", "Full Name", "Class", "Gender", "Guardian", "Email", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIConstants.createStyledTable();
        table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIConstants.BORDER_COLOR, 1));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionPanel.setOpaque(false);
        JButton editBtn = UIConstants.createSecondaryButton("✏ Edit");
        JButton deleteBtn = UIConstants.createDangerButton("🗑 Delete");
        JButton viewBtn = UIConstants.createPrimaryButton("👁 View Details");
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        viewBtn.addActionListener(e -> viewSelected());
        actionPanel.add(viewBtn); actionPanel.add(editBtn); actionPanel.add(deleteBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getRollNumber(), s.getFullName(),
                s.getClassName(), s.getGender(), s.getGuardianName(),
                s.getEmail(), s.getPhone()
            });
        }
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Student s : studentDAO.getAllStudents()) {
            if (s.getFullName().toLowerCase().contains(query) ||
                s.getRollNumber().toLowerCase().contains(query) ||
                (s.getClassName() != null && s.getClassName().toLowerCase().contains(query))) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getRollNumber(), s.getFullName(),
                    s.getClassName(), s.getGender(), s.getGuardianName(), s.getEmail(), s.getPhone()
                });
            }
        }
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) { UIConstants.showError(this, "Please select a student."); return -1; }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void editSelected() {
        int id = getSelectedId(); if (id < 0) return;
        showStudentDialog(studentDAO.getStudentById(id));
    }

    private void deleteSelected() {
        int id = getSelectedId(); if (id < 0) return;
        if (UIConstants.confirmDialog(this, "Delete this student? This cannot be undone.")) {
            if (studentDAO.deleteStudent(id)) { UIConstants.showSuccess(this, "Student deleted."); loadData(); }
            else UIConstants.showError(this, "Failed to delete student.");
        }
    }

    private void viewSelected() {
        int id = getSelectedId(); if (id < 0) return;
        Student s = studentDAO.getStudentById(id);
        if (s == null) return;
        String info = String.format("Name: %s\nRoll: %s\nClass: %s\nGender: %s\nGuardian: %s\nGuardian Phone: %s\nEmail: %s\nPhone: %s\nAdmission: %s",
            s.getFullName(), s.getRollNumber(), s.getClassName(), s.getGender(),
            s.getGuardianName(), s.getGuardianPhone(), s.getEmail(), s.getPhone(), s.getAdmissionDate());
        JOptionPane.showMessageDialog(this, info, "Student Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStudentDialog(Student existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing == null ? "Add Student" : "Edit Student", true);
        dialog.setSize(480, 520); dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 6, 6, 6);

        JTextField nameField = UIConstants.createStyledTextField();
        JTextField emailField = UIConstants.createStyledTextField();
        JTextField phoneField = UIConstants.createStyledTextField();
        JTextField rollField = UIConstants.createStyledTextField();
        JTextField guardianField = UIConstants.createStyledTextField();
        JTextField guardianPhoneField = UIConstants.createStyledTextField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        List<SchoolClass> classes = classDAO.getAllClasses();
        JComboBox<SchoolClass> classCombo = new JComboBox<>(classes.toArray(new SchoolClass[0]));

        if (existing != null) {
            nameField.setText(existing.getFullName()); emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhone()); rollField.setText(existing.getRollNumber());
            guardianField.setText(existing.getGuardianName()); guardianPhoneField.setText(existing.getGuardianPhone());
            if (existing.getGender() != null) genderCombo.setSelectedItem(existing.getGender());
            for (int i = 0; i < classes.size(); i++) if (classes.get(i).getId() == existing.getClassId()) { classCombo.setSelectedIndex(i); break; }
            nameField.setEditable(false); // Name/username managed via user separately
        }

        String[][] fields = {{"Full Name *", null}, {"Email", null}, {"Phone", null}, {"Roll Number *", null}, {"Class *", null}, {"Gender", null}, {"Guardian Name", null}, {"Guardian Phone", null}};
        JComponent[] components = {nameField, emailField, phoneField, rollField, classCombo, genderCombo, guardianField, guardianPhoneField};

        for (int i = 0; i < fields.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.3;
            JLabel lbl = new JLabel(fields[i][0]); lbl.setFont(UIConstants.FONT_SUBHEADING); panel.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 0.7; panel.add(components[i], gc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = UIConstants.createSecondaryButton("Cancel");
        JButton saveBtn = UIConstants.createPrimaryButton("Save");
        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            if (rollField.getText().trim().isEmpty()) { UIConstants.showError(dialog, "Roll number required."); return; }
            SchoolClass cls = (SchoolClass) classCombo.getSelectedItem();

            if (existing == null) {
                // Need to create user first - redirect to register
                UIConstants.showError(dialog, "Use the Register page to add new students. This dialog is for editing existing students.");
                return;
            }
            // Update existing
            existing.setEmail(emailField.getText().trim()); existing.setPhone(phoneField.getText().trim());
            existing.setRollNumber(rollField.getText().trim());
            if (cls != null) existing.setClassId(cls.getId());
            existing.setGender((String) genderCombo.getSelectedItem());
            existing.setGuardianName(guardianField.getText().trim()); existing.setGuardianPhone(guardianPhoneField.getText().trim());

            User u = userDAO.getUserById(existing.getUserId());
            if (u != null) { u.setEmail(emailField.getText().trim()); u.setPhone(phoneField.getText().trim()); userDAO.updateUser(u); }
            if (studentDAO.updateStudent(existing)) { UIConstants.showSuccess(dialog, "Student updated."); loadData(); dialog.dispose(); }
            else UIConstants.showError(dialog, "Update failed.");
        });
        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);
        gc.gridy = fields.length; gc.gridx = 0; gc.gridwidth = 2; panel.add(btnPanel, gc);

        dialog.add(new JScrollPane(panel)); dialog.setVisible(true);
    }
}
