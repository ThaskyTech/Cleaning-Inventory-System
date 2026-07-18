package cleanpro.desktopapp.model;

import java.time.LocalDate;

import java.time.LocalDate;

// Represents a cleaner staff member who receives issued materials; extends Person.
public class Cleaner{
    private int cleanerId;
    private String cleanerNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private LocalDate employmentDate;
    private boolean active;

    public Cleaner(int cleanerId, String cleanerNumber, String firstName, String lastName,
                   String phoneNumber, String email, LocalDate employmentDate, boolean active) {
        this.cleanerId = cleanerId;
        this.cleanerNumber = cleanerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.employmentDate = employmentDate;
        this.active = active;
    }

    //Getters and Setters
    
    public int getCleanerId() { return cleanerId; }

    public String getCleanerNumber() { return cleanerNumber; }
    public void setCleanerNumber(String cleanerNumber) { this.cleanerNumber = cleanerNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDate employmentDate) { this.employmentDate = employmentDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }


}
