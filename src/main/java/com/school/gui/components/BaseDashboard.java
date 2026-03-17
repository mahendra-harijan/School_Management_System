package com.school.gui.components;

import com.school.util.SessionManager;
import com.school.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class BaseDashboard extends JFrame {
    protected JPanel sidebarPanel;
    protected JPanel contentPanel;
    protected JPanel headerPanel;
    protected CardLayout cardLayout;
    protected JPanel mainContent;
    protected JButton activeButton;

    public BaseDashboard(String title) {
        setTitle("SchoolPro - " + title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        initBaseUI();
        // NOTE: buildDashboard() is NOT called here.
        // Each subclass must call initDashboard() at the END of its own constructor,
        // AFTER all its DAO fields have been initialized.
    }

    /**
     * Call this method at the END of every subclass constructor, after all
     * DAO / service fields have been assigned.  This replaces the old
     * buildDashboard() call that was inside initBaseUI().
     */
    protected final void initDashboard() {
        buildDashboard();
    }

    private void initBaseUI() {
        JPanel root = new JPanel(new BorderLayout());

        // Header
        headerPanel = createHeader();
        root.add(headerPanel, BorderLayout.NORTH);

        // Sidebar + Content
        JPanel body = new JPanel(new BorderLayout());
        sidebarPanel = createSidebar();
        body.add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(UIConstants.BG_COLOR);
        body.add(mainContent, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        add(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        header.setPreferredSize(new Dimension(0, 56));

        JLabel appName = new JLabel("🎓 SchoolPro");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        String user = SessionManager.getInstance().getCurrentUser().getFullName();
        String role = SessionManager.getInstance().getCurrentUser().getRole();
        JLabel userLabel = new JLabel(user + "  |  " + role);
        userLabel.setFont(UIConstants.FONT_BODY);
        userLabel.setForeground(new Color(200, 225, 255));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UIConstants.FONT_SMALL);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(229, 57, 53));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        logoutBtn.addActionListener(e -> handleLogout());

        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);

        header.add(appName, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        contentPanel = sidebar;
        return sidebar;
    }

    protected JButton addNavButton(String text, String icon, String cardName) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(UIConstants.FONT_SIDEBAR);
        btn.setForeground(new Color(176, 196, 222));
        btn.setBackground(UIConstants.SIDEBAR_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) { btn.setBackground(UIConstants.SIDEBAR_HOVER); btn.setForeground(Color.WHITE); }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeButton) { btn.setBackground(UIConstants.SIDEBAR_COLOR); btn.setForeground(new Color(176, 196, 222)); }
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(mainContent, cardName);
            setActiveButton(btn);
        });

        contentPanel.add(btn);
        return btn;
    }

    protected void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(UIConstants.SIDEBAR_COLOR);
            activeButton.setForeground(new Color(176, 196, 222));
        }
        activeButton = btn;
        btn.setBackground(UIConstants.PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
    }

    protected void addSidebarSeparator(String sectionTitle) {
        JLabel label = new JLabel("  " + sectionTitle.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(100, 120, 150));
        label.setBorder(BorderFactory.createEmptyBorder(16, 20, 4, 20));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(label);
    }

    private void handleLogout() {
        if (UIConstants.confirmDialog(this, "Are you sure you want to logout?")) {
            SessionManager.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> new com.school.gui.auth.LoginFrame().setVisible(true));
        }
    }

    protected abstract void buildDashboard();
}
