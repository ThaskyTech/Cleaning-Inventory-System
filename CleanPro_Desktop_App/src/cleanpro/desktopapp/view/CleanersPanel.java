package cleanpro.desktopapp.view;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class CleanersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, phoneField, emailField, deptField;
    private JCheckBox activeBox;
    private final cleanpro.desktopapp.controller.CleanerController cleanerController =
        new cleanpro.desktopapp.controller.CleanerController();

    public CleanersPanel() {
        setLayout(new MigLayout("fill, insets 24", "[grow]", "[grow]"));
        setBackground(UITheme.FIELD_BG);
        add(buildContent(), "grow");
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new MigLayout(
            "wrap, fillx, insets 0, gapy 16",
            "[grow]",
            "[][][grow]"
        ));
        panel.setBackground(UITheme.FIELD_BG);

        JLabel title = new JLabel("Cleaners / Staff Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.TEXT_DARK);
        panel.add(title, "wrap");

        JPanel topBar = new JPanel(new MigLayout("insets 0, fillx", "[grow][]", "[center]"));
        topBar.setBackground(UITheme.FIELD_BG);

        UIComponents.RoundedTextField searchField = new UIComponents.RoundedTextField("Search cleaners...");
        topBar.add(searchField, "growx");

        UIComponents.AnimatedButton addBtn = new UIComponents.AnimatedButton(
            "+ Add Cleaner", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.addActionListener(e -> showCleanerDialog(null));
        topBar.add(addBtn, "width 150!, height 40!");
        panel.add(topBar, "growx, wrap");

        String[] cols = {"ID", "Full Name", "Phone", "Email", "Department", "Status", "Created"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        JScrollPane scroll = createStyledScrollPane(table);
        panel.add(scroll, "grow, wrap");

        loadCleaners();

        JPanel actionPanel = new JPanel(new MigLayout("insets 0, gapx 8"));
        actionPanel.setBackground(UITheme.FIELD_BG);

        UIComponents.AnimatedButton editBtn = new UIComponents.AnimatedButton(
            "Edit", new Color(255, 152, 0), new Color(230, 130, 0));
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showCleanerDialog(row);
            else JOptionPane.showMessageDialog(this, "Please select a cleaner to edit.");
        });

        UIComponents.AnimatedButton deleteBtn = new UIComponents.AnimatedButton(
            "Delete", UITheme.DANGER, UITheme.DANGER_HOVER);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.addActionListener(e -> deleteSelected());

        actionPanel.add(editBtn, "width 100!, height 38!");
        actionPanel.add(deleteBtn, "width 100!, height 38!");
        panel.add(actionPanel, "align right");

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(40);
        t.setGridColor(new Color(230, 235, 235));
        t.setSelectionBackground(new Color(0, 168, 150, 30));
        t.setSelectionForeground(UITheme.TEXT_DARK);
        t.setShowGrid(true);
        t.setGridColor(new Color(220, 230, 230));
        t.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = t.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(UITheme.TEAL_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(null);

        return t;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 230), 1));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private void loadCleaners() {
        model.setRowCount(0);
        for (cleanpro.desktopapp.model.Cleaner c : cleanerController.getAllCleaners()) {
            model.addRow(new Object[]{
                c.getCleanerId(),
                c.getFirstName() + " " + c.getLastName(),
                c.getPhoneNumber(),
                c.getEmail(),
                c.isActive() ? "Active" : "Inactive",
                c.getEmploymentDate()
            });
        }
    }

    private void showCleanerDialog(Integer rowIndex) {
        boolean isEdit = rowIndex != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Cleaner" : "Add Cleaner", true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        nameField = new UIComponents.RoundedTextField("Enter full name");
        phoneField = new UIComponents.RoundedTextField("Enter phone number");
        emailField = new UIComponents.RoundedTextField("Enter email address");
        deptField = new UIComponents.RoundedTextField("Enter department");
        activeBox = new JCheckBox("Active");
        activeBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeBox.setSelected(true);

        if (isEdit) {
            nameField.setText((String) model.getValueAt(rowIndex, 1));
            phoneField.setText((String) model.getValueAt(rowIndex, 2));
            emailField.setText((String) model.getValueAt(rowIndex, 3));
            deptField.setText((String) model.getValueAt(rowIndex, 4));
            activeBox.setSelected("Active".equals(model.getValueAt(rowIndex, 5)));
        }

        form.add(newFormLabel("Full Name:")); form.add(nameField, "growx, height 38!");
        form.add(newFormLabel("Phone:")); form.add(phoneField, "growx, height 38!");
        form.add(newFormLabel("Email:")); form.add(emailField, "growx, height 38!");
        form.add(newFormLabel("Department:")); form.add(deptField, "growx, height 38!");
        form.add(new JLabel("")); form.add(activeBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new MigLayout("insets 16 24 24 24, align right"));
        btnPanel.setBackground(Color.WHITE);

        UIComponents.AnimatedButton cancelBtn = new UIComponents.AnimatedButton(
            "Cancel", new Color(180, 190, 190), new Color(160, 170, 170));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.addActionListener(e -> dialog.dispose());

        UIComponents.AnimatedButton saveBtn = new UIComponents.AnimatedButton(
            isEdit ? "Update" : "Save", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.addActionListener(e -> {
            try {
                String[] parts = nameField.getText().trim().split(" ", 2);
                String first = parts[0];
                String last = parts.length > 1 ? parts[1] : "";

                if (isEdit) {
                    int cleanerId = (int) model.getValueAt(rowIndex, 0);
                    cleanpro.desktopapp.model.Cleaner c = new cleanpro.desktopapp.model.Cleaner(
                        cleanerId, "C-" + cleanerId, first, last,
                        phoneField.getText().trim(), emailField.getText().trim(),
                        java.time.LocalDate.now(), activeBox.isSelected());
                    cleanerController.updateCleaner(c);
                } else {
                    cleanpro.desktopapp.model.Cleaner c = new cleanpro.desktopapp.model.Cleaner(
                        0, "C-" + System.currentTimeMillis() % 100000, first, last,
                        phoneField.getText().trim(), emailField.getText().trim(),
                        java.time.LocalDate.now(), activeBox.isSelected());
                    cleanerController.addCleaner(c);
                }
                loadCleaners();
                dialog.dispose();
            } catch (cleanpro.desktopapp.service.exceptions.ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn, "width 100!, height 38!, gapx 8");
        btnPanel.add(saveBtn, "width 100!, height 38!");
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel newFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a cleaner to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this cleaner?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int cleanerId = (int) model.getValueAt(row, 0);
                cleanerController.deleteCleaner(cleanerId);
                loadCleaners();
            } catch (cleanpro.desktopapp.service.exceptions.ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}