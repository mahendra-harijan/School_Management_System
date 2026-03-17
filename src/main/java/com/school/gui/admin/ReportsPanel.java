package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {
    private StudentDAO studentDAO;
    private MarksDAO marksDAO;
    private AttendanceDAO attendanceDAO;
    private ClassDAO classDAO;

    public ReportsPanel() {
        studentDAO = new StudentDAO(); marksDAO = new MarksDAO();
        attendanceDAO = new AttendanceDAO(); classDAO = new ClassDAO();
        setLayout(new BorderLayout()); setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UIConstants.createTitleLabel("Academic Reports");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIConstants.FONT_SUBHEADING);
        tabs.addTab("Student Performance", createPerformanceTab());
        tabs.addTab("Class Summary", createClassSummaryTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); filterPanel.setOpaque(false);
        List<Student> students = studentDAO.getAllStudents();
        JComboBox<Student> studentCb = new JComboBox<>(students.toArray(new Student[0]));
        JButton loadBtn = UIConstants.createPrimaryButton("Load Report");
        filterPanel.add(new JLabel("Student:")); filterPanel.add(studentCb); filterPanel.add(loadBtn);

        String[] cols = {"Subject", "Exam Type", "Marks", "Max Marks", "Percentage", "Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);

        JLabel summaryLabel = new JLabel(" ");
        summaryLabel.setFont(UIConstants.FONT_SUBHEADING); summaryLabel.setForeground(UIConstants.PRIMARY_COLOR);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        loadBtn.addActionListener(e -> {
            Student st = (Student) studentCb.getSelectedItem(); if (st == null) return;
            model.setRowCount(0);
            List<Marks> marksList = marksDAO.getMarksByStudent(st.getId());
            double totalPct = 0; int count = 0;
            for (Marks m : marksList) {
                model.addRow(new Object[]{m.getSubjectName(), m.getExamType(), m.getMarksObtained(), m.getMaxMarks(),
                    String.format("%.1f%%", m.getPercentage()), m.getGrade()});
                totalPct += m.getPercentage(); count++;
            }
            double attendance = attendanceDAO.getAttendancePercentage(st.getId());
            summaryLabel.setText(String.format("  Student: %s  |  Overall: %.1f%%  |  Attendance: %.1f%%",
                st.getFullName(), count > 0 ? totalPct / count : 0, attendance));
        });

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(summaryLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createClassSummaryTab() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); filterPanel.setOpaque(false);
        List<SchoolClass> classes = classDAO.getAllClasses();
        JComboBox<SchoolClass> classCb = new JComboBox<>(classes.toArray(new SchoolClass[0]));
        JButton loadBtn = UIConstants.createPrimaryButton("Load Summary");
        filterPanel.add(new JLabel("Class:")); filterPanel.add(classCb); filterPanel.add(loadBtn);

        String[] cols = {"Roll No", "Student Name", "Avg Marks %", "Attendance %", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);

        // Color-code status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    try {
                        double pct = Double.parseDouble(t.getModel().getValueAt(r, 2).toString().replace("%", ""));
                        if (pct >= 75) setBackground(new Color(232, 245, 233));
                        else if (pct >= 50) setBackground(new Color(255, 248, 225));
                        else setBackground(new Color(255, 235, 238));
                    } catch (Exception e) { setBackground(Color.WHITE); }
                }
                return this;
            }
        });

        loadBtn.addActionListener(e -> {
            SchoolClass cls = (SchoolClass) classCb.getSelectedItem(); if (cls == null) return;
            model.setRowCount(0);
            for (Student st : studentDAO.getStudentsByClass(cls.getId())) {
                List<Marks> marks = marksDAO.getMarksByStudent(st.getId());
                double avgPct = marks.stream().mapToDouble(Marks::getPercentage).average().orElse(0);
                double attPct = attendanceDAO.getAttendancePercentage(st.getId());
                String status = avgPct >= 75 ? "Good" : avgPct >= 50 ? "Average" : "Needs Improvement";
                model.addRow(new Object[]{st.getRollNumber(), st.getFullName(),
                    String.format("%.1f%%", avgPct), String.format("%.1f%%", attPct), status});
            }
        });

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
