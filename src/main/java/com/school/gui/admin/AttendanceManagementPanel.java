package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.SessionManager;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class AttendanceManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private AttendanceDAO attendanceDAO;
    private StudentDAO studentDAO;
    private ClassDAO classDAO;

    public AttendanceManagementPanel() {
        attendanceDAO = new AttendanceDAO(); studentDAO = new StudentDAO(); classDAO = new ClassDAO();
        setLayout(new BorderLayout()); setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UIConstants.createTitleLabel("Attendance Records");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Roll No", "Student", "Date", "Status", "Class", "Remarks"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(tableModel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); filterPanel.setOpaque(false);
        List<SchoolClass> classes = classDAO.getAllClasses();
        JComboBox<SchoolClass> classFilter = new JComboBox<>(classes.toArray(new SchoolClass[0]));
        classFilter.insertItemAt(null, 0); classFilter.setSelectedIndex(0);
        JTextField dateFilter = UIConstants.createStyledTextField(); dateFilter.setText(new Date(System.currentTimeMillis()).toString());
        dateFilter.setPreferredSize(new Dimension(130, 36));
        JButton loadBtn = UIConstants.createPrimaryButton("Load");
        JButton markBtn = UIConstants.createSuccessButton("+ Mark Attendance");
        loadBtn.addActionListener(e -> {
            SchoolClass cls = (SchoolClass) classFilter.getSelectedItem();
            if (cls == null) { loadAllAttendance(); return; }
            try { Date date = Date.valueOf(dateFilter.getText().trim()); loadClassAttendance(cls.getId(), date); }
            catch (Exception ex) { UIConstants.showError(this, "Invalid date format (YYYY-MM-DD)."); }
        });
        markBtn.addActionListener(e -> showMarkAttendanceDialog(classes));
        filterPanel.add(new JLabel("Class:")); filterPanel.add(classFilter);
        filterPanel.add(new JLabel("Date:")); filterPanel.add(dateFilter);
        filterPanel.add(loadBtn); filterPanel.add(markBtn);

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(filterPanel, BorderLayout.NORTH); center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        loadAllAttendance();
    }

    private void loadAllAttendance() {
        // Load recent attendance sample
        tableModel.setRowCount(0);
        // We'll load by a default class or show empty
    }

    private void loadClassAttendance(int classId, Date date) {
        tableModel.setRowCount(0);
        for (Attendance a : attendanceDAO.getAttendanceByClassAndDate(classId, date))
            tableModel.addRow(new Object[]{a.getId(), a.getRollNumber(), a.getStudentName(), a.getAttendanceDate(), a.getStatus(), a.getClassName(), a.getRemarks()});
    }

    private void showMarkAttendanceDialog(List<SchoolClass> classes) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Mark Attendance", true);
        d.setSize(700, 500); d.setLocationRelativeTo(this);
        JPanel main = new JPanel(new BorderLayout()); main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); top.setOpaque(false);
        JComboBox<SchoolClass> classCb = new JComboBox<>(classes.toArray(new SchoolClass[0]));
        JTextField dateFld = UIConstants.createStyledTextField(); dateFld.setText(new Date(System.currentTimeMillis()).toString());
        dateFld.setPreferredSize(new Dimension(130, 36));
        JButton loadStudents = UIConstants.createPrimaryButton("Load Students");
        top.add(new JLabel("Class:")); top.add(classCb); top.add(new JLabel("Date:")); top.add(dateFld); top.add(loadStudents);

        String[] cols = {"Student ID", "Roll No", "Name", "Status"};
        DefaultTableModel atModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        JTable atTable = UIConstants.createStyledTable(); atTable.setModel(atModel);
        TableColumn statusCol = atTable.getColumnModel().getColumn(3);
        statusCol.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Present", "Absent", "Late", "Excused"})));

        loadStudents.addActionListener(e -> {
            SchoolClass cls = (SchoolClass) classCb.getSelectedItem(); if (cls == null) return;
            atModel.setRowCount(0);
            for (Student st : studentDAO.getStudentsByClass(cls.getId()))
                atModel.addRow(new Object[]{st.getId(), st.getRollNumber(), st.getFullName(), "Present"});
        });

        JButton saveBtn = UIConstants.createPrimaryButton("Save Attendance");
        saveBtn.addActionListener(e -> {
            SchoolClass cls = (SchoolClass) classCb.getSelectedItem();
            Date date; try { date = Date.valueOf(dateFld.getText().trim()); } catch (Exception ex) { UIConstants.showError(d, "Invalid date."); return; }
            int saved = 0;
            for (int r = 0; r < atModel.getRowCount(); r++) {
                Attendance a = new Attendance();
                a.setStudentId((int) atModel.getValueAt(r, 0));
                if (cls != null) a.setClassId(cls.getId());
                a.setAttendanceDate(date); a.setStatus((String) atModel.getValueAt(r, 3));
                a.setMarkedBy(SessionManager.getInstance().getCurrentUser().getId());
                if (attendanceDAO.markAttendance(a)) saved++;
            }
            UIConstants.showSuccess(d, "Attendance saved for " + saved + " students."); d.dispose();
        });

        main.add(top, BorderLayout.NORTH); main.add(new JScrollPane(atTable), BorderLayout.CENTER);
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bp.add(saveBtn); main.add(bp, BorderLayout.SOUTH);
        d.add(main); d.setVisible(true);
    }
}
