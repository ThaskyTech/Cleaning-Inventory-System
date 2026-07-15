package com.group8.cleaninginventory.model;

// Abstract superclass representing a person; base for entities like Cleaner (demonstrates Inheritance).
public abstract class Person {

    private String firstName;
    private String lastName;
    private String contactNumber;
    private String email;

    public Person(String firstName, String lastName, String contactNumber, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    //Getters and Setters

    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){this.firstName = firstName;}

    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}

    public String getContactNumber(){return contactNumber;}
    public void setContactNumber(String contactNumber){this.contactNumber = contactNumber;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

}
