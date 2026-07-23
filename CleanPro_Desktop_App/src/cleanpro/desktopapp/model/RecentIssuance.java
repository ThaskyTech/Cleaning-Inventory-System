package cleanpro.desktopapp.model;

import java.time.LocalDateTime;

/**
 * One row of "recent activity" shown on the Dashboard — a short summary of a
 * stock issuance, without every column from StockIssuance (that full detail
 * belongs in the Issuance History report, see IssuanceHistoryItem).
 *
 * NOTE: uses LocalDateTime rather than java.sql.Timestamp (Member 2's original type)
 * to match StockIssuance.getIssuanceDate(), since this project's DAOs are currently
 * in-memory rather than raw JDBC.
 */
public class RecentIssuance {

    private final String issuanceNumber;
    private final String cleanerFullName;
    private final LocalDateTime issuanceDate;
    private final String status;

    public RecentIssuance(String issuanceNumber, String cleanerFullName,
                           LocalDateTime issuanceDate, String status) {
        this.issuanceNumber = issuanceNumber;
        this.cleanerFullName = cleanerFullName;
        this.issuanceDate = issuanceDate;
        this.status = status;
    }

    public String getIssuanceNumber() {
        return issuanceNumber;
    }

    public String getCleanerFullName() {
        return cleanerFullName;
    }

    public LocalDateTime getIssuanceDate() {
        return issuanceDate;
    }

    public String getStatus() {
        return status;
    }
}
