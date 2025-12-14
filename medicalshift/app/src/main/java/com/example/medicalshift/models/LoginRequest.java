package com.example.medicalshift.models;

public class LoginRequest {
    private String identifier;
    private String password;
    private String identifierType;

    public LoginRequest(String identifier, String password, String identifierType) {
        this.identifier = identifier;
        this.password = password;
        this.identifierType = identifierType;
    }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getIdentifierType() { return identifierType; }
    public void setIdentifierType(String identifierType) { this.identifierType = identifierType; }
}




