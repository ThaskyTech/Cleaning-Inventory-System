package com.group8.cleaninginventory.model;

// Represents a cleaning material/stock item, including quantity available and reorder level.
public class Material {

    private int materialID;
    private String materialName;
    private String description;
    //Remove private String category;
    private int quantityAvailable;
    private int reorderLevel;
    private int supplierID;

    //Unit * unit price = overall price;

    public Material(int materialID, String name, String description, String category, int quantityAvailable,
                    int reorderLevel, int supplierID) {
        this.materialID = materialID;
        this.materialName = name;
        this.description = description;
        setquantityAvailable(quantityAvailable);
        this.reorderLevel = reorderLevel;
        this.supplierID = supplierID;
    }

    //Getters and Setters

    public int getMaterialID(){return materialID;}

    public String getMaterialName(){return materialName;}
    public void setMaterialName(String name){this.materialName = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public int getQuantityAvailable(){return quantityAvailable;}
    public void setquantityAvailable(int quantityAvailable){this.quantityAvailable = quantityAvailable;}

    public int getReorderLevel(){return reorderLevel;}
    public void setReorderLevel(int reorderLevel){this.reorderLevel = reorderLevel;}

    public int getSupplierId() { return supplierID; }
    public void setSupplierId(int supplierId) { this.supplierID = supplierId; }

    public boolean isLowStock(){return quantityAvailable <= reorderLevel;}

}
