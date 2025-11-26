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

public class PagarFacturaActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar_factura);

        loadCurrentUser();

        RecyclerView recyclerFacturas = findViewById(R.id.recyclerFacturasPendientes);
        recyclerFacturas.setLayoutManager(new LinearLayoutManager(this));

        List<Factura> listaFacturas = loadFacturasPendientes();
        // Ahora usará el FacturaAdapter externo
        FacturaAdapter adapter = new FacturaAdapter(listaFacturas);
        recyclerFacturas.setAdapter(adapter);

        if (listaFacturas.isEmpty()) {
            Toast.makeText(this, "No tenés facturas pendientes de pago.", Toast.LENGTH_LONG).show();
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

    private List<Factura> loadFacturasPendientes() {
        List<Factura> facturasPendientes = new ArrayList<>();
        if (currentUser == null) return facturasPendientes;

        String json = loadJSONFromAsset("facturas.json");
        if (json != null) {
            try {
                JSONArray facturasArray = new JSONArray(json);
                for (int i = 0; i < facturasArray.length(); i++) {
                    JSONObject facturaObject = facturasArray.getJSONObject(i);
                    if (facturaObject.getString("userId").equals(currentUser.getNumeroDocumento()) &&
                        facturaObject.getString("estado").equals("Pendiente")) {
                        facturasPendientes.add(new Factura(facturaObject));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return facturasPendientes;
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
