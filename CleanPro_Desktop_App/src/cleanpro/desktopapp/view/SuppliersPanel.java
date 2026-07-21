package cleanpro.desktopapp.view;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class SuppliersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, contactField, phoneField, emailField, addressField;
    private int selectedRow = -1;

    public SuppliersPanel() {
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

        // Header
        JLabel title = new JLabel("Suppliers Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.TEXT_DARK);
        panel.add(title, "wrap");

        // Search + Add bar
        JPanel topBar = new JPanel(new MigLayout("insets 0, fillx", "[grow][]", "[center]"));
        topBar.setBackground(UITheme.FIELD_BG);

        UIComponents.RoundedTextField searchField = new UIComponents.RoundedTextField("Search suppliers...");
        topBar.add(searchField, "growx");

        UIComponents.AnimatedButton addBtn = new UIComponents.AnimatedButton(
            "+ Add Supplier", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.addActionListener(e -> showSupplierDialog(null));
        topBar.add(addBtn, "width 140!, height 40!");
        panel.add(topBar, "growx, wrap");

        // Table
        String[] cols = {"ID", "Supplier Name", "Contact Person", "Phone", "Email", "Address", "Created"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        JScrollPane scroll = createStyledScrollPane(table);
        panel.add(scroll, "grow, wrap");

        // Load sample data
        loadSampleData();

        // Action buttons panel
        JPanel actionPanel = new JPanel(new MigLayout("insets 0, gapx 8"));
        actionPanel.setBackground(UITheme.FIELD_BG);

        UIComponents.AnimatedButton editBtn = new UIComponents.AnimatedButton(
            "Edit", new Color(255, 152, 0), new Color(230, 130, 0));
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showSupplierDialog(row);
            else JOptionPane.showMessageDialog(this, "Please select a supplier to edit.");
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

    private void loadSampleData() {
        model.addRow(new Object[]{1, "CleanSupplies Co.", "John Smith", "555-0101", "john@cleansupplies.com", "123 Main St", "2024-01-15"});
        model.addRow(new Object[]{2, "EcoClean Ltd", "Sarah Johnson", "555-0102", "sarah@ecoclean.com", "456 Oak Ave", "2024-02-20"});
        model.addRow(new Object[]{3, "Sparkle Materials", "Mike Brown", "555-0103", "mike@sparkle.com", "789 Pine Rd", "2024-03-10"});
    }

    private void showSupplierDialog(Integer rowIndex) {
        boolean isEdit = rowIndex != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            isEdit ? "Edit Supplier" : "Add Supplier", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        nameField = new UIComponents.RoundedTextField("Enter supplier name");
        contactField = new UIComponents.RoundedTextField("Enter contact person");
        phoneField = new UIComponents.RoundedTextField("Enter phone number");
        emailField = new UIComponents.RoundedTextField("Enter email address");
        addressField = new UIComponents.RoundedTextField("Enter address");

        if (isEdit) {
            nameField.setText((String) model.getValueAt(rowIndex, 1));
            contactField.setText((String) model.getValueAt(rowIndex, 2));
            phoneField.setText((String) model.getValueAt(rowIndex, 3));
            emailField.setText((String) model.getValueAt(rowIndex, 4));
            addressField.setText((String) model.getValueAt(rowIndex, 5));
        }

        form.add(newFormLabel("Supplier Name:")); form.add(nameField, "growx, height 38!");
        form.add(newFormLabel("Contact Person:")); form.add(contactField, "growx, height 38!");
        form.add(newFormLabel("Phone:")); form.add(phoneField, "growx, height 38!");
        form.add(newFormLabel("Email:")); form.add(emailField, "growx, height 38!");
        form.add(newFormLabel("Address:")); form.add(addressField, "growx, height 38!");

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
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Supplier name is required.");
                return;
            }
            if (isEdit) {
                model.setValueAt(nameField.getText(), rowIndex, 1);
                model.setValueAt(contactField.getText(), rowIndex, 2);
                model.setValueAt(phoneField.getText(), rowIndex, 3);
                model.setValueAt(emailField.getText(), rowIndex, 4);
                model.setValueAt(addressField.getText(), rowIndex, 5);
            } else {
                int newId = model.getRowCount() + 1;
                model.addRow(new Object[]{newId, nameField.getText(), contactField.getText(),
                    phoneField.getText(), emailField.getText(), addressField.getText(),
                    java.time.LocalDate.now().toString()});
            }
            dialog.dispose();
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
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this supplier?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
        }
    }
}