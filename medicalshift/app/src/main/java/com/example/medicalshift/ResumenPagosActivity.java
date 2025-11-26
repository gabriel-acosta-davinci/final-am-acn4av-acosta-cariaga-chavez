package com.example.medicalshift;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ResumenPagosActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pagos);

        loadCurrentUser();

        RecyclerView recyclerPagos = findViewById(R.id.recyclerResumenPagos);
        recyclerPagos.setLayoutManager(new LinearLayoutManager(this));

        List<Factura> listaFacturas = loadAllFacturas();
        FacturaAdapter adapter = new FacturaAdapter(listaFacturas);
        recyclerPagos.setAdapter(adapter);

        if (listaFacturas.isEmpty()) {
            Toast.makeText(this, "No tenés facturas en tu historial.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadCurrentUser() {
        String json = loadJSONFromAsset("users.json");
        if (json != null) {
            try {
                JSONArray usersArray = new JSONArray(json);
                if (usersArray.length() > 0) {
                    currentUser = new User(usersArray.getJSONObject(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Factura> loadAllFacturas() {
        List<Factura> todasLasFacturas = new ArrayList<>();
        if (currentUser == null) return todasLasFacturas;

        String json = loadJSONFromAsset("facturas.json");
        if (json != null) {
            try {
                JSONArray facturasArray = new JSONArray(json);
                for (int i = 0; i < facturasArray.length(); i++) {
                    JSONObject facturaObject = facturasArray.getJSONObject(i);
                    if (facturaObject.getString("userId").equals(currentUser.getNumeroDocumento())) {
                        todasLasFacturas.add(new Factura(facturaObject));
                    }
                }
                // Aquí podrías añadir una lógica de ordenamiento si fuese necesario
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return todasLasFacturas;
    }

    private String loadJSONFromAsset(String fileName) {
        try (InputStream is = getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
