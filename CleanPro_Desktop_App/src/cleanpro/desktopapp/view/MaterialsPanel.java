package cleanpro.desktopapp.view;

import cleanpro.desktopapp.controller.MaterialController;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.Supplier;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MaterialsPanel extends JPanel {

    private final MaterialController materialController = new MaterialController();

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, descField, unitField, unitPriceField;
    private JSpinner qtySpinner, reorderSpinner, maxStockSpinner;
    private JComboBox<Supplier> supplierCombo;

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

        String[] cols = {"ID", "Material Name", "Description", "Unit", "Qty Available", "Reorder Level", "Max Stock", "Unit Price", "Supplier"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        JScrollPane scroll = createStyledScrollPane(table);
        panel.add(scroll, "grow, wrap");

        loadMaterials();

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

    // Pulls real rows from the database via the controller.
    private void loadMaterials() {
        model.setRowCount(0);
        List<Supplier> suppliers = materialController.getAllSuppliers();
        for (Material m : materialController.getAllMaterials()) {
            String supplierName = suppliers.stream()
                .filter(s -> s.getSupplierId() == m.getSupplierId())
                .map(Supplier::getSupplierName)
                .findFirst().orElse("Unknown");
            model.addRow(new Object[]{
                m.getMaterialId(), m.getMaterialName(), m.getMaterialDescription(),
                m.getUnit(), m.getCurrentQuantity(), m.getReorderLevel(),
                m.getMaximumStockLevel(), m.getUnitPrice(), supplierName
            });
        }
    }

    private void showMaterialDialog(Integer rowIndex) {
        boolean isEdit = rowIndex != null;
        int materialId = isEdit ? (int) model.getValueAt(rowIndex, 0) : 0;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Material" : "Add Material", true);
        dialog.setSize(520, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        nameField = new UIComponents.RoundedTextField("Enter material name");
        descField = new UIComponents.RoundedTextField("Enter description");
        unitField = new UIComponents.RoundedTextField("e.g. Litre, Pack, Box");
        unitPriceField = new UIComponents.RoundedTextField("e.g. 49.99");
        qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        reorderSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        maxStockSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));

        List<Supplier> suppliers = materialController.getAllSuppliers();
        supplierCombo = new JComboBox<>(suppliers.toArray(new Supplier[0]));
        supplierCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Supplier s) setText(s.getSupplierName());
                return this;
            }
        });
        styleCombo(supplierCombo);

        if (isEdit) {
            Material m = materialController.getAllMaterials().stream()
                .filter(mm -> mm.getMaterialId() == materialId).findFirst().orElse(null);
            if (m != null) {
                nameField.setText(m.getMaterialName());
                descField.setText(m.getMaterialDescription());
                unitField.setText(m.getUnit());
                unitPriceField.setText(m.getUnitPrice() != null ? m.getUnitPrice().toString() : "");
                qtySpinner.setValue(m.getCurrentQuantity());
                reorderSpinner.setValue(m.getReorderLevel());
                maxStockSpinner.setValue(m.getMaximumStockLevel());
                suppliers.stream().filter(s -> s.getSupplierId() == m.getSupplierId())
                    .findFirst().ifPresent(supplierCombo::setSelectedItem);
            }
        }

        form.add(newFormLabel("Material Name:")); form.add(nameField, "growx, height 38!");
        form.add(newFormLabel("Description:")); form.add(descField, "growx, height 38!");
        form.add(newFormLabel("Unit:")); form.add(unitField, "growx, height 38!");
        form.add(newFormLabel("Unit Price:")); form.add(unitPriceField, "growx, height 38!");
        form.add(newFormLabel("Quantity Available:")); form.add(qtySpinner, "width 120!, height 38!");
        form.add(newFormLabel("Reorder Level:")); form.add(reorderSpinner, "width 120!, height 38!");
        form.add(newFormLabel("Maximum Stock:")); form.add(maxStockSpinner, "width 120!, height 38!");
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
            Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
            if (selectedSupplier == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a supplier.");
                return;
            }
            BigDecimal unitPrice;
            try {
                unitPrice = new BigDecimal(unitPriceField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Unit price must be a valid number.");
                return;
            }

            Material material = new Material(
                materialId,
                nameField.getText().trim(),
                descField.getText().trim(),
                selectedSupplier.getSupplierId(),
                (int) qtySpinner.getValue(),
                (int) reorderSpinner.getValue(),
                (int) maxStockSpinner.getValue(),
                unitPrice,
                unitField.getText().trim()
            );

            try {
                if (isEdit) {
                    materialController.updateMaterial(material);
                } else {
                    materialController.addMaterial(material);
                }
                loadMaterials();
                dialog.dispose();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn, "width 100!, height 38!, gapx 8");
        btnPanel.add(saveBtn, "width 100!, height 38!");
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void styleCombo(JComboBox<Supplier> combo) {
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
            try {
                int materialId = (int) model.getValueAt(row, 0);
                materialController.deleteMaterial(materialId);
                loadMaterials();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}