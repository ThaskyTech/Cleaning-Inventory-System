public class exceptions {
}
/*package cleanpro.desktopapp.service;

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
}*/

/*package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.MaterialDAO;
import cleanpro.desktopapp.dao.StockIssuanceDAO;

import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.StockIssuance;
import cleanpro.desktopapp.model.StockIssuanceItem;

import cleanpro.desktopapp.service.exceptions.InsufficientStockException;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockIssuanceService {

    private final StockIssuanceDAO issuanceDAO = new StockIssuanceDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();

    private static final int NEXT_ID_PLACEHOLDER = 0;

    public StockIssuance issueStock(
            int cleanerId,
            int issuedByUserId,
            List<StockIssuanceItem> requestedItems,
            String notes)
            throws InsufficientStockException, ValidationException {

        validateIssuanceInput(
                cleanerId,
                issuedByUserId,
                requestedItems,
                notes
        );

        Set<Integer> materialIds = new HashSet<>();

        for (StockIssuanceItem requested : requestedItems) {

            if (!materialIds.add(requested.getMaterialId())) {
                throw new ValidationException(
                        "Duplicate material selected in issuance."
                );
            }

            if (requested.getQuantityIssued() <= 0) {
                throw new ValidationException(
                        "Quantity issued must be greater than zero."
                );
            }

            Material material =
                    materialDAO.getById(
                            requested.getMaterialId()
                    );

            if (material == null) {
                throw new ValidationException(
                        "Material with ID "
                                + requested.getMaterialId()
                                + " does not exist."
                );
            }

            if (material.getCurrentQuantity()
                    < requested.getQuantityIssued()) {

                throw new InsufficientStockException(
                        "Cannot issue "
                                + requested.getQuantityIssued()
                                + " units of "
                                + material.getMaterialName()
                                + ". Available stock: "
                                + material.getCurrentQuantity()
                );
            }
        }

        String issuanceNumber = generateIssuanceNumber();

        StockIssuance issuance =
                new StockIssuance(
                        NEXT_ID_PLACEHOLDER,
                        issuanceNumber,
                        cleanerId,
                        issuedByUserId,
                        LocalDateTime.now(),
                        StockIssuance.Status.COMPLETED,
                        notes
                );

        for (StockIssuanceItem requested : requestedItems) {

            Material material =
                    materialDAO.getById(
                            requested.getMaterialId()
                    );

            StockIssuanceItem item =
                    new StockIssuanceItem(
                            NEXT_ID_PLACEHOLDER,
                            issuance.getIssuanceId(),
                            material.getMaterialId(),
                            requested.getQuantityIssued(),
                            material.getUnitPrice()
                    );

            issuance.addItem(item);

            int newQuantity =
                    material.getCurrentQuantity()
                            - requested.getQuantityIssued();

            if (newQuantity < 0) {
                throw new InsufficientStockException(
                        "Stock cannot become negative."
                );
            }

            material.setCurrentQuantity(newQuantity);

            materialDAO.update(material);
        }

        issuanceDAO.insert(issuance);

        return issuance;
    }

    private void validateIssuanceInput(
            int cleanerId,
            int issuedByUserId,
            List<StockIssuanceItem> requestedItems,
            String notes)
            throws ValidationException {

        if (cleanerId <= 0) {
            throw new ValidationException(
                    "Invalid cleaner selected."
            );
        }

        if (issuedByUserId <= 0) {
            throw new ValidationException(
                    "Invalid user selected."
            );
        }

        if (requestedItems == null
                || requestedItems.isEmpty()) {

            throw new ValidationException(
                    "At least one material must be included."
            );
        }

        if (notes != null
                && notes.length() > 255) {

            throw new ValidationException(
                    "Notes cannot exceed 255 characters.";
            );
        }
    }

    public List<StockIssuance> getAllIssuances() {
        return issuanceDAO.getAll();
    }

    public List<StockIssuance> getIssuanceHistoryForCleaner(
            int cleanerId) {
        if (cleanerId <= 0) {
            return List.of();
        }
        return issuanceDAO.getByCleanerId(cleanerId);
    }

    private String generateIssuanceNumber() {
        return "ISS-" + System.currentTimeMillis();
    }
}*/

