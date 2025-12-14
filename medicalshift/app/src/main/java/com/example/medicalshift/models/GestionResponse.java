package com.example.medicalshift.models;

import com.example.medicalshift.Gestion;

import java.util.List;

public class GestionResponse {
    private String message;
    private Integer count;
    private List<Gestion> gestiones;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public List<Gestion> getGestiones() { return gestiones; }
    public void setGestiones(List<Gestion> gestiones) { this.gestiones = gestiones; }
}




