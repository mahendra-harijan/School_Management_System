package com.school;

import com.school.database.DatabaseConnection;
import com.school.gui.auth.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // Global rendering hints for better text quality
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Test database connection
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection db = DatabaseConnection.getInstance();
            if (!db.testConnection()) {
                int choice = JOptionPane.showConfirmDialog(null,
                    "Could not connect to database.\n\n" +
                    "Please ensure:\n" +
                    "1. MySQL is running on localhost:3306\n" +
                    "2. Database 'school_management' exists\n" +
                    "3. Username 'root' with correct password\n" +
                    "4. Run schema.sql to initialize the database\n\n" +
                    "Continue anyway?",
                    "Database Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
