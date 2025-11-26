package com.example.medicalshift;

import org.json.JSONException;
import org.json.JSONObject;

public class Factura {
    final String periodo, estado;
    final double monto;

    public Factura(JSONObject object) throws JSONException {
        this.periodo = object.getString("periodo");
        this.estado = object.getString("estado");
        this.monto = object.getDouble("monto");
    }
}
