package cleanpro.desktopapp.service;

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
                    "Notes cannot exceed 255 characters."
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
}