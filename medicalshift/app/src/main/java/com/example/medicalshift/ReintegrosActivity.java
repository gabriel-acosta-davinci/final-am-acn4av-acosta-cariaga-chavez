package com.example.medicalshift;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ReintegrosActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reintegros);

        // Encontrar Vistas
        LinearLayout editContainer = findViewById(R.id.editCbuContainer);
        TextInputEditText etCurrentCbu = findViewById(R.id.etCurrentCbu);
        MaterialButton btnCambiarCuenta = findViewById(R.id.btnCambiarCuenta);
        TextInputEditText etNuevoCbu = findViewById(R.id.etNuevoCbu);
        MaterialButton btnEnviarSolicitud = findViewById(R.id.btnEnviarSolicitud);

        loadCurrentUser();

        // Mostrar CBU Actual
        if (currentUser != null) {
            etCurrentCbu.setText(currentUser.getCbu());
        }

        // Lógica de cambio de vista
        btnCambiarCuenta.setOnClickListener(v -> {
            btnCambiarCuenta.setVisibility(View.GONE);
            editContainer.setVisibility(View.VISIBLE);
        });

        // Lógica de envío de solicitud
        btnEnviarSolicitud.setOnClickListener(v -> {
            String nuevoCbu = etNuevoCbu.getText().toString();
            if (nuevoCbu.isEmpty() || nuevoCbu.length() < 22) {
                etNuevoCbu.setError("Ingresá un CBU válido");
            } else {
                Toast.makeText(this, "Solicitud de cambio de CBU enviada (simulación)", Toast.LENGTH_LONG).show();
                finish(); // Cierra la actividad
            }
        });
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
