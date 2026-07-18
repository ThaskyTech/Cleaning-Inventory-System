package cleanpro.desktopapp.model;

import java.math.BigDecimal;

public class StockIssuanceItem {

    private int issuanceItemId;
    private int issuanceId;
    private int materialId;
    private int quantityIssued;
    private BigDecimal unitPriceAtIssue;

    public StockIssuanceItem(int issuanceItemId, int issuanceId, int materialId,
                              int quantityIssued, BigDecimal unitPriceAtIssue) {
        this.issuanceItemId = issuanceItemId;
        this.issuanceId = issuanceId;
        this.materialId = materialId;
        this.quantityIssued = quantityIssued;
        this.unitPriceAtIssue = unitPriceAtIssue;
    }

    
    //Getters and Setters
    public int getIssuanceItemId() { return issuanceItemId; }

    public int getIssuanceId() { return issuanceId; }
    public void setIssuanceId(int issuanceId) { this.issuanceId = issuanceId; }

    public int getMaterialId() { return materialId; }

    public int getQuantityIssued() { return quantityIssued; }

    public BigDecimal getUnitPriceAtIssue() { return unitPriceAtIssue; }
    public void setUnitPriceAtIssue(BigDecimal unitPriceAtIssue) { this.unitPriceAtIssue = unitPriceAtIssue; }


}
