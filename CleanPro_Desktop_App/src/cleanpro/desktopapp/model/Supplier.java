package com.group8.cleaninginventory.model;

// Represents a supplier who provides cleaning materials to the university.
public class Supplier {

    private int supplierID;
    private String supplierName;
    private String contactPerson;
    private String contactNumber;
    private String email;
    private String address;

    public Supplier(int ID, String name, String contactPerson, String contactNumber, String email, String address){
        this.supplierID = ID;
        this.supplierName = name;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    //Getters and Setters

    public int getSupplierID(){return supplierID;}

    public String getSupplierName(){return supplierName;}
    public void setSupplierName(String name){this.supplierName = name;}

    public String getContactPerson(){return contactPerson;}
    public void setContactPerson(String person){this.contactPerson = person;}

    public String getContactNumber(){return contactNumber;}
    public void setContactNumber(String number){this.contactNumber = number;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}


}
