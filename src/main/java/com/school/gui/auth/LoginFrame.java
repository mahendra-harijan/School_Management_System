package com.school.gui.auth;

import com.school.database.UserDAO;
import com.school.gui.admin.AdminDashboard;
import com.school.gui.student.StudentDashboard;
import com.school.gui.teacher.TeacherDashboard;
import com.school.model.User;
import com.school.util.SessionManager;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("School Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left panel - branding
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.PRIMARY_DARK, getWidth(), getHeight(), new Color(21, 101, 192));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new GridBagLayout());

        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);

        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("SchoolPro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(100, 150, 220));
        sep.setMaximumSize(new Dimension(200, 2));

        String[] features = {"✓  Student Management", "✓  Teacher Portal", "✓  Academic Reports", "✓  Fee Management"};
        brandContent.add(iconLabel);
        brandContent.add(Box.createRigidArea(new Dimension(0, 16)));
        brandContent.add(titleLabel);
        brandContent.add(Box.createRigidArea(new Dimension(0, 4)));
        brandContent.add(subtitleLabel);
        brandContent.add(Box.createRigidArea(new Dimension(0, 24)));
        brandContent.add(sep);
        brandContent.add(Box.createRigidArea(new Dimension(0, 16)));

        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fl.setForeground(new Color(200, 225, 255));
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);
            brandContent.add(fl);
            brandContent.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        leftPanel.add(brandContent);

        // Right panel - login form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UIConstants.BG_COLOR);

        JPanel formCard = UIConstants.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(340, 420));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(UIConstants.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginSubtitle = new JLabel("Sign in to your account");
        loginSubtitle.setFont(UIConstants.FONT_BODY);
        loginSubtitle.setForeground(UIConstants.TEXT_SECONDARY);
        loginSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UIConstants.FONT_SUBHEADING);
        userLabel.setForeground(UIConstants.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = UIConstants.createStyledTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIConstants.FONT_SUBHEADING);
        passLabel.setForeground(UIConstants.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = UIConstants.createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = UIConstants.createPrimaryButton("Sign In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        registerButton = UIConstants.createSecondaryButton("Create New Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        formCard.add(loginTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(loginSubtitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 24)));
        formCard.add(userLabel);
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));
        formCard.add(usernameField);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));
        formCard.add(passLabel);
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));
        formCard.add(passwordField);
        formCard.add(Box.createRigidArea(new Dimension(0, 24)));
        formCard.add(loginButton);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        formCard.add(registerButton);

        rightPanel.add(formCard);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

        // Event listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegister());
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        // Hover effects
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginButton.setBackground(UIConstants.PRIMARY_DARK); }
            public void mouseExited(MouseEvent e) { loginButton.setBackground(UIConstants.PRIMARY_COLOR); }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UIConstants.showError(this, "Please enter both username and password.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            protected User doInBackground() { return userDAO.authenticate(username, password); }
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        SessionManager.getInstance().setCurrentUser(user);
                        dispose();
                        openDashboard(user);
                    } else {
                        UIConstants.showError(LoginFrame.this, "Invalid username or password.");
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    UIConstants.showError(LoginFrame.this, "Login failed: " + ex.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                }
            }
        };
        worker.execute();
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            JFrame dashboard;
            switch (user.getRole()) {
                case "ADMIN": dashboard = new AdminDashboard(); break;
                case "TEACHER": dashboard = new TeacherDashboard(); break;
                case "STUDENT": dashboard = new StudentDashboard(); break;
                default: UIConstants.showError(null, "Unknown role."); return;
            }
            dashboard.setVisible(true);
        });
    }

    private void openRegister() {
        new RegisterFrame(this).setVisible(true);
    }
}
