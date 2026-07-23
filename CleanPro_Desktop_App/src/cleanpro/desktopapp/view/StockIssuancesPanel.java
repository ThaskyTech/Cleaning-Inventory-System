package cleanpro.desktopapp.view;

import cleanpro.desktopapp.controller.CleanerController;
import cleanpro.desktopapp.controller.MaterialController;
import cleanpro.desktopapp.controller.StockIssuanceController;
import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.StockIssuance;
import cleanpro.desktopapp.model.StockIssuanceItem;
import cleanpro.desktopapp.service.exceptions.InsufficientStockException;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.SessionManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StockIssuancesPanel extends JPanel {

    private final StockIssuanceController stockIssuanceController = new StockIssuanceController();
    private final CleanerController cleanerController = new CleanerController();
    private final MaterialController materialController = new MaterialController();

    private JTable table;
    private DefaultTableModel model;

    public StockIssuancesPanel() {
        setLayout(new MigLayout("fill, insets 24", "[grow]", "[grow]"));
        setBackground(UITheme.FIELD_BG);
        add(buildContent(), "grow");
        loadIssuances();
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 0, gapy 16", "[grow]", "[][][grow]"));
        panel.setBackground(UITheme.FIELD_BG);

        JLabel title = new JLabel("Stock Issuances");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.TEXT_DARK);
        panel.add(title, "wrap");

        JPanel topBar = new JPanel(new MigLayout("insets 0, fillx", "[grow][]"));
        topBar.setBackground(UITheme.FIELD_BG);

        UIComponents.RoundedTextField searchField = new UIComponents.RoundedTextField("Search issuances...");
        topBar.add(searchField, "growx");

        UIComponents.AnimatedButton addBtn = new UIComponents.AnimatedButton(
                "+ New Issuance", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        addBtn.addActionListener(e -> showIssuanceDialog());
        topBar.add(addBtn, "width 150!, height 40!");
        panel.add(topBar, "growx, wrap");

        String[] cols = {"Issuance ID", "Cleaner ID", "Issued By", "Issue Date", "Items Count", "Notes"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = createStyledTable(model);
        panel.add(new JScrollPane(table), "grow, wrap");

        return panel;
    }

    private void loadIssuances() {
        model.setRowCount(0);
        for (StockIssuance issuance : stockIssuanceController.getAllIssuances()) {
            model.addRow(new Object[]{
                    issuance.getIssuanceId(),
                    issuance.getCleanerId(),
                    issuance.getIssuedByUserId(),
                    issuance.getIssuanceDate(),
                    issuance.getItems().size(),
                    issuance.getNotes()
            });
        }
    }

    private void showIssuanceDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "New Stock Issuance", true);
        dialog.setSize(720, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 24, gapy 12, gapx 16", "[right][grow]"));
        form.setBackground(Color.WHITE);

        JComboBox<Cleaner> cleanerCombo = new JComboBox<>();
        for (Cleaner cleaner : cleanerController.getAllCleaners()) {
            if (cleaner.isActive()) cleanerCombo.addItem(cleaner);
        }
        cleanerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof Cleaner cleaner) {
                    setText(cleaner.getCleanerId() + " - "
                            + cleaner.getFirstName() + " " + cleaner.getLastName());
                }
                return this;
            }
        });

        DefaultTableModel itemModel = new DefaultTableModel(
                new Object[]{"Material", "Quantity"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        JTable itemTable = new JTable(itemModel);
        itemTable.setRowHeight(32);

        List<Material> materials = materialController.getAllMaterials();
        JComboBox<Material> materialCombo = new JComboBox<>(materials.toArray(new Material[0]));
        materialCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof Material material) {
                    setText(material.getMaterialId() + " - " + material.getMaterialName()
                            + " (stock: " + material.getCurrentQuantity() + ")");
                }
                return this;
            }
        });

        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(e -> {
            Material material = (Material) materialCombo.getSelectedItem();
            if (material != null) itemModel.addRow(new Object[]{material, 1});
        });

        JButton removeItemBtn = new JButton("Remove Selected");
        removeItemBtn.addActionListener(e -> {
            int row = itemTable.getSelectedRow();
            if (row >= 0) itemModel.removeRow(row);
        });

        JTextArea notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        form.add(new JLabel("Cleaner:"));
        form.add(cleanerCombo, "growx, height 38!");
        form.add(new JLabel("Material:"));
        JPanel materialPanel = new JPanel(new BorderLayout(8, 0));
        materialPanel.add(materialCombo, BorderLayout.CENTER);
        materialPanel.add(addItemBtn, BorderLayout.EAST);
        form.add(materialPanel, "growx");

        form.add(new JLabel("Selected Items:"));
        JPanel itemsPanel = new JPanel(new BorderLayout(8, 8));
        itemsPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        itemsPanel.add(removeItemBtn, BorderLayout.SOUTH);
        form.add(itemsPanel, "grow, height 220!");

        form.add(new JLabel("Notes:"));
        form.add(new JScrollPane(notesArea), "growx, height 70!");

        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton issueBtn = new JButton("Issue Stock");

        cancelBtn.addActionListener(e -> dialog.dispose());
        issueBtn.addActionListener(e -> {
            Cleaner cleaner = (Cleaner) cleanerCombo.getSelectedItem();
            if (cleaner == null) {
                showError(dialog, "Please select a cleaner.");
                return;
            }

            List<StockIssuanceItem> items = new ArrayList<>();
            for (int row = 0; row < itemModel.getRowCount(); row++) {
                Material material = (Material) itemModel.getValueAt(row, 0);
                int quantity;

                try {
                    quantity = Integer.parseInt(itemModel.getValueAt(row, 1).toString());
                } catch (NumberFormatException ex) {
                    showError(dialog, "Each quantity must be a whole number.");
                    return;
                }

                items.add(new StockIssuanceItem(
                        0, 0, material.getMaterialId(), quantity, material.getUnitPrice()));
            }

            if (SessionManager.getCurrentUser() == null) {
                showError(dialog, "No logged-in user is available.");
                return;
            }

            try {
                stockIssuanceController.issueStock(
                        cleaner.getCleanerId(),
                        SessionManager.getCurrentUser().getUserId(),
                        items,
                        notesArea.getText().trim()
                );

                dialog.dispose();
                loadIssuances();
                JOptionPane.showMessageDialog(this,
                        "Stock issued successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (InsufficientStockException | ValidationException ex) {
                showError(dialog, ex.getMessage());
            }
        });

        buttons.add(cancelBtn);
        buttons.add(issueBtn);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                "Stock Issuance Error", JOptionPane.ERROR_MESSAGE);
    }

    private JTable createStyledTable(DefaultTableModel tableModel) {
        JTable t = new JTable(tableModel);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(40);
        t.setSelectionBackground(new Color(0, 168, 150, 30));
        t.setSelectionForeground(UITheme.TEXT_DARK);

        JTableHeader header = t.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(UITheme.TEAL_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 42));
        return t;
    }
}
