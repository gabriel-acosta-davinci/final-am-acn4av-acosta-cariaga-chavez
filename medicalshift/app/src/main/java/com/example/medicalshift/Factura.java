package com.example.medicalshift;

import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class Factura {
    @SerializedName("periodo")
    private String periodo;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("monto")
    private Double monto;
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("userId")
    private String userId;

    // Constructor para compatibilidad con código existente
    public Factura(String periodo, String estado, double monto) {
        this.periodo = periodo;
        this.estado = estado;
        this.monto = monto;
    }

    // Constructor para JSONObject (compatibilidad con código existente)
    public Factura(JSONObject object) throws JSONException {
        this.periodo = object.getString("periodo");
        this.estado = object.getString("estado");
        this.monto = object.getDouble("monto");
        this.id = object.optString("id", null);
        this.userId = object.optString("userId", null);
    }

    // Constructor vacío para Gson
    public Factura() {}

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public double getMonto() { return monto != null ? monto : 0.0; }
    public void setMonto(Double monto) { this.monto = monto; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
