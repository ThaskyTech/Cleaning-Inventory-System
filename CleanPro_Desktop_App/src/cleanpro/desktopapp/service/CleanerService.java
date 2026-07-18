package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.CleanerDAO;
import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.service.exceptions.ValidationException;
import cleanpro.desktopapp.util.ValidationUtil;

import java.util.List;

public class CleanerService {

    private final CleanerDAO cleanerDAO = new CleanerDAO();

    public List<Cleaner> getAllCleaners() {
        return cleanerDAO.getAll();
    }

    public void addCleaner(Cleaner cleaner) throws ValidationException {
        validateCleaner(cleaner);
        cleanerDAO.insert(cleaner);
    }

    public void updateCleaner(Cleaner cleaner) throws ValidationException {
        validateCleaner(cleaner);
        boolean updated = cleanerDAO.update(cleaner);
        if (!updated) {
            throw new ValidationException("Cleaner with ID " + cleaner.getCleanerId() + " does not exist.");
        }
    }

    public void deleteCleaner(int cleanerId) throws ValidationException {
        boolean deleted = cleanerDAO.delete(cleanerId);
        if (!deleted) {
            throw new ValidationException("Cleaner with ID " + cleanerId + " does not exist.");
        }
    }

    private void validateCleaner(Cleaner cleaner) throws ValidationException {
        if (cleaner == null) {
            throw new ValidationException("Cleaner cannot be null.");
        }
        if (ValidationUtil.isNullOrBlank(cleaner.getFirstName()) || ValidationUtil.isNullOrBlank(cleaner.getLastName())) {
            throw new ValidationException("Cleaner first name and last name are required.");
        }
        if (ValidationUtil.isNullOrBlank(cleaner.getCleanerNumber())) {
            throw new ValidationException("Cleaner number is required.");
        }
        if (!ValidationUtil.isNullOrBlank(cleaner.getEmail()) && !ValidationUtil.isValidEmail(cleaner.getEmail())) {
            throw new ValidationException("Cleaner email address is not valid.");
        }
    }
}
