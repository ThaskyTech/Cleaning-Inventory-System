package cleanpro.desktopapp.controller;

import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.Supplier;
import cleanpro.desktopapp.service.MaterialService;
import cleanpro.desktopapp.service.SupplierService;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.util.List;

public class MaterialController {

    private final MaterialService materialService = new MaterialService();
    private final SupplierService supplierService = new SupplierService();

    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }

    public List<Material> getLowStockMaterials() {
        return materialService.getLowStockMaterials();
    }

    public void addMaterial(Material material) throws ValidationException {
        materialService.addMaterial(material);
    }

    public void updateMaterial(Material material) throws ValidationException {
        materialService.updateMaterial(material);
    }

    public void deleteMaterial(int materialId) throws ValidationException {
        materialService.deleteMaterial(materialId);
    }

    public List<Material> search(String keyword) {
        return materialService.search(keyword);
    }

    // Needed to populate the supplier dropdown when adding/editing a material.
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }
}