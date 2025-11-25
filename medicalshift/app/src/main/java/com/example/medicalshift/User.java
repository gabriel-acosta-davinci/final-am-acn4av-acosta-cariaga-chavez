package com.example.medicalshift;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private final String nombreCompleto;
    private final String numeroDocumento;
    private final String telefono;
    private final String email;
    private final String plan;
    private final String numeroAsociado;
    private final String cbu;
    private final String fechaNacimiento;
    private final String estadoCivil;
    private final String domicilioCompleto;

    // Campos del domicilio
    private final String calle;
    private final String numero;
    private final String piso;
    private final String dpto;
    private final String localidad;
    private final String provincia;

    public User(JSONObject object) throws JSONException {
        this.nombreCompleto = object.getString("Nombre Completo");
        this.numeroDocumento = object.getString("Número de documento");
        this.telefono = object.getString("Número de teléfono");
        this.email = object.getString("Email");
        this.plan = object.getString("Plan");
        this.numeroAsociado = object.getString("Número de asociado");
        this.cbu = object.getString("CBU");
        this.fechaNacimiento = object.getString("Fecha de nacimiento");
        this.estadoCivil = object.getString("Estado Civil");

        JSONObject dom = object.getJSONObject("Domicilio de Residencia");
        this.calle = dom.getString("Calle");
        this.numero = String.valueOf(dom.getInt("Número"));
        this.piso = dom.optString("Piso", "");
        this.dpto = dom.optString("Dpto", "");
        this.localidad = dom.getString("Localidad");
        this.provincia = dom.getString("Provincia");

        // Construir el domicilio completo
        StringBuilder sb = new StringBuilder();
        sb.append(this.calle).append(" ").append(this.numero);
        if (!this.piso.isEmpty()) {
            sb.append(", Piso ").append(this.piso);
        }
        if (!this.dpto.isEmpty()) {
            sb.append(", Dpto ").append(this.dpto);
        }
        sb.append(", ").append(this.localidad).append(", ").append(this.provincia);
        this.domicilioCompleto = sb.toString();
    }

    // Getters
    public String getNombreCompleto() { return nombreCompleto; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getPlan() { return plan; }
    public String getNumeroAsociado() { return numeroAsociado; }
    public String getCbu() { return cbu; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getEstadoCivil() { return estadoCivil; }
    public String getDomicilio() { return domicilioCompleto; }
    public String getLocalidad() { return localidad; }
    public String getCalle() { return calle; }
    public String getNumero() { return numero; }
    public String getPiso() { return piso; }
    public String getDpto() { return dpto; }
    public String getProvincia() { return provincia; }
}
