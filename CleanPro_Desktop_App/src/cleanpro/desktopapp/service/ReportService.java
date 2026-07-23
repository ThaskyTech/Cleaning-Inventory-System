package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.CleanerDAO;
import cleanpro.desktopapp.dao.MaterialDAO;
import cleanpro.desktopapp.dao.StockIssuanceDAO;
import cleanpro.desktopapp.dao.SupplierDAO;
import cleanpro.desktopapp.dao.UserDAO;
import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.model.InventoryReportItem;
import cleanpro.desktopapp.model.IssuanceHistoryItem;
import cleanpro.desktopapp.model.LowStockReportItem;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.MaterialUsageItem;
import cleanpro.desktopapp.model.StockIssuance;
import cleanpro.desktopapp.model.StockIssuanceItem;
import cleanpro.desktopapp.model.Supplier;
import cleanpro.desktopapp.model.User;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic (service) layer for all four required reports:
 * Inventory, Low-Stock, Issuance History, Material Usage.
 *
 * ADAPTED from Member 2's original version: her implementation ran SQL JOINs directly
 * against the database in a dedicated ReportDAO. This version gets the same result by
 * composing calls to the project's actual DAOs (MaterialDAO, SupplierDAO, CleanerDAO,
 * UserDAO, StockIssuanceDAO) and joining the results in Java instead of SQL — each of
 * those DAOs already does its own real JDBC query. The report definitions themselves
 * (what counts as low stock, how usage is aggregated, etc.) are unchanged from her design.
 */
public class ReportService {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final CleanerDAO cleanerDAO = new CleanerDAO();
    private final UserDAO userDAO = new UserDAO();
    private final StockIssuanceDAO issuanceDAO = new StockIssuanceDAO();

    public List<InventoryReportItem> generateInventoryReport() {
        Map<Integer, String> supplierNamesById = supplierNamesById();

        List<InventoryReportItem> results = new ArrayList<>();
        for (Material m : materialDAO.getAll()) {
            BigDecimal stockValue = m.getUnitPrice() != null
                    ? m.getUnitPrice().multiply(BigDecimal.valueOf(m.getCurrentQuantity()))
                    : BigDecimal.ZERO;

            results.add(new InventoryReportItem(
                    m.getMaterialName(),
                    supplierNamesById.getOrDefault(m.getSupplierId(), "Unknown Supplier"),
                    m.getCurrentQuantity(),
                    m.getReorderLevel(),
                    m.getMaximumStockLevel(),
                    m.getUnitPrice(),
                    m.getUnit(),
                    stockValue
            ));
        }
        return results;
    }

    public List<LowStockReportItem> generateLowStockReport() {
        Map<Integer, String> supplierNamesById = supplierNamesById();

        List<LowStockReportItem> results = new ArrayList<>();
        for (Material m : materialDAO.getAll()) {
            if (m.isLowStock()) {
                results.add(new LowStockReportItem(
                        m.getMaterialName(),
                        supplierNamesById.getOrDefault(m.getSupplierId(), "Unknown Supplier"),
                        m.getCurrentQuantity(),
                        m.getReorderLevel()
                ));
            }
        }
        results.sort((a, b) -> Integer.compare(b.getShortfall(), a.getShortfall()));
        return results;
    }

