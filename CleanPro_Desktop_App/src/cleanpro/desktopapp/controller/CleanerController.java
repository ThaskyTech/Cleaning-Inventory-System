package cleanpro.desktopapp.controller;

import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.service.CleanerService;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.util.List;

public class CleanerController {

    private final CleanerService cleanerService = new CleanerService();

    public List<Cleaner> getAllCleaners() {
        return cleanerService.getAllCleaners();
    }

    public void addCleaner(Cleaner cleaner) throws ValidationException {
        cleanerService.addCleaner(cleaner);
    }

    public void updateCleaner(Cleaner cleaner) throws ValidationException {
        cleanerService.updateCleaner(cleaner);
    }

    public void deleteCleaner(int cleanerId) throws ValidationException {
        cleanerService.deleteCleaner(cleanerId);
    }
}