/*package cleanpro.desktopapp.service;
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
}*/

/*
package cleanpro.desktopapp.service;
import cleanpro.desktopapp.dao.RoleDAO;
import cleanpro.desktopapp.dao.UserDAO;
import cleanpro.desktopapp.model.Role;
import cleanpro.desktopapp.model.User;
import cleanpro.desktopapp.service.exceptions.DuplicateEntryException;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.PasswordUtil;
import cleanpro.desktopapp.util.ValidationUtil;
import java.time.LocalDateTime;
import java.util.List;

public class LoginService {
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private static final int ID_PLACEHOLDER = 0;

    // LOGIN

    public User login(String username, String password)
            throws ValidationException {
        if (ValidationUtil.isNullOrBlank(username)
                || ValidationUtil.isNullOrBlank(password)) {
            throw new ValidationException(
                    "Username and password are required."
            );
        }

        User user =
                userDAO.getByUsername(
                        username.trim()
                );

        if (user == null) {
           throw new ValidationException(
                    "Invalid username or password."
            );
        }

        if (!user.isActive()) {
            throw new ValidationException(
                    "This account has been deactivated."
            );
        }

        if (!PasswordUtil.verifyPassword(
                password,
                user.getPasswordHash())) {
            throw new ValidationException(
                    "Invalid username or password."
            );
        }

        userDAO.updateLastLogin(
                user.getUserId(),
                LocalDateTime.now()
        );

        return user;
    }

    // REGISTER USER

    public User register(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String username,
            String password,
            int roleId
            )

            throws DuplicateEntryException, ValidationException {
             validateRegistration(
                firstName,
                lastName,
                email,
                phoneNumber,
                username,
                password,
                roleId
        );

        if (userDAO.usernameExists(username)) {
            throw new DuplicateEntryException(
                    "Username already exists."
            );
        }

        if (userDAO.emailExists(email)) {
            throw new DuplicateEntryException(
                    "Email already exists."
            );
        }

        String passwordHash =
                PasswordUtil.hashPassword(password);

        User newUser =
                new User(
                        ID_PLACEHOLDER,
                        firstName,
                        lastName,
                        email,
                        phoneNumber,
                        username,
                        passwordHash,
                        roleId,
                        true,
                        null
                );

        userDAO.insert(newUser);
        return newUser;
    }

    // REGISTRATION VALIDATION

    private void validateRegistration(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String username,
            String password,
            int roleId
            )

            throws ValidationException {

        if (ValidationUtil.isNullOrBlank(firstName)
                || ValidationUtil.isNullOrBlank(lastName)) {
            throw new ValidationException(
                    "First name and last name are required."
            );
        }

        if (!firstName.matches("[a-zA-Z ]+")
                || !lastName.matches("[a-zA-Z ]+")) {

            throw new ValidationException(
                    "Names can only contain letters."
            );
        }

        if (ValidationUtil.isNullOrBlank(username)) {
            throw new ValidationException(
                    "Username is required."
            );
        }

        if (!username.matches("[a-zA-Z0-9_]{4,20}")) {
            throw new ValidationException(
                    "Username must be 4-20 characters and contain only letters, numbers, and underscores."
            );
        }

        if (ValidationUtil.isNullOrBlank(password)) {
            throw new ValidationException(
                    "Password is required."
            );
        }

        if (password.length() < 8) {
            throw new ValidationException(
                    "Password must be at least 8 characters long."
            );
        }

        if (!password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*[0-9].*")) {
            throw new ValidationException(
                    "Password must contain uppercase, lowercase, and a number."
           );
        }

        if (ValidationUtil.isNullOrBlank(email)
                || !ValidationUtil.isValidEmail(email)) {
            throw new ValidationException(
                    "Email address is not valid."
            );
        }

        if (!ValidationUtil.isNullOrBlank(phoneNumber)
                && !phoneNumber.matches("\\d{10,15}")) {
            throw new ValidationException(
                    "Phone number must contain 10 to 15 digits."
            );
        }

        if (roleId <= 0
                || roleDAO.getById(roleId) == null) {
            throw new ValidationException(
                    "Selected role does not exist."
            );
        }
    }

    // GET ROLES

    public List<Role> getAllRoles() {
        return roleDAO.getAll();
    }
}*/

