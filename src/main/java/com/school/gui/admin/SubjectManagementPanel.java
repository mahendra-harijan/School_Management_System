package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class SubjectManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private SubjectDAO subjectDAO;
    private ClassDAO classDAO;
    private TeacherDAO teacherDAO;

    public SubjectManagementPanel() {
        subjectDAO = new SubjectDAO(); classDAO = new ClassDAO(); teacherDAO = new TeacherDAO();
        setLayout(new BorderLayout()); setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.add(UIConstants.createTitleLabel("Subject Management"), BorderLayout.WEST);
        JButton addBtn = UIConstants.createPrimaryButton("+ Add Subject");
        addBtn.addActionListener(e -> showDialog(null));
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Code", "Subject Name", "Class", "Teacher", "Max Marks"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); actions.setOpaque(false);
        JButton editBtn = UIConstants.createSecondaryButton("✏ Edit");
        JButton delBtn = UIConstants.createDangerButton("🗑 Delete");
        editBtn.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) { UIConstants.showError(this, "Select a subject."); return; }
            int id = (int) tableModel.getValueAt(r, 0);
            subjectDAO.getAllSubjects().stream().filter(x -> x.getId() == id).findFirst().ifPresent(s -> showDialog(s)); });
        delBtn.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) return;
            int id = (int) tableModel.getValueAt(r, 0);
            if (UIConstants.confirmDialog(this, "Delete subject?") && subjectDAO.deleteSubject(id)) loadData(); });
        actions.add(editBtn); actions.add(delBtn);

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(new JScrollPane(table), BorderLayout.CENTER); center.add(actions, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Subject s : subjectDAO.getAllSubjects())
            tableModel.addRow(new Object[]{s.getId(), s.getSubjectCode(), s.getSubjectName(), s.getClassName(), s.getTeacherName(), s.getMaxMarks()});
    }

    private void showDialog(Subject existing) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing == null ? "Add Subject" : "Edit Subject", true);
        d.setSize(420, 320); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridBagLayout()); p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 6, 6, 6);

        JTextField nameF = UIConstants.createStyledTextField();
        JTextField codeF = UIConstants.createStyledTextField();
        JTextField maxF = UIConstants.createStyledTextField(); maxF.setText("100");
        List<SchoolClass> classes = classDAO.getAllClasses();
        JComboBox<SchoolClass> classCombo = new JComboBox<>(classes.toArray(new SchoolClass[0]));
        classCombo.insertItemAt(null, 0); classCombo.setSelectedIndex(0);
        List<Teacher> teachers = teacherDAO.getAllTeachers();
        JComboBox<Teacher> teacherCombo = new JComboBox<>(teachers.toArray(new Teacher[0]));
        teacherCombo.insertItemAt(null, 0); teacherCombo.setSelectedIndex(0);

        if (existing != null) {
            nameF.setText(existing.getSubjectName()); codeF.setText(existing.getSubjectCode()); maxF.setText(String.valueOf(existing.getMaxMarks()));
            codeF.setEditable(false);
            for (int i = 0; i < classes.size(); i++) if (classes.get(i).getId() == existing.getClassId()) { classCombo.setSelectedIndex(i + 1); break; }
            for (int i = 0; i < teachers.size(); i++) if (teachers.get(i).getId() == existing.getTeacherId()) { teacherCombo.setSelectedIndex(i + 1); break; }
        }

        String[] labels = {"Subject Name *", "Subject Code *", "Class", "Teacher", "Max Marks"};
        JComponent[] comps = {nameF, codeF, classCombo, teacherCombo, maxF};
        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.4; JLabel l = new JLabel(labels[i]); l.setFont(UIConstants.FONT_SUBHEADING); p.add(l, gc);
            gc.gridx = 1; gc.weightx = 0.6; p.add(comps[i], gc);
        }
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sv = UIConstants.createPrimaryButton("Save");
        sv.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty() || codeF.getText().trim().isEmpty()) { UIConstants.showError(d, "Name & code required."); return; }
            Subject s = existing != null ? existing : new Subject();
            s.setSubjectName(nameF.getText().trim()); s.setSubjectCode(codeF.getText().trim());
            try { s.setMaxMarks(Integer.parseInt(maxF.getText().trim())); } catch (NumberFormatException ex) { s.setMaxMarks(100); }
            SchoolClass cls = (SchoolClass) classCombo.getSelectedItem(); if (cls != null) s.setClassId(cls.getId());
            Teacher t = (Teacher) teacherCombo.getSelectedItem(); if (t != null) s.setTeacherId(t.getId());
            boolean ok = existing == null ? subjectDAO.createSubject(s) : subjectDAO.updateSubject(s);
            if (ok) { UIConstants.showSuccess(d, "Saved."); loadData(); d.dispose(); } else UIConstants.showError(d, "Failed.");
        });
        JButton cn = UIConstants.createSecondaryButton("Cancel"); cn.addActionListener(e -> d.dispose());
        bp.add(cn); bp.add(sv);
        gc.gridy = labels.length; gc.gridx = 0; gc.gridwidth = 2; p.add(bp, gc);
        d.add(p); d.setVisible(true);
    }
}
