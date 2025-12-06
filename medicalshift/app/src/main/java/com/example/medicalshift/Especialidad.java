package com.example.medicalshift;

public class Especialidad {
    private final String nombre;
    private final String descripcion;
    private final int iconoResId;

    public Especialidad(String nombre, String descripcion, int iconoResId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoResId = iconoResId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getIconoResId() {
        return iconoResId;
    }
}