/*
package cleanpro.desktopapp.service;
import cleanpro.desktopapp.dao.CleanerDAO;
import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.ValidationUtil;
import java.time.LocalDate;
import java.util.List;

public class CleanerService {
    private final CleanerDAO cleanerDAO = new CleanerDAO();

    // GET ALL CLEANERS

    public List<Cleaner> getAllCleaners() {
        return cleanerDAO.getAll();
    }

    // ADD CLEANER

    public void addCleaner(Cleaner cleaner)
            throws ValidationException {
        validateCleaner(cleaner);
        cleanerDAO.insert(cleaner);
    }

    // UPDATE CLEANER

    public void updateCleaner(Cleaner cleaner)
            throws ValidationException {
        validateCleaner(cleaner);

        boolean updated =
                cleanerDAO.update(cleaner);

        if (!updated) {
            throw new ValidationException(

                    "Cleaner with ID "
                    + cleaner.getCleanerId()
                    + " does not exist."
            );
        }
    }

    // DELETE CLEANER

    public void deleteCleaner(int cleanerId)
            throws ValidationException {

        if (cleanerId <= 0) {
            throw new ValidationException(
                    "Invalid cleaner ID."
            );
        }

        boolean deleted =
                cleanerDAO.delete(cleanerId);

        if (!deleted) {

            throw new ValidationException(

                    "Cleaner with ID "
                    + cleanerId
                    + " does not exist."
            );
        }
    }

    // CLEANER VALIDATION

    private void validateCleaner(Cleaner cleaner)
            throws ValidationException {
        if (cleaner == null) {

            throw new ValidationException(
                    "Cleaner cannot be null."
            );
        }

        // Cleaner number validation

        if (ValidationUtil.isNullOrBlank(
                cleaner.getCleanerNumber())) {

            throw new ValidationException(
                    "Cleaner number is required."
            );
        }

        if (!cleaner.getCleanerNumber()
                .matches("[A-Za-z0-9-]+")) {

            throw new ValidationException(
                    "Cleaner number can only contain letters, numbers, and hyphens."
            );
        }

        // First name validation

        if (ValidationUtil.isNullOrBlank(
                cleaner.getFirstName())) {

            throw new ValidationException(
                    "Cleaner first name is required."
            );
        }

        if (!cleaner.getFirstName()
                .matches("[a-zA-Z ]+")) {

            throw new ValidationException(
                    "First name can only contain letters."
            );
        }

        // Last name validation

        if (ValidationUtil.isNullOrBlank(
                cleaner.getLastName())) {

            throw new ValidationException(
                    "Cleaner last name is required."
            );
        }

        if (!cleaner.getLastName()
                .matches("[a-zA-Z ]+")) {

            throw new ValidationException(
                    "Last name can only contain letters."
            );
        }

        // Phone validation

        if (!ValidationUtil.isNullOrBlank(
                cleaner.getPhone())) {

            if (!cleaner.getPhone()
                    .matches("\\d{10,15}")) {

                throw new ValidationException(
                        "Phone number must contain 10 to 15 digits."
                );
            }
        }

        // Email validation

        if (!ValidationUtil.isNullOrBlank(
                cleaner.getEmail())) {

            if (!ValidationUtil.isValidEmail(
                    cleaner.getEmail())) {

                throw new ValidationException(
                        "Cleaner email address is not valid."
                );
            }
        }

        // Employment date validation

        if (cleaner.getEmploymentDate() != null) {
            LocalDate employmentDate =
                    cleaner.getEmploymentDate()
                            .toLocalDate();

            if (employmentDate.isAfter(
                    LocalDate.now())) {

                throw new ValidationException(
                        "Employment date cannot be in the future."
                );
            }
        }
    }
}*/
