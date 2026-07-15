package com.group8.cleaninginventory.model;

import java.time.LocalDateTime;
// Represents a single transaction of materials issued to a cleaner.
public class StockIssuance {

    private int issuanceID;
    private String issuanceNumber;
    private int cleanerID;
    private int userID;;
    private LocalDateTime issuanceDate;
    private String status;
    private String notes;

    public StockIssuance(int issuanceID, String issuanceNumber, int cleanerID, int userID, LocalDateTime issuanceDate,
                         String status, String notes) {
        this.issuanceID = issuanceID;
        this.issuanceNumber = issuanceNumber;
        this.cleanerID = cleanerID;
        this.userID = userID;
        this.issuanceDate = issuanceDate;
        this.status = status;
        this.notes = notes;
    }

    //Getters
    public int getUserID() {return userID;}
    public String getIssuanceNumber() {return issuanceNumber;}
    public int getCleanerID() {return cleanerID;}
    public LocalDateTime getIssuanceDate() {return issuanceDate;}
    public String getStatus() {return status;}
    public String getNotes() {return notes;}


}
