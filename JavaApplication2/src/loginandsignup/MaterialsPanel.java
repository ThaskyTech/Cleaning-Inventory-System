package loginandsignup;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaterialsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, descField, categoryField, unitField;
    private JSpinner qtySpinner, reorderSpinner;
    private JComboBox<String> supplierCombo;
    private int selectedRow = -1;

    public MaterialsPanel() {
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

        JLabel title = new JLabel("Inventory / Materials");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.TEXT_DARK);
        panel.add(title, "wrap");

        // Search + Add bar
        JPanel topBar = new JPanel(new MigLayout("insets 0, fillx", "[grow][]", "[center]"));
        topBar.setBackground(UITheme.FIELD_BG);

        UIComponents.RoundedTextField searchField = new UIComponents.RoundedTextField("Search materials...");
        topBar.add(searchField, "growx");

        UIComponents.AnimatedButton addBtn = new UIComponents.AnimatedButton(
            "+ Add Material", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.addActionListener(e -> showMaterialDialog(null));
        topBar.add(addBtn, "width 150!, height 40!");
        panel.add(topBar, "growx, wrap");

        // Table
        String[] cols = {"ID", "Material Name", "Category", "Unit", "Qty Available", "Reorder Level", "Supplier"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        JScrollPane scroll = createStyledScrollPane(table);
        panel.add(scroll, "grow, wrap");

        loadSampleData();

        // Action buttons
        JPanel actionPanel = new JPanel(new MigLayout("insets 0, gapx 8"));
        actionPanel.setBackground(UITheme.FIELD_BG);

        UIComponents.AnimatedButton editBtn = new UIComponents.AnimatedButton(
            "Edit", new Color(255, 152, 0), new Color(230, 130, 0));
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showMaterialDialog(row);
            else JOptionPane.showMessageDialog(this, "Please select a material to edit.");
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
        model.addRow(new Object[]{1, "Disinfectant Spray", "Chemicals", "Litre", 50, 10, "CleanSupplies Co."});
        model.addRow(new Object[]{2, "Microfiber Cloths", "Tools", "Pack", 120, 20, "EcoClean Ltd"});
        model.addRow(new Object[]{3, "Floor Cleaner", "Chemicals", "Litre", 30, 5, "Sparkle Materials"});
        model.addRow(new Object[]{4, "Vacuum Bags", "Tools", "Pack", 45, 8, "CleanSupplies Co."});
        model.addRow(new Object[]{5, "Glass Cleaner", "Chemicals", "Litre", 25, 5, "EcoClean Ltd"});
    }

    private void showMaterialDialog(Integer rowIndex) {
        boolean isEdit = rowIndex != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Material" : "Add Material", true);
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        nameField = new UIComponents.RoundedTextField("Enter material name");
        descField = new UIComponents.RoundedTextField("Enter description");
        categoryField = new UIComponents.RoundedTextField("Enter category");
        unitField = new UIComponents.RoundedTextField("e.g. Litre, Pack, Box");
        qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        reorderSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        supplierCombo = new JComboBox<>(new String[]{"CleanSupplies Co.", "EcoClean Ltd", "Sparkle Materials"});
        styleCombo(supplierCombo);

        if (isEdit) {
            nameField.setText((String) model.getValueAt(rowIndex, 1));
            categoryField.setText((String) model.getValueAt(rowIndex, 2));
            unitField.setText((String) model.getValueAt(rowIndex, 3));
            qtySpinner.setValue(model.getValueAt(rowIndex, 4));
            reorderSpinner.setValue(model.getValueAt(rowIndex, 5));
            supplierCombo.setSelectedItem(model.getValueAt(rowIndex, 6));
        }

        form.add(newFormLabel("Material Name:")); form.add(nameField, "growx, height 38!");
        form.add(newFormLabel("Description:")); form.add(descField, "growx, height 38!");
        form.add(newFormLabel("Category:")); form.add(categoryField, "growx, height 38!");
        form.add(newFormLabel("Unit:")); form.add(unitField, "growx, height 38!");
        form.add(newFormLabel("Quantity Available:")); form.add(qtySpinner, "width 120!, height 38!");
        form.add(newFormLabel("Reorder Level:")); form.add(reorderSpinner, "width 120!, height 38!");
        form.add(newFormLabel("Supplier:")); form.add(supplierCombo, "growx, height 38!");

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
                JOptionPane.showMessageDialog(dialog, "Material name is required.");
                return;
            }
            if (isEdit) {
                model.setValueAt(nameField.getText(), rowIndex, 1);
                model.setValueAt(categoryField.getText(), rowIndex, 2);
                model.setValueAt(unitField.getText(), rowIndex, 3);
                model.setValueAt(qtySpinner.getValue(), rowIndex, 4);
                model.setValueAt(reorderSpinner.getValue(), rowIndex, 5);
                model.setValueAt(supplierCombo.getSelectedItem(), rowIndex, 6);
            } else {
                int newId = model.getRowCount() + 1;
                model.addRow(new Object[]{newId, nameField.getText(), categoryField.getText(),
                    unitField.getText(), qtySpinner.getValue(), reorderSpinner.getValue(),
                    supplierCombo.getSelectedItem()});
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
            JOptionPane.showMessageDialog(this, "Please select a material to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this material?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
        }
    }
}