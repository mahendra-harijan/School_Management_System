package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ClassManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ClassDAO classDAO;
    private TeacherDAO teacherDAO;

    public ClassManagementPanel() {
        classDAO = new ClassDAO(); teacherDAO = new TeacherDAO();
        initUI(); loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout()); setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.add(UIConstants.createTitleLabel("Class Management"), BorderLayout.WEST);
        JButton addBtn = UIConstants.createPrimaryButton("+ Add Class");
        addBtn.addActionListener(e -> showClassDialog(null));
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Class Name", "Section", "Academic Year", "Class Teacher"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = UIConstants.createStyledTable(); table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); actions.setOpaque(false);
        JButton editBtn = UIConstants.createSecondaryButton("✏ Edit");
        JButton delBtn = UIConstants.createDangerButton("🗑 Delete");
        editBtn.addActionListener(e -> editSelected()); delBtn.addActionListener(e -> deleteSelected());
        actions.add(editBtn); actions.add(delBtn);

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(new JScrollPane(table), BorderLayout.CENTER); center.add(actions, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (SchoolClass c : classDAO.getAllClasses())
            tableModel.addRow(new Object[]{c.getId(), c.getClassName(), c.getSection(), c.getAcademicYear(), c.getTeacherName()});
    }

    private int getSelectedId() {
        int r = table.getSelectedRow(); if (r < 0) { UIConstants.showError(this, "Select a class."); return -1; }
        return (int) tableModel.getValueAt(r, 0);
    }

    private void editSelected() {
        int id = getSelectedId(); if (id < 0) return;
        showClassDialog(classDAO.getClassById(id));
    }

    private void deleteSelected() {
        int id = getSelectedId(); if (id < 0) return;
        if (UIConstants.confirmDialog(this, "Delete this class?"))
            if (classDAO.deleteClass(id)) { UIConstants.showSuccess(this, "Class deleted."); loadData(); }
            else UIConstants.showError(this, "Failed.");
    }

    private void showClassDialog(SchoolClass existing) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing == null ? "Add Class" : "Edit Class", true);
        d.setSize(400, 300); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridBagLayout()); p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 6, 6, 6);

        JTextField nameF = UIConstants.createStyledTextField();
        JTextField secF = UIConstants.createStyledTextField();
        JTextField yearF = UIConstants.createStyledTextField();
        List<Teacher> teachers = teacherDAO.getAllTeachers();
        Teacher[] teacherArr = teachers.toArray(new Teacher[0]);
        JComboBox<Teacher> teacherCombo = new JComboBox<>(teacherArr);
        teacherCombo.insertItemAt(null, 0); teacherCombo.setSelectedIndex(0);

        if (existing != null) { nameF.setText(existing.getClassName()); secF.setText(existing.getSection()); yearF.setText(existing.getAcademicYear()); }
        else yearF.setText("2024-2025");

        String[] labels = {"Class Name *", "Section *", "Academic Year *", "Class Teacher"};
        JComponent[] comps = {nameF, secF, yearF, teacherCombo};
        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.4; JLabel l = new JLabel(labels[i]); l.setFont(UIConstants.FONT_SUBHEADING); p.add(l, gc);
            gc.gridx = 1; gc.weightx = 0.6; p.add(comps[i], gc);
        }
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sv = UIConstants.createPrimaryButton("Save");
        sv.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty() || secF.getText().trim().isEmpty()) { UIConstants.showError(d, "Name & section required."); return; }
            SchoolClass c = existing != null ? existing : new SchoolClass();
            c.setClassName(nameF.getText().trim()); c.setSection(secF.getText().trim()); c.setAcademicYear(yearF.getText().trim());
            Teacher t = (Teacher) teacherCombo.getSelectedItem(); if (t != null) c.setTeacherId(t.getId());
            boolean ok = existing == null ? classDAO.createClass(c) : classDAO.updateClass(c);
            if (ok) { UIConstants.showSuccess(d, "Saved."); loadData(); d.dispose(); } else UIConstants.showError(d, "Failed.");
        });
        JButton cn = UIConstants.createSecondaryButton("Cancel"); cn.addActionListener(e -> d.dispose());
        bp.add(cn); bp.add(sv);
        gc.gridy = labels.length; gc.gridx = 0; gc.gridwidth = 2; p.add(bp, gc);
        d.add(p); d.setVisible(true);
    }
}
