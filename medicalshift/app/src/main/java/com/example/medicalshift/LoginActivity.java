package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etDocumento;
    private TextInputEditText etContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etDocumento = findViewById(R.id.etDocumento);
        etContraseña = findViewById(R.id.etContraseña);
        MaterialButton btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> {
            String documento = etDocumento.getText().toString();
            String contraseña = etContraseña.getText().toString();

            if (documento.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = validarUsuario(documento, contraseña);
            if (userId != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("LOGGED_IN_USER_ID", userId);
                startActivity(intent);
                finish(); // Cierra LoginActivity para que no se pueda volver atrás
            } else {
                Toast.makeText(this, "Documento o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String validarUsuario(String documento, String contraseña) {
        try {
            String json = loadJSONFromAsset("users.json");
            JSONArray usersArray = new JSONArray(json);
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("Número de documento").equals(documento) && user.getString("contraseña").equals(contraseña)) {
                    return user.getString("Número de documento"); // Devuelve el DNI como ID de usuario
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra el usuario
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
