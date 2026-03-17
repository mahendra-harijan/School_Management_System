package com.school.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UIConstants {
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    public static final Color PRIMARY_DARK = new Color(13, 71, 161);
    public static final Color PRIMARY_LIGHT = new Color(100, 181, 246);
    public static final Color ACCENT_COLOR = new Color(255, 152, 0);
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    public static final Color DANGER_COLOR = new Color(244, 67, 54);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    public static final Color BG_COLOR = new Color(245, 247, 250);
    public static final Color SIDEBAR_COLOR = new Color(26, 35, 56);
    public static final Color SIDEBAR_HOVER = new Color(37, 51, 80);
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color BORDER_COLOR = new Color(222, 226, 230);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.PLAIN, 14);

    // Dimensions
    public static final int SIDEBAR_WIDTH = 230;
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 760;

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_SUBHEADING);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(DANGER_COLOR);
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(SUCCESS_COLOR);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(PRIMARY_COLOR);
        btn.setFont(FONT_SUBHEADING);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        return btn;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static JComboBox<?> createStyledComboBox(Object[] items) {
        JComboBox<Object> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return combo;
    }

    public static JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(FONT_SUBHEADING);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return table;
    }

    public static JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel topBar = new JPanel();
        topBar.setBackground(color);
        topBar.setPreferredSize(new Dimension(0, 4));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_BODY);
        titleLabel.setForeground(TEXT_SECONDARY);

        card.add(topBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmDialog(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
