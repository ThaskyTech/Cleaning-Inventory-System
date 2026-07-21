package cleanpro.desktopapp.service;
import cleanpro.desktopapp.dao.MaterialDAO;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.List;

public class MaterialService {
    private final MaterialDAO materialDAO = new MaterialDAO();

    // GET ALL MATERIALS

    public List<Material> getAllMaterials() {
        return materialDAO.getAll();
    }

    // GET LOW STOCK MATERIALS

    public List<Material> getLowStockMaterials() {
        return materialDAO.getAll()
                .stream()
                .filter(Material::isLowStock)
                .toList();
    }

    // GET OVERSTOCKED MATERIALS

    public List<Material> getOverstockedMaterials() {
        return materialDAO.getAll()
                .stream()
                .filter(Material::isOverMaximumStock)
                .toList();
    }

    // ADD MATERIAL

    public void addMaterial(Material material)
            throws ValidationException {
        validateMaterial(material);
        materialDAO.insert(material);
    }

    // UPDATE MATERIAL

    public void updateMaterial(Material material)
            throws ValidationException {
        validateMaterial(material);
        boolean updated =
                materialDAO.update(material);

        if (!updated) {
            throw new ValidationException(
                    "Material with ID "
                    + material.getMaterialId()
                    + " does not exist."
            );
        }
    }

    // DELETE MATERIAL

    public void deleteMaterial(int materialId)
            throws ValidationException {
        if (materialId <= 0) {
            throw new ValidationException(
                    "Invalid material ID."
            );
        }

        boolean deleted =
                materialDAO.delete(materialId);

        if (!deleted) {
            throw new ValidationException(
                    "Material with ID "
                    + materialId
                    + " does not exist."
            );
        }
    }

    // SEARCH MATERIALS

    public List<Material> search(String keyword) {
        if (keyword == null
                || keyword.trim().isEmpty()) {
            return materialDAO.getAll();
        }

        return materialDAO.searchByName(
                keyword.trim()
        );
    }

    // MATERIAL VALIDATION

    private void validateMaterial(Material material)
            throws ValidationException {
        if (material == null) {
            throw new ValidationException(
                    "Material cannot be null."
            );
        }

        // Material name validation

        if (ValidationUtil.isNullOrBlank(
                material.getMaterialName())) {
            throw new ValidationException(
                    "Material name is required."
            );
        }

        if (material.getMaterialName().length() > 100) {
            throw new ValidationException(
                    "Material name cannot exceed 100 characters."
            );
        }

        // Description validation

        if (ValidationUtil.isNullOrBlank(
                material.getDescription())) {

            throw new ValidationException(
                    "Material description is required."
            );
        }

        // Supplier validation

        if (material.getSupplierId() <= 0) {
            throw new ValidationException(
                    "Supplier must be selected."
            );
        }

        // Current quantity validation

        if (!ValidationUtil.isNonNegative(
                material.getCurrentQuantity())) {
            throw new ValidationException(
                    "Current quantity cannot be negative."
            );
        }

        // Reorder level validation

        if (!ValidationUtil.isNonNegative(
                material.getReorderLevel())) {
            throw new ValidationException(
                    "Reorder level cannot be negative."
            );
        }

        // Maximum stock validation

        if (material.getMaximumStockLevel() <= 0) {
            throw new ValidationException(
                    "Maximum stock level must be greater than zero."
            );
        }

        if (material.getMaximumStockLevel()
                < material.getReorderLevel()) {
            throw new ValidationException(
                    "Maximum stock level cannot be lower than reorder level."
            );
        }

        // Unit price validation

       if (material.getUnitPrice() == null) {
            throw new ValidationException(
                    "Unit price is required."
            );
        }

        if (material.getUnitPrice()
                .compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(
                    "Unit price must be greater than zero."
            );
        }

        // Unit validation

        if (ValidationUtil.isNullOrBlank(
                material.getUnit())) {
            throw new ValidationException(
                    "Unit is required."
            );
        }
    }
}