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
                    cleaner.getEmploymentDate().toLocalDate();

            if (employmentDate.isAfter(
                    LocalDate.now())) {

                throw new ValidationException(
                        "Employment date cannot be in the future."
                );
            }
        }
    }
}