    public List<IssuanceHistoryItem> generateIssuanceHistoryReport(LocalDate fromDate, LocalDate toDate)
            throws ValidationException {
        validateDateRange(fromDate, toDate);

        Map<Integer, Cleaner> cleanersById = cleanersById();
        Map<Integer, String> usernamesById = usernamesById();

        List<IssuanceHistoryItem> results = new ArrayList<>();
        for (StockIssuance issuance : issuanceDAO.getAll()) {
            if (!withinRange(issuance.getIssuanceDate(), fromDate, toDate)) {
                continue;
            }

            int totalItems = 0;
            BigDecimal totalValue = BigDecimal.ZERO;
            for (StockIssuanceItem item : issuance.getItems()) {
                totalItems += item.getQuantityIssued();
                if (item.getUnitPriceAtIssue() != null) {
                    totalValue = totalValue.add(
                            item.getUnitPriceAtIssue().multiply(BigDecimal.valueOf(item.getQuantityIssued())));
                }
            }

            Cleaner cleaner = cleanersById.get(issuance.getCleanerId());
            String cleanerFullName = cleaner != null
                    ? cleaner.getFirstName() + " " + cleaner.getLastName()
                    : "Unknown Cleaner";

            results.add(new IssuanceHistoryItem(
                    issuance.getIssuanceNumber(),
                    cleanerFullName,
                    usernamesById.getOrDefault(issuance.getIssuedByUserId(), "Unknown User"),
                    issuance.getIssuanceDate(),
                    issuance.getStatus().name(),
                    issuance.getNotes(),
                    totalItems,
                    totalValue
            ));
        }
        results.sort((a, b) -> b.getIssuanceDate().compareTo(a.getIssuanceDate()));
        return results;
    }

    public List<MaterialUsageItem> generateMaterialUsageReport(LocalDate fromDate, LocalDate toDate)
            throws ValidationException {
        validateDateRange(fromDate, toDate);

        Map<Integer, String> materialNamesById = new HashMap<>();
        for (Material m : materialDAO.getAll()) {
            materialNamesById.put(m.getMaterialId(), m.getMaterialName());
        }

        Map<Integer, Integer> quantityByMaterial = new HashMap<>();
        Map<Integer, Integer> issuanceCountByMaterial = new HashMap<>();
        Map<Integer, BigDecimal> valueByMaterial = new HashMap<>();

        for (StockIssuance issuance : issuanceDAO.getAll()) {
            if (!withinRange(issuance.getIssuanceDate(), fromDate, toDate)) {
                continue;
            }
            for (StockIssuanceItem item : issuance.getItems()) {
                int materialId = item.getMaterialId();
                quantityByMaterial.merge(materialId, item.getQuantityIssued(), Integer::sum);
                issuanceCountByMaterial.merge(materialId, 1, Integer::sum);

                BigDecimal lineValue = item.getUnitPriceAtIssue() != null
                        ? item.getUnitPriceAtIssue().multiply(BigDecimal.valueOf(item.getQuantityIssued()))
                        : BigDecimal.ZERO;
                valueByMaterial.merge(materialId, lineValue, BigDecimal::add);
            }
        }

        List<MaterialUsageItem> results = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : quantityByMaterial.entrySet()) {
            int materialId = entry.getKey();
            results.add(new MaterialUsageItem(
                    materialNamesById.getOrDefault(materialId, "Unknown Material"),
                    entry.getValue(),
                    issuanceCountByMaterial.getOrDefault(materialId, 0),
                    valueByMaterial.getOrDefault(materialId, BigDecimal.ZERO)
            ));
        }
        results.sort((a, b) -> Integer.compare(b.getTotalQuantityIssued(), a.getTotalQuantityIssued()));
        return results;
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) throws ValidationException {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ValidationException("The 'from' date cannot be after the 'to' date.");
        }
    }

    private boolean withinRange(LocalDateTime issuanceDate, LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && issuanceDate.toLocalDate().isBefore(fromDate)) {
            return false;
        }
        if (toDate != null && issuanceDate.toLocalDate().isAfter(toDate)) {
            return false;
        }
        return true;
    }

    private Map<Integer, String> supplierNamesById() {
        Map<Integer, String> map = new HashMap<>();
        for (Supplier s : supplierDAO.getAll()) {
            map.put(s.getSupplierId(), s.getSupplierName());
        }
        return map;
    }

    private Map<Integer, Cleaner> cleanersById() {
        Map<Integer, Cleaner> map = new HashMap<>();
        for (Cleaner c : cleanerDAO.getAll()) {
            map.put(c.getCleanerId(), c);
        }
        return map;
    }

    private Map<Integer, String> usernamesById() {
        Map<Integer, String> map = new HashMap<>();
        for (User u : userDAO.getAll()) {
            map.put(u.getUserId(), u.getUsername());
        }
        return map;
    }
}