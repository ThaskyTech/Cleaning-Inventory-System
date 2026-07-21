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
            throw new ValidationException(
                    "Supplier with ID " + supplier.getSupplierId() + " does not exist."
            );
        }
    }

    public void deleteSupplier(int supplierId) throws ValidationException {

        if (supplierId <= 0) {
            throw new ValidationException("Invalid supplier ID.");
        }

        boolean deleted = supplierDAO.delete(supplierId);

        if (!deleted) {
            throw new ValidationException(
                    "Supplier with ID " + supplierId + " does not exist."
            );
        }
    }

    private void validateSupplier(Supplier supplier) throws ValidationException {

        if (supplier == null) {
            throw new ValidationException("Supplier cannot be null.");
        }

        if (ValidationUtil.isNullOrBlank(supplier.getSupplierCode())) {
            throw new ValidationException("Supplier code is required.");
        }

        if (supplier.getSupplierCode().length() > 20) {
            throw new ValidationException("Supplier code cannot exceed 20 characters.");
        }

        if (ValidationUtil.isNullOrBlank(supplier.getSupplierName())) {
            throw new ValidationException("Supplier name is required.");
        }

        if (!supplier.getSupplierName().matches("[a-zA-Z ]+")) {
            throw new ValidationException("Supplier name can only contain letters.");
        }

        if (!ValidationUtil.isNullOrBlank(supplier.getContactPerson())
                && !supplier.getContactPerson().matches("[a-zA-Z ]+")) {

            throw new ValidationException(
                    "Contact person can only contain letters."
            );
        }

        if (!ValidationUtil.isNullOrBlank(supplier.getPhoneNumber())
                && !supplier.getPhoneNumber().matches("\\d{10,15}")) {

            throw new ValidationException(
                    "Phone number must contain 10 to 15 digits."
            );
        }

        if (!ValidationUtil.isNullOrBlank(supplier.getEmail())
                && !ValidationUtil.isValidEmail(supplier.getEmail())) {

            throw new ValidationException(
                    "Supplier email address is not valid."
            );
        }

        if (ValidationUtil.isNullOrBlank(supplier.getAddress())) {
            throw new ValidationException("Supplier address is required.");
        }

        if (ValidationUtil.isNullOrBlank(supplier.getCity())) {
            throw new ValidationException("Supplier city is required.");
        }

        if (ValidationUtil.isNullOrBlank(supplier.getProvince())) {
            throw new ValidationException("Supplier province is required.");
        }

        if (!ValidationUtil.isNullOrBlank(supplier.getPostalCode())
                && !supplier.getPostalCode().matches("[A-Za-z0-9 ]{3,10}")) {

            throw new ValidationException("Invalid postal code.");
        }
    }
}