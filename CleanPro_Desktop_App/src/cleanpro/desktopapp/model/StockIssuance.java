package cleanpro.desktopapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockIssuance{
    
    public enum Status { PENDING, COMPLETED, CANCELLED }
    private int issuanceId;
    private String issuanceNumber;
    private int cleanerId;
    private int issuedByUserId;
    private LocalDateTime issuanceDate;
    private Status status;
    private String notes;
    private List<StockIssuanceItem> items = new ArrayList<>();

    public StockIssuance(int issuanceId, String issuanceNumber, int cleanerId, int issuedByUserId,
                          LocalDateTime issuanceDate, Status status, String notes) {
        this.issuanceId = issuanceId;
        this.issuanceNumber = issuanceNumber;
        this.cleanerId = cleanerId;
        this.issuedByUserId = issuedByUserId;
        this.issuanceDate = issuanceDate;
        this.status = status;
        this.notes = notes;
    }
    
    //Getters and Setters
    public int getIssuanceId() { return issuanceId; }

    public String getIssuanceNumber() { return issuanceNumber; }
    public void setIssuanceNumber(String issuanceNumber) { this.issuanceNumber = issuanceNumber; }

    public int getCleanerId() { return cleanerId; }

    public int getIssuedByUserId() { return issuedByUserId; }

    public LocalDateTime getIssuanceDate() { return issuanceDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<StockIssuanceItem> getItems() { return items; }
    public void setItems(List<StockIssuanceItem> items) { this.items = items; }
    public void addItem(StockIssuanceItem item) { this.items.add(item); }
}