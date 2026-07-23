package cleanpro.desktopapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One row of the "Issuance History Report" — a full record of a stock
 * issuance event, with the line items aggregated (total items, total value)
 * so the report shows one row per issuance rather than one row per material.
 *
 * NOTE: uses LocalDateTime rather than java.sql.Timestamp (Member 2's original type),
 * to match StockIssuance.getIssuanceDate().
 */
public class IssuanceHistoryItem {

    private final String issuanceNumber;
    private final String cleanerFullName;
    private final String issuedByUsername;
    private final LocalDateTime issuanceDate;
    private final String status;
    private final String notes;
    private final int totalItemsIssued;
    private final BigDecimal totalValue;

    public IssuanceHistoryItem(String issuanceNumber, String cleanerFullName, String issuedByUsername,
                                LocalDateTime issuanceDate, String status, String notes,
                                int totalItemsIssued, BigDecimal totalValue) {
        this.issuanceNumber = issuanceNumber;
        this.cleanerFullName = cleanerFullName;
        this.issuedByUsername = issuedByUsername;
        this.issuanceDate = issuanceDate;
        this.status = status;
        this.notes = notes;
        this.totalItemsIssued = totalItemsIssued;
        this.totalValue = totalValue;
    }

    public String getIssuanceNumber() {
        return issuanceNumber;
    }

    public String getCleanerFullName() {
        return cleanerFullName;
    }

    public String getIssuedByUsername() {
        return issuedByUsername;
    }

    public LocalDateTime getIssuanceDate() {
        return issuanceDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public int getTotalItemsIssued() {
        return totalItemsIssued;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }
}
