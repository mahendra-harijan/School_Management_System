package com.school.gui.teacher;

import com.school.database.*;
import com.school.gui.components.BaseDashboard;
import com.school.model.*;
import com.school.util.SessionManager;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class TeacherDashboard extends BaseDashboard {
    private Teacher teacher;
    private TeacherDAO teacherDAO;
    private SubjectDAO subjectDAO;
    private StudentDAO studentDAO;
    private MarksDAO marksDAO;
    private AttendanceDAO attendanceDAO;

    public TeacherDashboard() {
        super("Teacher Dashboard");
        // Initialize all DAOs BEFORE buildDashboard() is triggered
        teacherDAO = new TeacherDAO(); subjectDAO = new SubjectDAO();
        studentDAO = new StudentDAO(); marksDAO = new MarksDAO(); attendanceDAO = new AttendanceDAO();
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        teacher = teacherDAO.getTeacherByUserId(userId);
        // Now safe to build the UI — all fields are ready
        initDashboard();
    }

    @Override
    protected void buildDashboard() {
        addSidebarSeparator("Main");
        JButton dashBtn = addNavButton("Dashboard", "📊", "dashboard");
        addSidebarSeparator("Teaching");
        addNavButton("My Subjects", "📚", "subjects");
        addNavButton("My Students", "👨‍🎓", "students");
        addSidebarSeparator("Academic");
        addNavButton("Enter Marks", "📝", "marks");
        addNavButton("Attendance", "✅", "attendance");
        addNavButton("Performance", "📈", "performance");
        contentPanel.add(Box.createVerticalGlue());

        mainContent.add(createOverviewPanel(), "dashboard");
        mainContent.add(createSubjectsPanel(), "subjects");
        mainContent.add(createStudentsPanel(), "students");
        mainContent.add(createMarksPanel(), "marks");
        mainContent.add(createAttendancePanel(), "attendance");
        mainContent.add(createPerformancePanel(), "performance");

        setActiveButton(dashBtn);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        String name = SessionManager.getInstance().getCurrentUser().getFullName();
        JLabel welcome = UIConstants.createTitleLabel("Welcome, " + name + "!");
        welcome.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(welcome, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 16)); stats.setOpaque(false);
        if (teacher != null) {
            List<Subject> subjects = subjectDAO.getSubjectsByTeacher(teacher.getId());
            int totalSubjects = subjects.size();
            int totalStudents = 0;
            for (Subject s : subjects) if (s.getClassId() > 0) totalStudents += studentDAO.getStudentsByClass(s.getClassId()).size();
            stats.add(UIConstants.createStatCard("My Subjects", String.valueOf(totalSubjects), UIConstants.PRIMARY_COLOR));
            stats.add(UIConstants.createStatCard("My Students", String.valueOf(totalStudents), UIConstants.SUCCESS_COLOR));
            stats.add(UIConstants.createStatCard("Department", teacher.getDepartment() != null ? teacher.getDepartment() : "N/A", UIConstants.ACCENT_COLOR));
        }
        panel.add(stats, BorderLayout.CENTER);

        // Teacher info card
        if (teacher != null) {
            JPanel infoCard = UIConstants.createCardPanel();
            infoCard.setLayout(new GridLayout(2, 2, 8, 8));
            String[][] info = {{"Employee ID", teacher.getEmployeeId()}, {"Department", teacher.getDepartment()},
                {"Qualification", teacher.getQualification()}, {"Joining Date", String.valueOf(teacher.getJoiningDate())}};
            for (String[] row : info) {
                JLabel key = new JLabel(row[0] + ": "); key.setFont(UIConstants.FONT_SUBHEADING); key.setForeground(UIConstants.TEXT_SECONDARY);
                JLabel val = new JLabel(row[1] != null ? row[1] : "N/A"); val.setFont(UIConstants.FONT_BODY);
                infoCard.add(key); infoCard.add(val);
            }
            panel.add(infoCard, BorderLayout.SOUTH);
        }
        return panel;
    }

    private JPanel createSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("My Subjects"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Code", "Subject", "Class", "Max Marks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);
        if (teacher != null)
            for (Subject s : subjectDAO.getSubjectsByTeacher(teacher.getId()))
                model.addRow(new Object[]{s.getId(), s.getSubjectCode(), s.getSubjectName(), s.getClassName(), s.getMaxMarks()});
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("My Students"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Roll No", "Name", "Class", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);
        if (teacher != null) {
            List<Integer> classIds = new java.util.ArrayList<>();
            for (Subject s : subjectDAO.getSubjectsByTeacher(teacher.getId()))
                if (s.getClassId() > 0 && !classIds.contains(s.getClassId())) classIds.add(s.getClassId());
            for (int classId : classIds)
                for (Student st : studentDAO.getStudentsByClass(classId))
                    model.addRow(new Object[]{st.getRollNumber(), st.getFullName(), st.getClassName(), st.getEmail(), st.getPhone()});
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("Enter Marks"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = UIConstants.createCardPanel(); form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(8, 8, 8, 8);

        List<Subject> mySubjects = teacher != null ? subjectDAO.getSubjectsByTeacher(teacher.getId()) : List.of();
        JComboBox<Subject> subjectCb = new JComboBox<>(mySubjects.toArray(new Subject[0]));
        JComboBox<String> examCb = new JComboBox<>(new String[]{"Unit Test 1", "Unit Test 2", "Mid Term", "Final", "Assignment"});
        JTextField dateFld = UIConstants.createStyledTextField(); dateFld.setText(new Date(System.currentTimeMillis()).toString());

        gc.gridy = 0; gc.gridx = 0; gc.weightx = 0.3; JLabel l1 = new JLabel("Subject:"); l1.setFont(UIConstants.FONT_SUBHEADING); form.add(l1, gc);
        gc.gridx = 1; gc.weightx = 0.7; form.add(subjectCb, gc);
        gc.gridy = 1; gc.gridx = 0; gc.weightx = 0.3; JLabel l2 = new JLabel("Exam Type:"); l2.setFont(UIConstants.FONT_SUBHEADING); form.add(l2, gc);
        gc.gridx = 1; gc.weightx = 0.7; form.add(examCb, gc);
        gc.gridy = 2; gc.gridx = 0; gc.weightx = 0.3; JLabel l3 = new JLabel("Exam Date:"); l3.setFont(UIConstants.FONT_SUBHEADING); form.add(l3, gc);
        gc.gridx = 1; gc.weightx = 0.7; form.add(dateFld, gc);

        String[] cols = {"Student ID", "Roll No", "Name", "Marks", "Max"};
        DefaultTableModel marksModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        JTable marksTable = UIConstants.createStyledTable(); marksTable.setModel(marksModel);

        JButton loadBtn = UIConstants.createSecondaryButton("Load Students");
        JButton saveBtn = UIConstants.createSuccessButton("Save All Marks");
        loadBtn.addActionListener(e -> {
            Subject sub = (Subject) subjectCb.getSelectedItem(); if (sub == null || sub.getClassId() == 0) { UIConstants.showError(panel, "Select a subject with a class."); return; }
            marksModel.setRowCount(0);
            for (Student st : studentDAO.getStudentsByClass(sub.getClassId()))
                marksModel.addRow(new Object[]{st.getId(), st.getRollNumber(), st.getFullName(), "", sub.getMaxMarks()});
        });
        saveBtn.addActionListener(e -> {
            Subject sub = (Subject) subjectCb.getSelectedItem(); if (sub == null) return;
            int saved = 0;
            for (int r = 0; r < marksModel.getRowCount(); r++) {
                Object marksVal = marksModel.getValueAt(r, 3); if (marksVal == null || marksVal.toString().trim().isEmpty()) continue;
                try {
                    double marks = Double.parseDouble(marksVal.toString().trim());
                    Marks m = new Marks(); m.setStudentId((int) marksModel.getValueAt(r, 0));
                    m.setSubjectId(sub.getId()); m.setExamType((String) examCb.getSelectedItem());
                    m.setMarksObtained(marks); m.setMaxMarks((int) marksModel.getValueAt(r, 4));
                    try { m.setExamDate(Date.valueOf(dateFld.getText().trim())); } catch (Exception ex) {}
                    m.setEnteredBy(SessionManager.getInstance().getCurrentUser().getId());
                    if (marksDAO.addMarks(m)) saved++;
                } catch (NumberFormatException ex) {}
            }
            UIConstants.showSuccess(panel, "Saved marks for " + saved + " students.");
        });

        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2; gc.weightx = 1.0;
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); btnPnl.setOpaque(false);
        btnPnl.add(loadBtn); btnPnl.add(saveBtn); form.add(btnPnl, gc);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(marksTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("Mark Attendance"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = UIConstants.createCardPanel(); form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(8, 8, 8, 8);

        List<Subject> mySubjects = teacher != null ? subjectDAO.getSubjectsByTeacher(teacher.getId()) : List.of();
        JComboBox<Subject> subCb = new JComboBox<>(mySubjects.toArray(new Subject[0]));
        JTextField dateFld = UIConstants.createStyledTextField(); dateFld.setText(new Date(System.currentTimeMillis()).toString());

        gc.gridy = 0; gc.gridx = 0; gc.weightx = 0.3; JLabel l1 = new JLabel("Subject:"); l1.setFont(UIConstants.FONT_SUBHEADING); form.add(l1, gc);
        gc.gridx = 1; gc.weightx = 0.7; form.add(subCb, gc);
        gc.gridy = 1; gc.gridx = 0; gc.weightx = 0.3; JLabel l2 = new JLabel("Date:"); l2.setFont(UIConstants.FONT_SUBHEADING); form.add(l2, gc);
        gc.gridx = 1; gc.weightx = 0.7; form.add(dateFld, gc);

        String[] cols = {"Student ID", "Class ID", "Roll No", "Name", "Status"};
        DefaultTableModel attModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        attModel.addTableModelListener(e2 -> {});
        JTable attTable = UIConstants.createStyledTable(); attTable.setModel(attModel);
        attTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Present", "Absent", "Late", "Excused"})));
        attTable.getColumnModel().getColumn(0).setMaxWidth(0); attTable.getColumnModel().getColumn(0).setMinWidth(0);
        attTable.getColumnModel().getColumn(1).setMaxWidth(0); attTable.getColumnModel().getColumn(1).setMinWidth(0);

        JButton loadBtn = UIConstants.createSecondaryButton("Load Students");
        JButton saveBtn = UIConstants.createSuccessButton("Save Attendance");
        loadBtn.addActionListener(e -> {
            Subject sub = (Subject) subCb.getSelectedItem(); if (sub == null || sub.getClassId() == 0) { UIConstants.showError(panel, "Select subject with class."); return; }
            attModel.setRowCount(0);
            for (Student st : studentDAO.getStudentsByClass(sub.getClassId()))
                attModel.addRow(new Object[]{st.getId(), st.getClassId(), st.getRollNumber(), st.getFullName(), "Present"});
        });
        saveBtn.addActionListener(e -> {
            Subject sub = (Subject) subCb.getSelectedItem();
            Date date; try { date = Date.valueOf(dateFld.getText().trim()); } catch (Exception ex) { UIConstants.showError(panel, "Invalid date."); return; }
            int saved = 0;
            for (int r = 0; r < attModel.getRowCount(); r++) {
                Attendance a = new Attendance(); a.setStudentId((int) attModel.getValueAt(r, 0));
                a.setClassId((int) attModel.getValueAt(r, 1)); a.setAttendanceDate(date);
                a.setStatus((String) attModel.getValueAt(r, 4));
                if (sub != null) a.setSubjectId(sub.getId());
                a.setMarkedBy(SessionManager.getInstance().getCurrentUser().getId());
                if (attendanceDAO.markAttendance(a)) saved++;
            }
            UIConstants.showSuccess(panel, "Attendance saved for " + saved + " students.");
        });

        gc.gridy = 2; gc.gridx = 0; gc.gridwidth = 2;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); bp.setOpaque(false);
        bp.add(loadBtn); bp.add(saveBtn); form.add(bp, gc);
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(attTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("Student Performance"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel filterPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); filterPnl.setOpaque(false);
        List<Subject> mySubjects = teacher != null ? subjectDAO.getSubjectsByTeacher(teacher.getId()) : List.of();
        JComboBox<Subject> subCb = new JComboBox<>(mySubjects.toArray(new Subject[0]));
        JButton loadBtn = UIConstants.createPrimaryButton("Load");
        filterPnl.add(new JLabel("Subject:")); filterPnl.add(subCb); filterPnl.add(loadBtn);

        String[] cols = {"Roll No", "Student", "Exam", "Marks", "Max", "%", "Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);
        loadBtn.addActionListener(e -> {
            Subject sub = (Subject) subCb.getSelectedItem(); if (sub == null || sub.getClassId() == 0) return;
            model.setRowCount(0);
            for (Marks m : marksDAO.getMarksByClassAndSubject(sub.getClassId(), sub.getId()))
                model.addRow(new Object[]{m.getRollNumber(), m.getStudentName(), m.getExamType(),
                    m.getMarksObtained(), m.getMaxMarks(), String.format("%.1f%%", m.getPercentage()), m.getGrade()});
        });

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(filterPnl, BorderLayout.NORTH); center.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
}
