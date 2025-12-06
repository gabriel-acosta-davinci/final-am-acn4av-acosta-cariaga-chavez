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

        String userId = getIntent().getStringExtra("LOGGED_IN_USER_ID");
        loadCurrentUser(userId);

        RecyclerView recyclerPagos = findViewById(R.id.recyclerResumenPagos);
        recyclerPagos.setLayoutManager(new LinearLayoutManager(this));

        List<Factura> listaFacturas = loadAllFacturas();
        FacturaAdapter adapter = new FacturaAdapter(listaFacturas);
        recyclerPagos.setAdapter(adapter);

        if (listaFacturas.isEmpty()) {
            Toast.makeText(this, "No tenés facturas en tu historial.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadCurrentUser(String userId) {
        if (userId == null) return;
        try {
            String json = loadJSONFromAsset("users.json");
            JSONArray usersArray = new JSONArray(json);
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                if (userObject.getString("Número de documento").equals(userId)) {
                    currentUser = new User(userObject);
                    break;
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Factura> loadAllFacturas() {
        List<Factura> todasLasFacturas = new ArrayList<>();
        if (currentUser == null) return todasLasFacturas;

        try {
            String json = loadJSONFromAsset("facturas.json");
            JSONArray facturasArray = new JSONArray(json);
            for (int i = 0; i < facturasArray.length(); i++) {
                JSONObject facturaObject = facturasArray.getJSONObject(i);
                if (facturaObject.getString("userId").equals(currentUser.getNumeroDocumento())) {
                    todasLasFacturas.add(new Factura(facturaObject));
                }
            }
        } catch (IOException | JSONException e) { // CORREGIDO: Manejar la excepción
            e.printStackTrace();
        }
        
        return todasLasFacturas;
    }

    private String loadJSONFromAsset(String fileName) throws IOException {
        InputStream is = getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
