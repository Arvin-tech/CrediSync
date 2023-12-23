package com.example.credisync;

public class Users {
    private String LA_ID;
    private String LA_EmailAddress;
    // Add other fields along with getters and setters

    public Users() {
        // Default constructor required for Firestore
    }

    public Users(String LA_ID, String LA_EmailAddress /* Add other parameters */) {
        this.LA_ID = LA_ID;
        this.LA_EmailAddress = LA_EmailAddress;
        // Set other parameters in a similar way
    }

    public String getLA_ID() {
        return LA_ID;
    }

    public void setLA_ID(String LA_ID) {
        this.LA_ID = LA_ID;
    }

    public String getLA_EmailAddress() {
        return LA_EmailAddress;
    }

    public void setLA_EmailAddress(String LA_EmailAddress) {
        this.LA_EmailAddress = LA_EmailAddress;
    }

    // Add other getters and setters for additional fields
}
