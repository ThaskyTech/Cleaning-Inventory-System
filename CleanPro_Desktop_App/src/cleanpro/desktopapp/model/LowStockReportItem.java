package cleanpro.desktopapp.model;

/**
 * One row of the "Low-Stock Report" - only materials where
 * current_quantity <= reorder_level. The "shortfall" tells staff exactly how
 * many units they are below the reorder point, which is what makes this
 * report actionable rather than just a filtered inventory list.
 */
public class LowStockReportItem {

    private final String materialName;
    private final String supplierName;
    private final int currentQuantity;
    private final int reorderLevel;
    private final int shortfall;

    public LowStockReportItem(String materialName, String supplierName,
                               int currentQuantity, int reorderLevel) {
        this.materialName = materialName;
        this.supplierName = supplierName;
        this.currentQuantity = currentQuantity;
        this.reorderLevel = reorderLevel;
        this.shortfall = Math.max(0, reorderLevel - currentQuantity);
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

    public int getShortfall() {
        return shortfall;
    }
}
