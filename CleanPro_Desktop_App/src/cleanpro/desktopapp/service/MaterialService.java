package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.MaterialDAO;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;

public class MaterialService {

    private final MaterialDAO materialDAO = new MaterialDAO();

    public List<Material> getAllMaterials() {
        return materialDAO.getAll();
    }

    
    public List<Material> getLowStockMaterials() {
        return materialDAO.getAll().stream()
                .filter(Material::isLowStock)
                .toList(); 
    }

    public List<Material> getOverstockedMaterials() {
        return materialDAO.getAll().stream()
                .filter(Material::isOverMaximumStock)
                .toList();
    }

    public void addMaterial(Material material) throws ValidationException {
        validateMaterial(material);
        materialDAO.insert(material);
    }

    public void updateMaterial(Material material) throws ValidationException {
        validateMaterial(material);
        boolean updated = materialDAO.update(material);
        if (!updated) {
            throw new ValidationException("Material with ID " + material.getMaterialId() + " does not exist.");
        }
    }

    public void deleteMaterial(int materialId) throws ValidationException {
        boolean deleted = materialDAO.delete(materialId);
        if (!deleted) {
            throw new ValidationException("Material with ID " + materialId + " does not exist.");
        }
    }

    public List<Material> search(String keyword) {
        return materialDAO.searchByName(keyword);
    }

    
    private void validateMaterial(Material material) throws ValidationException {
        if (material == null) {
            throw new ValidationException("Material cannot be null.");
        }
        if (ValidationUtil.isNullOrBlank(material.getMaterialName())) {
            throw new ValidationException("Material name is required.");
        }
        if (!ValidationUtil.isNonNegative(material.getCurrentQuantity())) {
            
            throw new ValidationException("Current quantity cannot be negative.");
        }
        if (!ValidationUtil.isNonNegative(material.getReorderLevel())) {
            throw new ValidationException("Reorder level cannot be negative.");
        }
        if (material.getMaximumStockLevel() < material.getReorderLevel()) {
            throw new ValidationException("Maximum stock level cannot be lower than reorder level.");
        }
        if (material.getUnitPrice() == null || material.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Unit price cannot be negative.");
        }
    }
}