package com.example.medicalshift;

import com.google.gson.annotations.SerializedName;

public class Gestion {
    @SerializedName("id")
    private String id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("userId")
    private String userId;

    // Constructor para compatibilidad con código existente
    public Gestion(String nombre, String fecha, String estado) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.estado = estado;
    }

    // Constructor vacío para Gson
    public Gestion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    // Mantener getTitulo() para compatibilidad con código existente
    public String getTitulo() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
