package com.group8.cleaninginventory.model;

import java.time.LocalDate;

// Represents a cleaner staff member who receives issued materials; extends Person.
public class Cleaner extends Person {
    private int cleanerID;
    private String cleanerNumber;
    private LocalDate employmentDate;
    private String department;


    public Cleaner(int ID,String firstName, String lastName, String contactNumber, String department, String email,
                   String cleanerNumber, LocalDate employmentDate){
        super(firstName, lastName, contactNumber, email);
        this.cleanerID = ID;
        this.cleanerNumber = cleanerNumber;
        this.employmentDate = employmentDate;
        this.department = department;
    }

    @Override
    public String toString(){
        return "Cleaner #" + cleanerID + ": " + getFirstName() + " " + getLastName()
                + (department != null ? " (" + department + ")" : "");
    }

    //Getters and setters

    public int getCleanerID(){return cleanerID;}

    public String getCleanerNumber(){return cleanerNumber;}
    public void setCleanerNumber(String cleanerNumber){this.cleanerNumber = cleanerNumber;}

    public LocalDate getEmploymentDate(){return employmentDate;}

    public String getDepartment(){return department;}
    public void setDepartment(String department){this.department = department;}


}
