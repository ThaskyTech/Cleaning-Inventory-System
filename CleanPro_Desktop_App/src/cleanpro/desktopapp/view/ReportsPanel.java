package cleanpro.desktopapp.view;

import cleanpro.desktopapp.service.ReportService;
import cleanpro.desktopapp.model.InventoryReportItem;
import cleanpro.desktopapp.model.IssuanceHistoryItem;
import cleanpro.desktopapp.model.LowStockReportItem;
import cleanpro.desktopapp.model.MaterialUsageItem;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Reports screen: lets the user pick one of the four required reports,
 * optionally filter by date range (only relevant for Issuance History and
 * Material Usage), view it in a table, and export it to CSV.
 *
 * Like DashboardPanel, this class only handles layout/display - all the
 * actual report generation happens in ReportService.
 */
public class ReportsPanel extends JPanel {

    private static final String INVENTORY_REPORT = "Inventory Report";
    private static final String LOW_STOCK_REPORT = "Low-Stock Report";
    private static final String ISSUANCE_HISTORY_REPORT = "Issuance History Report";
    private static final String MATERIAL_USAGE_REPORT = "Material Usage Report";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    private final ReportService reportService;

    private JComboBox<String> reportTypeComboBox;
    private JTextField fromDateField;
    private JTextField toDateField;
    private DefaultTableModel tableModel;
    private JTable resultsTable;

    public ReportsPanel() {
        this.reportService = new ReportService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Top controls: report type, date range, generate/export buttons ---
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        controlsPanel.add(new JLabel("Report:"));
        reportTypeComboBox = new JComboBox<>(new String[]{
                INVENTORY_REPORT, LOW_STOCK_REPORT, ISSUANCE_HISTORY_REPORT, MATERIAL_USAGE_REPORT
        });
        controlsPanel.add(reportTypeComboBox);

        controlsPanel.add(new JLabel("From (yyyy-MM-dd, optional):"));
        fromDateField = new JTextField(10);
        controlsPanel.add(fromDateField);

        controlsPanel.add(new JLabel("To (yyyy-MM-dd, optional):"));
        toDateField = new JTextField(10);
        controlsPanel.add(toDateField);

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateSelectedReport());
        controlsPanel.add(generateButton);

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportToCsv());
        controlsPanel.add(exportButton);

        add(controlsPanel, BorderLayout.NORTH);

        // --- Results table ---
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
    }

    private void generateSelectedReport() {
        String selectedReport = (String) reportTypeComboBox.getSelectedItem();

        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = parseOptionalDate(fromDateField.getText());
            toDate = parseOptionalDate(toDateField.getText());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Dates must be in yyyy-MM-dd format, e.g. 2026-07-01.",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (selectedReport) {
                case INVENTORY_REPORT:
                    showInventoryReport(reportService.generateInventoryReport());
                    break;
                case LOW_STOCK_REPORT:
                    showLowStockReport(reportService.generateLowStockReport());
                    break;
                case ISSUANCE_HISTORY_REPORT:
                    showIssuanceHistoryReport(reportService.generateIssuanceHistoryReport(fromDate, toDate));
                    break;
                case MATERIAL_USAGE_REPORT:
                    showMaterialUsageReport(reportService.generateMaterialUsageReport(fromDate, toDate));
                    break;
                default:
                    // Unreachable: combo box only offers the four values above
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate parseOptionalDate(String text) throws DateTimeParseException {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(text.trim(), DATE_FORMAT);
    }

    private void showInventoryReport(List<InventoryReportItem> items) {
        String[] columns = {"Material", "Supplier", "Qty", "Reorder Lvl", "Max Lvl", "Unit Price", "Unit", "Stock Value"};
        tableModel.setDataVector(new Object[0][0], columns);
        for (InventoryReportItem item : items) {
            tableModel.addRow(new Object[]{
                    item.getMaterialName(), item.getSupplierName(), item.getCurrentQuantity(),
                    item.getReorderLevel(), item.getMaximumStockLevel(), item.getUnitPrice(),
                    item.getUnit(), item.getStockValue()
            });
        }
    }

    private void showLowStockReport(List<LowStockReportItem> items) {
        String[] columns = {"Material", "Supplier", "Current Qty", "Reorder Level", "Shortfall"};
        tableModel.setDataVector(new Object[0][0], columns);
        for (LowStockReportItem item : items) {
            tableModel.addRow(new Object[]{
                    item.getMaterialName(), item.getSupplierName(), item.getCurrentQuantity(),
                    item.getReorderLevel(), item.getShortfall()
            });
        }
    }

    private void showIssuanceHistoryReport(List<IssuanceHistoryItem> items) {
        String[] columns = {"Issuance #", "Cleaner", "Issued By", "Date", "Status", "Total Items", "Total Value", "Notes"};
        tableModel.setDataVector(new Object[0][0], columns);
        for (IssuanceHistoryItem item : items) {
            tableModel.addRow(new Object[]{
                    item.getIssuanceNumber(), item.getCleanerFullName(), item.getIssuedByUsername(),
                    item.getIssuanceDate(), item.getStatus(), item.getTotalItemsIssued(),
                    item.getTotalValue(), item.getNotes()
            });
        }
    }

    private void showMaterialUsageReport(List<MaterialUsageItem> items) {
        String[] columns = {"Material", "Total Qty Issued", "Times Issued", "Total Value"};
        tableModel.setDataVector(new Object[0][0], columns);
        for (MaterialUsageItem item : items) {
            tableModel.addRow(new Object[]{
                    item.getMaterialName(), item.getTotalQuantityIssued(),
                    item.getNumberOfIssuances(), item.getTotalValue()
            });
        }
    }

    /** Writes whatever is currently in the results table out to a CSV file. */
    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Generate a report first before exporting.",
                    "Nothing to Export", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(reportTypeComboBox.getSelectedItem() + ".csv"));
        int userChoice = fileChooser.showSaveDialog(this);
        if (userChoice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
            int columnCount = tableModel.getColumnCount();

            for (int col = 0; col < columnCount; col++) {
                writer.print(tableModel.getColumnName(col));
                writer.print(col < columnCount - 1 ? "," : "\n");
            }

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < columnCount; col++) {
                    Object value = tableModel.getValueAt(row, col);
                    writer.print(value != null ? value.toString().replace(",", ";") : "");
                    writer.print(col < columnCount - 1 ? "," : "\n");
                }
            }

            JOptionPane.showMessageDialog(this, "Report exported successfully.",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not write the CSV file: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
