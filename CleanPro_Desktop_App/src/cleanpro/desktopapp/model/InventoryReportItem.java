package cleanpro.desktopapp.model;

import java.math.BigDecimal;

/**
 * One row of the "Inventory Report" - a full snapshot of a single material,
 * including the calculated stock value (current_quantity * unit_price).
 */
public class InventoryReportItem {

    private final String materialName;
    private final String supplierName;
    private final int currentQuantity;
    private final int reorderLevel;
    private final int maximumStockLevel;
    private final BigDecimal unitPrice;
    private final String unit;
    private final BigDecimal stockValue;

    public InventoryReportItem(String materialName, String supplierName, int currentQuantity,
                                int reorderLevel, int maximumStockLevel, BigDecimal unitPrice,
                                String unit, BigDecimal stockValue) {
        this.materialName = materialName;
        this.supplierName = supplierName;
        this.currentQuantity = currentQuantity;
        this.reorderLevel = reorderLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.stockValue = stockValue;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public int getMaximumStockLevel() {
        return maximumStockLevel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }
}
