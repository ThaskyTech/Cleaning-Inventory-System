package cleanpro.desktopapp.service;

import cleanpro.desktopapp.dao.MaterialDAO;
import cleanpro.desktopapp.dao.StockIssuanceDAO;
import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.model.StockIssuance;
import cleanpro.desktopapp.model.StockIssuanceItem;
import cleanpro.desktopapp.service.exceptions.InsufficientStockException;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class StockIssuanceService {

    private final StockIssuanceDAO issuanceDAO = new StockIssuanceDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();

    private static final int NEXT_ID_PLACEHOLDER = 0; // TODO: replace once DAO auto-generates IDs

    public StockIssuance issueStock(int cleanerId, int issuedByUserId,
                                     List<StockIssuanceItem> requestedItems, String notes)
            throws InsufficientStockException, ValidationException {

        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new ValidationException("At least one material must be included in the issuance.");
        }

        //Valedating things beofre commiting
        for (StockIssuanceItem requested : requestedItems) {
            if (requested.getQuantityIssued() <= 0) {
                throw new ValidationException("Quantity issued must be greater than zero.");
            }

            Material material = materialDAO.getById(requested.getMaterialId());
            if (material == null) {
                throw new ValidationException("Material with ID " + requested.getMaterialId() + " does not exist.");
            }

            if (material.getCurrentQuantity() < requested.getQuantityIssued()) {
                throw new InsufficientStockException(
                        "Cannot issue " + requested.getQuantityIssued() + " units of '" + material.getMaterialName()
                                + "' — only " + material.getCurrentQuantity() + " available.");
            }
        }
         
        //Once all items have passed validation or something like that
        StockIssuance issuance = new StockIssuance(
                NEXT_ID_PLACEHOLDER, generateIssuanceNumber(), cleanerId, issuedByUserId,
                LocalDateTime.now(), StockIssuance.Status.COMPLETED, notes);

        for (StockIssuanceItem requested : requestedItems) {
            Material material = materialDAO.getById(requested.getMaterialId());

            StockIssuanceItem item = new StockIssuanceItem(
                    NEXT_ID_PLACEHOLDER, issuance.getIssuanceId(), material.getMaterialId(),
                    requested.getQuantityIssued(), material.getUnitPrice());
            issuance.addItem(item);

            material.setCurrentQuantity(material.getCurrentQuantity() - requested.getQuantityIssued());
            materialDAO.update(material);
        }

        issuanceDAO.insert(issuance);
        return issuance;
    }

    public List<StockIssuance> getAllIssuances() {
        return issuanceDAO.getAll();
    }

    public List<StockIssuance> getIssuanceHistoryForCleaner(int cleanerId) {
        return issuanceDAO.getByCleanerId(cleanerId);
    }


    private String generateIssuanceNumber() {
        return "ISS-" + System.currentTimeMillis();
    }
}
