package com.school.gui.auth;

import com.school.database.StudentDAO;
import com.school.database.TeacherDAO;
import com.school.database.UserDAO;
import com.school.database.ClassDAO;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class RegisterFrame extends JDialog {
    private JTextField fullNameField, usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleCombo;
    // Student-specific
    private JTextField rollNumberField, guardianNameField, guardianPhoneField;
    private JComboBox<SchoolClass> classCombo;
    private JComboBox<String> genderCombo;
    // Teacher-specific
    private JTextField empIdField, departmentField, qualificationField;
    private JPanel extraPanel;
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private ClassDAO classDAO;

    public RegisterFrame(JFrame parent) {
        super(parent, "Register New User", true);
        userDAO = new UserDAO(); studentDAO = new StudentDAO();
        teacherDAO = new TeacherDAO(); classDAO = new ClassDAO();
        initUI();
    }

    private void initUI() {
        setSize(550, 680); setLocationRelativeTo(getParent()); setResizable(false);
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UIConstants.BG_COLOR);
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = UIConstants.createTitleLabel("Create Account");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIConstants.CARD_COLOR);
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(5, 5, 5, 5);
        gc.weightx = 1.0;

        fullNameField = UIConstants.createStyledTextField();
        usernameField = UIConstants.createStyledTextField();
        emailField = UIConstants.createStyledTextField();
        phoneField = UIConstants.createStyledTextField();
        passwordField = UIConstants.createStyledPasswordField();
        confirmPasswordField = UIConstants.createStyledPasswordField();
        roleCombo = new JComboBox<>(new String[]{"STUDENT", "TEACHER"});
        roleCombo.setFont(UIConstants.FONT_BODY);

        addFormRow(form, gc, 0, "Full Name *", fullNameField);
        addFormRow(form, gc, 1, "Username *", usernameField);
        addFormRow(form, gc, 2, "Email", emailField);
        addFormRow(form, gc, 3, "Phone", phoneField);
        addFormRow(form, gc, 4, "Password *", passwordField);
        addFormRow(form, gc, 5, "Confirm Password *", confirmPasswordField);
        addFormRow(form, gc, 6, "Role *", roleCombo);

        // Extra fields panel
        extraPanel = new JPanel(new GridBagLayout());
        extraPanel.setBackground(UIConstants.CARD_COLOR);
        gc.gridy = 7; gc.gridx = 0; gc.gridwidth = 2;
        form.add(extraPanel, gc);

        loadExtraFields();
        roleCombo.addActionListener(e -> loadExtraFields());

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        main.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UIConstants.BG_COLOR);
        JButton cancelBtn = UIConstants.createSecondaryButton("Cancel");
        JButton registerBtn = UIConstants.createPrimaryButton("Register");
        cancelBtn.addActionListener(e -> dispose());
        registerBtn.addActionListener(e -> handleRegister());
        btnPanel.add(cancelBtn); btnPanel.add(registerBtn);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);
    }

    private void addFormRow(JPanel form, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridy = row; gc.gridx = 0; gc.gridwidth = 1; gc.weightx = 0.3;
        JLabel lbl = new JLabel(label); lbl.setFont(UIConstants.FONT_SUBHEADING);
        form.add(lbl, gc);
        gc.gridx = 1; gc.weightx = 0.7; gc.gridwidth = 1;
        form.add(field, gc);
    }

    private void loadExtraFields() {
        extraPanel.removeAll();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(5, 5, 5, 5); gc.weightx = 1.0;

        if ("STUDENT".equals(roleCombo.getSelectedItem())) {
            rollNumberField = UIConstants.createStyledTextField();
            guardianNameField = UIConstants.createStyledTextField();
            guardianPhoneField = UIConstants.createStyledTextField();
            genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            genderCombo.setFont(UIConstants.FONT_BODY);

            List<SchoolClass> classes = classDAO.getAllClasses();
            classCombo = new JComboBox<>(classes.toArray(new SchoolClass[0]));
            classCombo.setFont(UIConstants.FONT_BODY);

            JSeparator sep = new JSeparator();
            gc.gridy = 0; gc.gridx = 0; gc.gridwidth = 2;
            JLabel secLabel = new JLabel("── Student Details ──");
            secLabel.setFont(UIConstants.FONT_SUBHEADING); secLabel.setForeground(UIConstants.PRIMARY_COLOR);
            extraPanel.add(secLabel, gc);

            addExtraRow(gc, 1, "Roll Number *", rollNumberField);
            addExtraRow(gc, 2, "Class *", classCombo);
            addExtraRow(gc, 3, "Gender", genderCombo);
            addExtraRow(gc, 4, "Guardian Name", guardianNameField);
            addExtraRow(gc, 5, "Guardian Phone", guardianPhoneField);
        } else {
            empIdField = UIConstants.createStyledTextField();
            departmentField = UIConstants.createStyledTextField();
            qualificationField = UIConstants.createStyledTextField();

            gc.gridy = 0; gc.gridx = 0; gc.gridwidth = 2;
            JLabel secLabel = new JLabel("── Teacher Details ──");
            secLabel.setFont(UIConstants.FONT_SUBHEADING); secLabel.setForeground(UIConstants.PRIMARY_COLOR);
            extraPanel.add(secLabel, gc);

            addExtraRow(gc, 1, "Employee ID *", empIdField);
            addExtraRow(gc, 2, "Department", departmentField);
            addExtraRow(gc, 3, "Qualification", qualificationField);
        }
        extraPanel.revalidate(); extraPanel.repaint();
        revalidate(); repaint();
    }

    private void addExtraRow(GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridy = row; gc.gridx = 0; gc.gridwidth = 1; gc.weightx = 0.3;
        JLabel lbl = new JLabel(label); lbl.setFont(UIConstants.FONT_SUBHEADING);
        extraPanel.add(lbl, gc);
        gc.gridx = 1; gc.weightx = 0.7;
        extraPanel.add(field, gc);
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            UIConstants.showError(this, "Full Name, Username, and Password are required."); return;
        }
        if (!password.equals(confirm)) {
            UIConstants.showError(this, "Passwords do not match."); return;
        }
        if (password.length() < 6) {
            UIConstants.showError(this, "Password must be at least 6 characters."); return;
        }
        if (userDAO.usernameExists(username)) {
            UIConstants.showError(this, "Username already exists."); return;
        }

        User user = new User();
        user.setFullName(fullName); user.setUsername(username);
        user.setPassword(password); user.setRole(role);
        user.setEmail(email); user.setPhone(phone);

        if (!userDAO.createUser(user)) {
            UIConstants.showError(this, "Failed to create user account."); return;
        }

        boolean success = false;
        if ("STUDENT".equals(role)) {
            String roll = rollNumberField.getText().trim();
            if (roll.isEmpty()) { UIConstants.showError(this, "Roll number is required."); return; }
            if (studentDAO.rollNumberExists(roll)) { UIConstants.showError(this, "Roll number already exists."); return; }
            SchoolClass cls = (SchoolClass) classCombo.getSelectedItem();

            Student s = new Student();
            s.setUserId(user.getId()); s.setRollNumber(roll);
            if (cls != null) s.setClassId(cls.getId());
            s.setGender((String) genderCombo.getSelectedItem());
            s.setGuardianName(guardianNameField.getText().trim());
            s.setGuardianPhone(guardianPhoneField.getText().trim());
            success = studentDAO.createStudent(s);
        } else {
            String empId = empIdField.getText().trim();
            if (empId.isEmpty()) { UIConstants.showError(this, "Employee ID is required."); return; }
            if (teacherDAO.employeeIdExists(empId)) { UIConstants.showError(this, "Employee ID already exists."); return; }

            Teacher t = new Teacher();
            t.setUserId(user.getId()); t.setEmployeeId(empId);
            t.setDepartment(departmentField.getText().trim());
            t.setQualification(qualificationField.getText().trim());
            success = teacherDAO.createTeacher(t);
        }

        if (success) {
            UIConstants.showSuccess(this, "Account created successfully! You can now login.");
            dispose();
        } else {
            UIConstants.showError(this, "Failed to create profile. Please try again.");
        }
    }
}
