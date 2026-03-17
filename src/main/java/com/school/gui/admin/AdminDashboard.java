package com.school.gui.admin;

import com.school.database.*;
import com.school.gui.components.BaseDashboard;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AdminDashboard extends BaseDashboard {
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private ClassDAO classDAO;
    private MarksDAO marksDAO;
    private FeeDAO feeDAO;

    public AdminDashboard() {
        super("Admin Dashboard");
        // Initialize all DAOs BEFORE buildDashboard() is triggered
        userDAO = new UserDAO(); studentDAO = new StudentDAO();
        teacherDAO = new TeacherDAO(); classDAO = new ClassDAO();
        marksDAO = new MarksDAO(); feeDAO = new FeeDAO();
        // Now safe to build the UI — all fields are ready
        initDashboard();
    }

    @Override
    protected void buildDashboard() {
        addSidebarSeparator("Main");
        JButton dashBtn = addNavButton("Dashboard", "📊", "dashboard");
        addSidebarSeparator("Management");
        addNavButton("Students", "👨‍🎓", "students");
        addNavButton("Teachers", "👩‍🏫", "teachers");
        addNavButton("Classes", "🏫", "classes");
        addNavButton("Subjects", "📚", "subjects");
        addSidebarSeparator("Academic");
        addNavButton("Marks", "📝", "marks");
        addNavButton("Attendance", "✅", "attendance");
        addSidebarSeparator("Finance");
        addNavButton("Fees", "💰", "fees");
        addSidebarSeparator("Reports");
        addNavButton("Reports", "📈", "reports");

        contentPanel.add(Box.createVerticalGlue());

        mainContent.add(createOverviewPanel(), "dashboard");
        mainContent.add(new StudentManagementPanel(), "students");
        mainContent.add(new TeacherManagementPanel(), "teachers");
        mainContent.add(new ClassManagementPanel(), "classes");
        mainContent.add(new SubjectManagementPanel(), "subjects");
        mainContent.add(new MarksManagementPanel(), "marks");
        mainContent.add(new AttendanceManagementPanel(), "attendance");
        mainContent.add(new FeeManagementPanel(), "fees");
        mainContent.add(new ReportsPanel(), "reports");

        setActiveButton(dashBtn);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = UIConstants.createTitleLabel("Dashboard Overview");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        // Stats grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 16, 16));
        statsGrid.setOpaque(false);

        int totalStudents = userDAO.getTotalCount("STUDENT");
        int totalTeachers = userDAO.getTotalCount("TEACHER");
        int totalClasses = classDAO.getAllClasses().size();
        int totalSubjects = new SubjectDAO().getAllSubjects().size();
        double pendingFees = feeDAO.getTotalPendingFees();

        statsGrid.add(UIConstants.createStatCard("Total Students", String.valueOf(totalStudents), UIConstants.PRIMARY_COLOR));
        statsGrid.add(UIConstants.createStatCard("Total Teachers", String.valueOf(totalTeachers), UIConstants.SUCCESS_COLOR));
        statsGrid.add(UIConstants.createStatCard("Classes", String.valueOf(totalClasses), UIConstants.ACCENT_COLOR));
        statsGrid.add(UIConstants.createStatCard("Subjects", String.valueOf(totalSubjects), new Color(156, 39, 176)));
        statsGrid.add(UIConstants.createStatCard("Pending Fees", String.format("₹%.0f", pendingFees), UIConstants.DANGER_COLOR));
        statsGrid.add(UIConstants.createStatCard("Total Users", String.valueOf(totalStudents + totalTeachers), UIConstants.PRIMARY_DARK));
        statsGrid.add(UIConstants.createStatCard("Academic Year", "2024-2025", new Color(0, 150, 136)));
        statsGrid.add(UIConstants.createStatCard("System Status", "Active", UIConstants.SUCCESS_COLOR));

        panel.add(statsGrid, BorderLayout.CENTER);

        // Quick actions
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        quickActions.setOpaque(false);
        quickActions.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            "Quick Actions", 0, 0, UIConstants.FONT_SUBHEADING, UIConstants.TEXT_SECONDARY));

        String[][] actions = {{"+ Add Student", "students"}, {"+ Add Teacher", "teachers"}, {"+ Add Class", "classes"}, {"View Reports", "reports"}};
        for (String[] action : actions) {
            JButton btn = UIConstants.createPrimaryButton(action[0]);
            btn.addActionListener(e -> cardLayout.show(mainContent, action[1]));
            quickActions.add(btn);
        }
        panel.add(quickActions, BorderLayout.SOUTH);
        return panel;
    }
}
