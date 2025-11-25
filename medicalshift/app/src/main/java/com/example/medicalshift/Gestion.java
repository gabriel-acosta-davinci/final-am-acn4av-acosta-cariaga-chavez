package com.example.medicalshift;

public class Gestion {
    private String titulo;
    private String fecha;
    private String estado;

    public Gestion(String titulo, String fecha, String estado) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }
}
