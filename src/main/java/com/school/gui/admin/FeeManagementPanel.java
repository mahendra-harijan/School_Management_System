package com.school.gui.admin;

import com.school.database.*;
import com.school.model.*;
import com.school.util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class FeeManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private FeeDAO feeDAO;
    private StudentDAO studentDAO;

    public FeeManagementPanel() {
        feeDAO = new FeeDAO(); studentDAO = new StudentDAO();
        setLayout(new BorderLayout()); setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.add(UIConstants.createTitleLabel("Fee Management"), BorderLayout.WEST);
        JButton addBtn = UIConstants.createPrimaryButton("+ Add Fee");
        addBtn.addActionListener(e -> showFeeDialog(null));
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Roll No", "Student", "Fee Type", "Amount", "Due Date", "Status", "Paid Date"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = UIConstants.createStyledTable(); table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Color rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    String status = (String) t.getModel().getValueAt(r, 6);
                    if ("Paid".equals(status)) setBackground(new Color(232, 245, 233));
                    else if ("Overdue".equals(status)) setBackground(new Color(255, 235, 238));
                    else setBackground(Color.WHITE);
                }
                return this;
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); actions.setOpaque(false);
        JButton markPaidBtn = UIConstants.createSuccessButton("✓ Mark Paid");
        JButton editBtn = UIConstants.createSecondaryButton("✏ Edit");
        JButton refreshBtn = UIConstants.createSecondaryButton("↻ Refresh");
        markPaidBtn.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) { UIConstants.showError(this, "Select a fee record."); return; }
            int id = (int) tableModel.getValueAt(r, 0);
            feeDAO.getAllFees().stream().filter(f -> f.getId() == id).findFirst().ifPresent(f -> {
                f.setStatus("Paid"); f.setPaidDate(new Date(System.currentTimeMillis())); f.setPaymentMethod("Cash");
                if (feeDAO.updateFee(f)) { UIConstants.showSuccess(this, "Marked as paid."); loadData(); }
            }); });
        editBtn.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) return;
            int id = (int) tableModel.getValueAt(r, 0);
            feeDAO.getAllFees().stream().filter(f -> f.getId() == id).findFirst().ifPresent(f -> showFeeDialog(f)); });
        refreshBtn.addActionListener(e -> loadData());
        actions.add(markPaidBtn); actions.add(editBtn); actions.add(refreshBtn);

        // Summary
        double pending = feeDAO.getTotalPendingFees();
        JLabel summary = new JLabel(String.format("  Total Pending Fees: ₹%.2f", pending));
        summary.setFont(UIConstants.FONT_SUBHEADING); summary.setForeground(UIConstants.DANGER_COLOR);

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout()); bottom.setOpaque(false);
        bottom.add(actions, BorderLayout.WEST); bottom.add(summary, BorderLayout.EAST);
        center.add(bottom, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Fee f : feeDAO.getAllFees())
            tableModel.addRow(new Object[]{f.getId(), f.getRollNumber(), f.getStudentName(), f.getFeeType(),
                String.format("₹%.2f", f.getAmount()), f.getDueDate(), f.getStatus(), f.getPaidDate()});
    }

    private void showFeeDialog(Fee existing) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing == null ? "Add Fee" : "Edit Fee", true);
        d.setSize(420, 360); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridBagLayout()); p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 6, 6, 6);

        List<Student> students = studentDAO.getAllStudents();
        JComboBox<Student> studentCb = new JComboBox<>(students.toArray(new Student[0]));
        JComboBox<String> feeTypeCb = new JComboBox<>(new String[]{"Tuition Fee", "Exam Fee", "Library Fee", "Lab Fee", "Transport Fee", "Other"});
        JTextField amtField = UIConstants.createStyledTextField();
        JTextField dueDateField = UIConstants.createStyledTextField(); dueDateField.setText(new Date(System.currentTimeMillis()).toString());
        JComboBox<String> statusCb = new JComboBox<>(new String[]{"Pending", "Paid", "Overdue", "Waived"});
        JTextField remarksField = UIConstants.createStyledTextField();

        if (existing != null) {
            for (int i = 0; i < students.size(); i++) if (students.get(i).getId() == existing.getStudentId()) { studentCb.setSelectedIndex(i); break; }
            feeTypeCb.setSelectedItem(existing.getFeeType()); amtField.setText(String.valueOf(existing.getAmount()));
            if (existing.getDueDate() != null) dueDateField.setText(existing.getDueDate().toString());
            statusCb.setSelectedItem(existing.getStatus()); if (existing.getRemarks() != null) remarksField.setText(existing.getRemarks());
        }

        String[] labels = {"Student *", "Fee Type *", "Amount *", "Due Date", "Status", "Remarks"};
        JComponent[] comps = {studentCb, feeTypeCb, amtField, dueDateField, statusCb, remarksField};
        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.4; JLabel l = new JLabel(labels[i]); l.setFont(UIConstants.FONT_SUBHEADING); p.add(l, gc);
            gc.gridx = 1; gc.weightx = 0.6; p.add(comps[i], gc);
        }
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sv = UIConstants.createPrimaryButton("Save");
        sv.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amtField.getText().trim());
                Fee f = existing != null ? existing : new Fee();
                Student st = (Student) studentCb.getSelectedItem(); if (st == null) { UIConstants.showError(d, "Select student."); return; }
                f.setStudentId(st.getId()); f.setFeeType((String) feeTypeCb.getSelectedItem()); f.setAmount(amt);
                try { f.setDueDate(Date.valueOf(dueDateField.getText().trim())); } catch (Exception ex) {}
                f.setStatus((String) statusCb.getSelectedItem()); f.setRemarks(remarksField.getText().trim());
                boolean ok = existing == null ? feeDAO.addFee(f) : feeDAO.updateFee(f);
                if (ok) { UIConstants.showSuccess(d, "Saved."); loadData(); d.dispose(); } else UIConstants.showError(d, "Failed.");
            } catch (NumberFormatException ex) { UIConstants.showError(d, "Invalid amount."); }
        });
        JButton cn = UIConstants.createSecondaryButton("Cancel"); cn.addActionListener(e -> d.dispose());
        bp.add(cn); bp.add(sv);
        gc.gridy = labels.length; gc.gridx = 0; gc.gridwidth = 2; p.add(bp, gc);
        d.add(p); d.setVisible(true);
    }
}
