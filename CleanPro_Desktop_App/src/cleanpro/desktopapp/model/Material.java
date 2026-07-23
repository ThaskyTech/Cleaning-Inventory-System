package cleanpro.desktopapp.model;

import java.math.BigDecimal;

public class Material {

    private int materialId;
    private String materialName;
    private String materialDescription;
    private int supplierId;
    private int currentQuantity;
    private int reorderLevel;
    private int maximumStockLevel;
    private BigDecimal unitPrice;
    private String unit;

    public Material(int materialId, String materialName, String materialDescription, int supplierId,
                     int currentQuantity, int reorderLevel, int maximumStockLevel,
                     BigDecimal unitPrice, String unit) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.materialDescription = materialDescription;
        this.supplierId = supplierId;
        setCurrentQuantity(currentQuantity); 
        this.reorderLevel = reorderLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.unitPrice = unitPrice;
        this.unit = unit;
    }

    
    public boolean isLowStock(){return currentQuantity <= reorderLevel;}
    
    public boolean isOverMaximumStock(){return currentQuantity > maximumStockLevel;}
    
    
    //Getters and Setters
    public int getMaterialId(){return materialId;}
    
    public String getMaterialName(){return materialName;}
    public void setMaterialName(String materialName){this.materialName = materialName;}
    
    public String getMaterialDescription(){return materialDescription;}
    public void setMaterialDescription(String materialDescription){this.materialDescription = materialDescription;}
    
    public int getSupplierId(){return supplierId;}
    public void setSupplierId(int supplierId){this.supplierId = supplierId;}
    
    public int getCurrentQuantity(){return currentQuantity;}
    public void setCurrentQuantity(int currentQuantity){
        if(currentQuantity < 0){throw new IllegalArgumentException("Stcok Quantity cannot be a negative!");}
        this.currentQuantity = currentQuantity;
    }
    
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public int getMaximumStockLevel() { return maximumStockLevel; }
    public void setMaximumStockLevel(int maximumStockLevel) { this.maximumStockLevel = maximumStockLevel; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
