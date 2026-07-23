package cleanpro.desktopapp.controller;

import cleanpro.desktopapp.model.StockIssuance;
import cleanpro.desktopapp.model.StockIssuanceItem;
import cleanpro.desktopapp.service.StockIssuanceService;
import cleanpro.desktopapp.service.exceptions.InsufficientStockException;
import cleanpro.desktopapp.service.exceptions.ValidationException;

import java.util.List;

public class StockIssuanceController {

    private final StockIssuanceService issuanceService = new StockIssuanceService();

    public StockIssuance issueStock(int cleanerId, int issuedByUserId,
                                     List<StockIssuanceItem> items, String notes)
            throws InsufficientStockException, ValidationException {
        return issuanceService.issueStock(cleanerId, issuedByUserId, items, notes);
    }

    public List<StockIssuance> getAllIssuances() {
        return issuanceService.getAllIssuances();
    }

    public List<StockIssuance> getIssuanceHistoryForCleaner(int cleanerId) {
        return issuanceService.getIssuanceHistoryForCleaner(cleanerId);
    }
}