package com.example.medicalshift.models;

public class GestionRequest {
    private String estado;
    private String nombre;
    private String fecha;
    private String userId;

    public GestionRequest(String estado, String nombre, String fecha, String userId) {
        this.estado = estado;
        this.nombre = nombre;
        this.fecha = fecha;
        this.userId = userId;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}



