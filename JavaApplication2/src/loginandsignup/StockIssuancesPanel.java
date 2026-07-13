package loginandsignup;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StockIssuancesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cleanerCombo, issuedByCombo;
    private JTextField notesField;
    private JSpinner itemCountSpinner;

    public StockIssuancesPanel() {
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

        JLabel title = new JLabel("Stock Issuances");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.TEXT_DARK);
        panel.add(title, "wrap");

        JPanel topBar = new JPanel(new MigLayout("insets 0, fillx", "[grow][]", "[center]"));
        topBar.setBackground(UITheme.FIELD_BG);

        UIComponents.RoundedTextField searchField = new UIComponents.RoundedTextField("Search issuances...");
        topBar.add(searchField, "growx");

        UIComponents.AnimatedButton addBtn = new UIComponents.AnimatedButton(
            "+ New Issuance", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.addActionListener(e -> showIssuanceDialog(null));
        topBar.add(addBtn, "width 150!, height 40!");
        panel.add(topBar, "growx, wrap");

        String[] cols = {"Issuance ID", "Cleaner", "Issued By", "Issue Date", "Items Count", "Notes"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        JScrollPane scroll = createStyledScrollPane(table);
        panel.add(scroll, "grow, wrap");

        loadSampleData();

        JPanel actionPanel = new JPanel(new MigLayout("insets 0, gapx 8"));
        actionPanel.setBackground(UITheme.FIELD_BG);

        UIComponents.AnimatedButton viewBtn = new UIComponents.AnimatedButton(
            "View Details", new Color(63, 81, 181), new Color(50, 65, 150));
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showIssuanceItems(row);
            else JOptionPane.showMessageDialog(this, "Please select an issuance to view.");
        });

        UIComponents.AnimatedButton deleteBtn = new UIComponents.AnimatedButton(
            "Delete", UITheme.DANGER, UITheme.DANGER_HOVER);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.addActionListener(e -> deleteSelected());

        actionPanel.add(viewBtn, "width 130!, height 38!");
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
        model.addRow(new Object[]{101, "Alice Cooper", "Admin", "2024-07-01", 3, "Weekly supplies"});
        model.addRow(new Object[]{102, "Bob Martinez", "Admin", "2024-07-03", 5, "Deep cleaning kit"});
        model.addRow(new Object[]{103, "David Lee", "Manager", "2024-07-05", 2, "Emergency restock"});
    }

    private void showIssuanceDialog(Integer rowIndex) {
        boolean isEdit = rowIndex != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Issuance" : "New Stock Issuance", true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        cleanerCombo = new JComboBox<>(new String[]{"Alice Cooper", "Bob Martinez", "Carol White", "David Lee"});
        issuedByCombo = new JComboBox<>(new String[]{"Admin", "Manager", "Supervisor"});
        notesField = new UIComponents.RoundedTextField("Enter notes...");
        itemCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));

        styleCombo(cleanerCombo);
        styleCombo(issuedByCombo);

        if (isEdit) {
            cleanerCombo.setSelectedItem(model.getValueAt(rowIndex, 1));
            issuedByCombo.setSelectedItem(model.getValueAt(rowIndex, 2));
            notesField.setText((String) model.getValueAt(rowIndex, 5));
            itemCountSpinner.setValue(model.getValueAt(rowIndex, 4));
        }

        form.add(newFormLabel("Cleaner:")); form.add(cleanerCombo, "growx, height 38!");
        form.add(newFormLabel("Issued By:")); form.add(issuedByCombo, "growx, height 38!");
        form.add(newFormLabel("Items Count:")); form.add(itemCountSpinner, "width 120!, height 38!");
        form.add(newFormLabel("Notes:")); form.add(notesField, "growx, height 38!");

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
            if (isEdit) {
                model.setValueAt(cleanerCombo.getSelectedItem(), rowIndex, 1);
                model.setValueAt(issuedByCombo.getSelectedItem(), rowIndex, 2);
                model.setValueAt(itemCountSpinner.getValue(), rowIndex, 4);
                model.setValueAt(notesField.getText(), rowIndex, 5);
            } else {
                int newId = 100 + model.getRowCount() + 1;
                model.addRow(new Object[]{newId, cleanerCombo.getSelectedItem(),
                    issuedByCombo.getSelectedItem(), java.time.LocalDate.now().toString(),
                    itemCountSpinner.getValue(), notesField.getText()});
            }
            dialog.dispose();
        });

        btnPanel.add(cancelBtn, "width 100!, height 38!, gapx 8");
        btnPanel.add(saveBtn, "width 100!, height 38!");
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showIssuanceItems(int row) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Issuance Items - ID " + model.getValueAt(row, 0), true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"Item ID", "Material", "Quantity Issued"};
        DefaultTableModel itemModel = new DefaultTableModel(cols, 0);
        itemModel.addRow(new Object[]{1, "Disinfectant Spray", 2});
        itemModel.addRow(new Object[]{2, "Microfiber Cloths", 5});
        itemModel.addRow(new Object[]{3, "Floor Cleaner", 1});

        JTable itemTable = new JTable(itemModel);
        itemTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemTable.setRowHeight(36);
        JScrollPane sp = new JScrollPane(itemTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 230)));

        dialog.add(sp, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new MigLayout("insets 16, align right"));
        btnPanel.setBackground(Color.WHITE);
        UIComponents.AnimatedButton closeBtn = new UIComponents.AnimatedButton(
            "Close", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        closeBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(closeBtn, "width 100!, height 38!");
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
            JOptionPane.showMessageDialog(this, "Please select an issuance to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this issuance?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
        }
    }
}