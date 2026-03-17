package com.school.gui.student;

import com.school.database.*;
import com.school.gui.components.BaseDashboard;
import com.school.model.*;
import com.school.util.SessionManager;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends BaseDashboard {
    private Student student;
    private StudentDAO studentDAO;
    private MarksDAO marksDAO;
    private AttendanceDAO attendanceDAO;
    private FeeDAO feeDAO;

    public StudentDashboard() {
        super("Student Dashboard");
        // Initialize all DAOs BEFORE buildDashboard() is triggered
        studentDAO = new StudentDAO(); marksDAO = new MarksDAO();
        attendanceDAO = new AttendanceDAO(); feeDAO = new FeeDAO();
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        student = studentDAO.getStudentByUserId(userId);
        // Now safe to build the UI — all fields are ready
        initDashboard();
    }

    @Override
    protected void buildDashboard() {
        addSidebarSeparator("Main");
        JButton dashBtn = addNavButton("Dashboard", "📊", "dashboard");
        addSidebarSeparator("Academic");
        addNavButton("My Marks", "📝", "marks");
        addNavButton("Attendance", "✅", "attendance");
        addNavButton("Report Card", "📊", "report");
        addSidebarSeparator("Finance");
        addNavButton("Fee Info", "💰", "fees");
        addSidebarSeparator("Profile");
        addNavButton("My Profile", "👤", "profile");
        contentPanel.add(Box.createVerticalGlue());

        mainContent.add(createOverviewPanel(), "dashboard");
        mainContent.add(createMarksPanel(), "marks");
        mainContent.add(createAttendancePanel(), "attendance");
        mainContent.add(createReportPanel(), "report");
        mainContent.add(createFeePanel(), "fees");
        mainContent.add(createProfilePanel(), "profile");
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
        if (student != null) {
            List<Marks> allMarks = marksDAO.getMarksByStudent(student.getId());
            double avgPct = allMarks.stream().mapToDouble(Marks::getPercentage).average().orElse(0);
            double attPct = attendanceDAO.getAttendancePercentage(student.getId());
            long pendingFees = feeDAO.getFeesByStudent(student.getId()).stream().filter(f -> "Pending".equals(f.getStatus()) || "Overdue".equals(f.getStatus())).count();
            stats.add(UIConstants.createStatCard("Average Score", String.format("%.1f%%", avgPct), UIConstants.PRIMARY_COLOR));
            stats.add(UIConstants.createStatCard("Attendance", String.format("%.1f%%", attPct), attPct >= 75 ? UIConstants.SUCCESS_COLOR : UIConstants.DANGER_COLOR));
            stats.add(UIConstants.createStatCard("Pending Fees", String.valueOf(pendingFees), pendingFees > 0 ? UIConstants.DANGER_COLOR : UIConstants.SUCCESS_COLOR));
        }
        panel.add(stats, BorderLayout.CENTER);

        if (student != null) {
            JPanel infoCard = UIConstants.createCardPanel();
            infoCard.setLayout(new GridLayout(2, 4, 12, 8));
            String[][] info = {{"Roll Number", student.getRollNumber()}, {"Class", student.getClassName()},
                {"Guardian", student.getGuardianName()}, {"Admission", String.valueOf(student.getAdmissionDate())}};
            for (String[] row : info) {
                JPanel cell = new JPanel(new BorderLayout(0, 4)); cell.setOpaque(false);
                JLabel key = new JLabel(row[0]); key.setFont(UIConstants.FONT_SMALL); key.setForeground(UIConstants.TEXT_SECONDARY);
                JLabel val = new JLabel(row[1] != null ? row[1] : "N/A"); val.setFont(UIConstants.FONT_SUBHEADING); val.setForeground(UIConstants.TEXT_PRIMARY);
                cell.add(key, BorderLayout.NORTH); cell.add(val, BorderLayout.CENTER);
                infoCard.add(cell);
            }
            panel.add(infoCard, BorderLayout.SOUTH);
        }
        return panel;
    }

    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("My Marks"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Subject", "Exam Type", "Marks", "Max", "Percentage", "Grade", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel && c == 5) {
                    String grade = val != null ? val.toString() : "";
                    if (grade.startsWith("A")) { setForeground(UIConstants.SUCCESS_COLOR); setFont(UIConstants.FONT_SUBHEADING); }
                    else if (grade.equals("F")) { setForeground(UIConstants.DANGER_COLOR); setFont(UIConstants.FONT_SUBHEADING); }
                    else { setForeground(UIConstants.TEXT_PRIMARY); setFont(UIConstants.FONT_BODY); }
                }
                return this;
            }
        });

        if (student != null)
            for (Marks m : marksDAO.getMarksByStudent(student.getId()))
                model.addRow(new Object[]{m.getSubjectName(), m.getExamType(), m.getMarksObtained(), m.getMaxMarks(),
                    String.format("%.1f%%", m.getPercentage()), m.getGrade(), m.getExamDate()});

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("My Attendance"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        double pct = student != null ? attendanceDAO.getAttendancePercentage(student.getId()) : 0;
        JLabel pctLabel = new JLabel(String.format("  Overall Attendance: %.1f%%", pct));
        pctLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pctLabel.setForeground(pct >= 75 ? UIConstants.SUCCESS_COLOR : UIConstants.DANGER_COLOR);
        pctLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel northPanel = new JPanel(new BorderLayout()); northPanel.setOpaque(false);
        northPanel.add(title, BorderLayout.NORTH); northPanel.add(pctLabel, BorderLayout.SOUTH);
        panel.add(northPanel, BorderLayout.NORTH);

        String[] cols = {"Date", "Status", "Class", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel && c == 1) {
                    String status = val != null ? val.toString() : "";
                    if ("Present".equals(status)) { setForeground(UIConstants.SUCCESS_COLOR); }
                    else if ("Absent".equals(status)) { setForeground(UIConstants.DANGER_COLOR); }
                    else { setForeground(UIConstants.WARNING_COLOR); }
                }
                return this;
            }
        });

        if (student != null)
            for (Attendance a : attendanceDAO.getAttendanceByStudent(student.getId()))
                model.addRow(new Object[]{a.getAttendanceDate(), a.getStatus(), a.getClassName(), a.getRemarks()});

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("Report Card"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        if (student == null) { panel.add(new JLabel("Student profile not found."), BorderLayout.CENTER); return panel; }

        JPanel reportCard = UIConstants.createCardPanel();
        reportCard.setLayout(new BorderLayout(0, 16));

        JLabel studentInfo = new JLabel(String.format("<html><b>%s</b>  |  Roll: %s  |  Class: %s</html>",
            student.getFullName(), student.getRollNumber(), student.getClassName()));
        studentInfo.setFont(UIConstants.FONT_HEADING); studentInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        reportCard.add(studentInfo, BorderLayout.NORTH);

        List<Marks> allMarks = marksDAO.getMarksByStudent(student.getId());
        java.util.Map<String, double[]> subjectSummary = new java.util.LinkedHashMap<>();
        for (Marks m : allMarks) {
            subjectSummary.computeIfAbsent(m.getSubjectName(), k -> new double[]{0, 0, 0});
            subjectSummary.get(m.getSubjectName())[0] += m.getMarksObtained();
            subjectSummary.get(m.getSubjectName())[1] += m.getMaxMarks();
            subjectSummary.get(m.getSubjectName())[2]++;
        }

        String[] cols = {"Subject", "Total Marks", "Max Marks", "Percentage", "Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);
        double totalPct = 0; int subCount = 0;
        for (java.util.Map.Entry<String, double[]> entry : subjectSummary.entrySet()) {
            double[] vals = entry.getValue(); double pct = vals[1] > 0 ? vals[0] / vals[1] * 100 : 0;
            String grade = pct >= 90 ? "A+" : pct >= 80 ? "A" : pct >= 70 ? "B+" : pct >= 60 ? "B" : pct >= 50 ? "C" : pct >= 40 ? "D" : "F";
            model.addRow(new Object[]{entry.getKey(), String.format("%.1f", vals[0]), String.format("%.0f", vals[1]), String.format("%.1f%%", pct), grade});
            totalPct += pct; subCount++;
        }

        double attPct = attendanceDAO.getAttendancePercentage(student.getId());
        double overallPct = subCount > 0 ? totalPct / subCount : 0;
        JLabel summary = new JLabel(String.format("<html>  <b>Overall: %.1f%%</b>  |  Attendance: %.1f%%  |  Result: <b style='color:%s'>%s</b></html>",
            overallPct, attPct, overallPct >= 40 ? "green" : "red", overallPct >= 40 ? "PASS" : "FAIL"));
        summary.setFont(UIConstants.FONT_HEADING);

        reportCard.add(new JScrollPane(table), BorderLayout.CENTER);
        reportCard.add(summary, BorderLayout.SOUTH);
        panel.add(reportCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFeePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("Fee Information"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Fee Type", "Amount", "Due Date", "Status", "Paid Date", "Payment Method"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(model);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel && c == 3) {
                    String status = val != null ? val.toString() : "";
                    if ("Paid".equals(status)) setForeground(UIConstants.SUCCESS_COLOR);
                    else if ("Overdue".equals(status)) setForeground(UIConstants.DANGER_COLOR);
                    else setForeground(UIConstants.WARNING_COLOR);
                }
                return this;
            }
        });

        double totalPending = 0;
        if (student != null) {
            for (Fee f : feeDAO.getFeesByStudent(student.getId())) {
                model.addRow(new Object[]{f.getFeeType(), String.format("₹%.2f", f.getAmount()), f.getDueDate(), f.getStatus(), f.getPaidDate(), f.getPaymentMethod()});
                if ("Pending".equals(f.getStatus()) || "Overdue".equals(f.getStatus())) totalPending += f.getAmount();
            }
        }

        JLabel pendingLabel = new JLabel(String.format("  Total Pending: ₹%.2f", totalPending));
        pendingLabel.setFont(UIConstants.FONT_HEADING); pendingLabel.setForeground(totalPending > 0 ? UIConstants.DANGER_COLOR : UIConstants.SUCCESS_COLOR);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(pendingLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = UIConstants.createTitleLabel("My Profile"); title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        if (student == null) { panel.add(new JLabel("Profile not found."), BorderLayout.CENTER); return panel; }

        JPanel card = UIConstants.createCardPanel(); card.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(8, 8, 8, 8);

        User user = SessionManager.getInstance().getCurrentUser();
        String[][] info = {
            {"Full Name", student.getFullName()}, {"Username", user.getUsername()},
            {"Roll Number", student.getRollNumber()}, {"Class", student.getClassName()},
            {"Email", user.getEmail()}, {"Phone", user.getPhone()},
            {"Gender", student.getGender()}, {"Date of Birth", String.valueOf(student.getDateOfBirth())},
            {"Guardian", student.getGuardianName()}, {"Guardian Phone", student.getGuardianPhone()},
            {"Admission Date", String.valueOf(student.getAdmissionDate())}
        };

        for (int i = 0; i < info.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.35;
            JLabel key = new JLabel(info[i][0]); key.setFont(UIConstants.FONT_SUBHEADING); key.setForeground(UIConstants.TEXT_SECONDARY); card.add(key, gc);
            gc.gridx = 1; gc.weightx = 0.65;
            JLabel val = new JLabel(info[i][1] != null ? info[i][1] : "N/A"); val.setFont(UIConstants.FONT_BODY); card.add(val, gc);
        }
        panel.add(new JScrollPane(card), BorderLayout.CENTER);
        return panel;
    }
}
