package cleanpro.desktopapp.model;

import java.math.BigDecimal;

/**
 * One row of the "Material Usage Report" - shows, per material, how much has
 * been issued (optionally within a date range), across how many separate
 * issuance events, and the total value of what was issued. This is the
 * report that answers "what are we using the most / spending the most on".
 */
public class MaterialUsageItem {

    private final String materialName;
    private final int totalQuantityIssued;
    private final int numberOfIssuances;
    private final BigDecimal totalValue;

    public MaterialUsageItem(String materialName, int totalQuantityIssued,
                              int numberOfIssuances, BigDecimal totalValue) {
        this.materialName = materialName;
        this.totalQuantityIssued = totalQuantityIssued;
        this.numberOfIssuances = numberOfIssuances;
        this.totalValue = totalValue;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getTotalQuantityIssued() {
        return totalQuantityIssued;
    }

    public int getNumberOfIssuances() {
        return numberOfIssuances;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }
}
