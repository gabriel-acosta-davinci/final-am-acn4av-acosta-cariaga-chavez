package com.example.medicalshift.models;

import com.example.medicalshift.Gestion;

public class CreateGestionResponse {
    private String message;
    private Gestion gestion;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Gestion getGestion() { return gestion; }
    public void setGestion(Gestion gestion) { this.gestion = gestion; }
}



