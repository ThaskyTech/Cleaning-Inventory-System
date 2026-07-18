package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.SupplierDAO;
import cleanpro.desktopapp.model.Supplier;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.ValidationUtil;

import java.util.List;

public class SupplierService {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    public List<Supplier> getAllSuppliers() {
        return supplierDAO.getAll();
    }

    public void addSupplier(Supplier supplier) throws ValidationException {
        validateSupplier(supplier);
        supplierDAO.insert(supplier);
    }

    public void updateSupplier(Supplier supplier) throws ValidationException {
        validateSupplier(supplier);
        boolean updated = supplierDAO.update(supplier);
        if (!updated) {
            throw new ValidationException("Supplier with ID " + supplier.getSupplierId() + " does not exist.");
        }
    }

    public void deleteSupplier(int supplierId) throws ValidationException {
        boolean deleted = supplierDAO.delete(supplierId);
        if (!deleted) {
            throw new ValidationException("Supplier with ID " + supplierId + " does not exist.");
        }
    }

    private void validateSupplier(Supplier supplier) throws ValidationException {
        if (supplier == null) {
            throw new ValidationException("Supplier cannot be null.");
        }
        if (ValidationUtil.isNullOrBlank(supplier.getSupplierName())) {
            throw new ValidationException("Supplier name is required.");
        }
        if (ValidationUtil.isNullOrBlank(supplier.getSupplierCode())) {
            throw new ValidationException("Supplier code is required.");
        }
        if (!ValidationUtil.isNullOrBlank(supplier.getEmail()) && !ValidationUtil.isValidEmail(supplier.getEmail())) {
            throw new ValidationException("Supplier email address is not valid.");
        }
    }
}
