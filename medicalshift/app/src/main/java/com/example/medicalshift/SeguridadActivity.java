package com.example.medicalshift;

import android.os.Bundle;
import android.widget.TextView;
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
import java.util.Random;

public class SeguridadActivity extends AppCompatActivity {

    private User currentUser;
    private TextView tvToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguridad);

        String userId = getIntent().getStringExtra("LOGGED_IN_USER_ID");
        loadCurrentUser(userId);

        tvToken = findViewById(R.id.tvToken);
        TextInputEditText etEmailRecuperacion = findViewById(R.id.etEmailRecuperacion);
        MaterialButton btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        MaterialButton btnGenerarToken = findViewById(R.id.btnGenerarToken);

        updateUI();

        btnEnviarCorreo.setOnClickListener(v -> {
            String email = etEmailRecuperacion.getText().toString();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmailRecuperacion.setError("Ingresá un email válido");
            } else {
                Toast.makeText(this, "Correo de recuperación enviado a " + email, Toast.LENGTH_LONG).show();
            }
        });

        btnGenerarToken.setOnClickListener(v -> {
            String nuevoToken = String.format("%03d", new Random().nextInt(1000));
            tvToken.setText(nuevoToken);
            Toast.makeText(this, "Nuevo token generado: " + nuevoToken, Toast.LENGTH_SHORT).show();
        });
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

    private void updateUI() {
        if (currentUser != null) {
            tvToken.setText(currentUser.getToken());
        }
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
