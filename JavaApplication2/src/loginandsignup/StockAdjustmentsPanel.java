package loginandsignup;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StockAdjustmentsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> materialCombo, adjustedByCombo, typeCombo;
    private JSpinner qtySpinner;
    private JTextField reasonField;

    public StockAdjustmentsPanel() {
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

        JLabel title = new JLabel("Stock Adjustments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.TEXT_DARK);
        panel.add(title, "wrap");

        JPanel topBar = new JPanel(new MigLayout("insets 0, fillx", "[grow][]", "[center]"));
        topBar.setBackground(UITheme.FIELD_BG);

        UIComponents.RoundedTextField searchField = new UIComponents.RoundedTextField("Search adjustments...");
        topBar.add(searchField, "growx");

        UIComponents.AnimatedButton addBtn = new UIComponents.AnimatedButton(
            "+ New Adjustment", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.addActionListener(e -> showAdjustmentDialog(null));
        topBar.add(addBtn, "width 160!, height 40!");
        panel.add(topBar, "growx, wrap");

        String[] cols = {"Adj. ID", "Material", "Adjusted By", "Type", "Quantity", "Reason", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        JScrollPane scroll = createStyledScrollPane(table);
        panel.add(scroll, "grow, wrap");

        loadSampleData();

        JPanel actionPanel = new JPanel(new MigLayout("insets 0, gapx 8"));
        actionPanel.setBackground(UITheme.FIELD_BG);

        UIComponents.AnimatedButton editBtn = new UIComponents.AnimatedButton(
            "Edit", new Color(255, 152, 0), new Color(230, 130, 0));
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showAdjustmentDialog(row);
            else JOptionPane.showMessageDialog(this, "Please select an adjustment to edit.");
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
        model.addRow(new Object[]{1, "Disinfectant Spray", "Admin", "Addition", 10, "Restocked from supplier", "2024-07-01"});
        model.addRow(new Object[]{2, "Microfiber Cloths", "Manager", "Deduction", 5, "Damaged items removed", "2024-07-02"});
        model.addRow(new Object[]{3, "Floor Cleaner", "Admin", "Addition", 20, "New batch received", "2024-07-04"});
    }

    private void showAdjustmentDialog(Integer rowIndex) {
        boolean isEdit = rowIndex != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Adjustment" : "New Stock Adjustment", true);
        dialog.setSize(520, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        materialCombo = new JComboBox<>(new String[]{"Disinfectant Spray", "Microfiber Cloths", "Floor Cleaner", "Vacuum Bags", "Glass Cleaner"});
        adjustedByCombo = new JComboBox<>(new String[]{"Admin", "Manager", "Supervisor"});
        typeCombo = new JComboBox<>(new String[]{"Addition", "Deduction", "Correction"});
        qtySpinner = new JSpinner(new SpinnerNumberModel(0, -999, 999, 1));
        reasonField = new UIComponents.RoundedTextField("Enter reason for adjustment");

        styleCombo(materialCombo);
        styleCombo(adjustedByCombo);
        styleCombo(typeCombo);

        if (isEdit) {
            materialCombo.setSelectedItem(model.getValueAt(rowIndex, 1));
            adjustedByCombo.setSelectedItem(model.getValueAt(rowIndex, 2));
            typeCombo.setSelectedItem(model.getValueAt(rowIndex, 3));
            qtySpinner.setValue(model.getValueAt(rowIndex, 4));
            reasonField.setText((String) model.getValueAt(rowIndex, 5));
        }

        form.add(newFormLabel("Material:")); form.add(materialCombo, "growx, height 38!");
        form.add(newFormLabel("Adjusted By:")); form.add(adjustedByCombo, "growx, height 38!");
        form.add(newFormLabel("Adjustment Type:")); form.add(typeCombo, "growx, height 38!");
        form.add(newFormLabel("Quantity:")); form.add(qtySpinner, "width 120!, height 38!");
        form.add(newFormLabel("Reason:")); form.add(reasonField, "growx, height 38!");

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
            if (reasonField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Reason is required.");
                return;
            }
            if (isEdit) {
                model.setValueAt(materialCombo.getSelectedItem(), rowIndex, 1);
                model.setValueAt(adjustedByCombo.getSelectedItem(), rowIndex, 2);
                model.setValueAt(typeCombo.getSelectedItem(), rowIndex, 3);
                model.setValueAt(qtySpinner.getValue(), rowIndex, 4);
                model.setValueAt(reasonField.getText(), rowIndex, 5);
            } else {
                int newId = model.getRowCount() + 1;
                model.addRow(new Object[]{newId, materialCombo.getSelectedItem(),
                    adjustedByCombo.getSelectedItem(), typeCombo.getSelectedItem(),
                    qtySpinner.getValue(), reasonField.getText(),
                    java.time.LocalDate.now().toString()});
            }
            dialog.dispose();
        });

        btnPanel.add(cancelBtn, "width 100!, height 38!, gapx 8");
        btnPanel.add(saveBtn, "width 100!, height 38!");
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(UITheme.FIELD_BG);
        combo.setForeground(UITheme.TEXT_DARK);
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
            JOptionPane.showMessageDialog(this, "Please select an adjustment to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this adjustment?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
        }
    }
}
