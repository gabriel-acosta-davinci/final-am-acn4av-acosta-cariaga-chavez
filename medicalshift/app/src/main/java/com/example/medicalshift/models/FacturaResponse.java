package com.example.medicalshift.models;

import com.example.medicalshift.Factura;

import java.util.List;

public class FacturaResponse {
    private String message;
    private Integer count;
    private List<Factura> facturas;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }
}



