package com.example.medicalshift;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MisDatosActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        String userId = getIntent().getStringExtra("LOGGED_IN_USER_ID");
        loadCurrentUser(userId);
        updateUI();

        Button btnEditar = findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(v -> {
            if (currentUser != null) {
                EditarDatosBottomSheetFragment bottomSheet = EditarDatosBottomSheetFragment.newInstance(currentUser);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
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
            ((TextView) findViewById(R.id.dataNombreCompleto)).setText(currentUser.getNombreCompleto());
            ((TextView) findViewById(R.id.dataNumeroDocumento)).setText(currentUser.getNumeroDocumento());
            ((TextView) findViewById(R.id.dataFechaNacimiento)).setText(currentUser.getFechaNacimiento());
            ((TextView) findViewById(R.id.dataPlan)).setText(currentUser.getPlan());
            ((TextView) findViewById(R.id.dataNumeroAsociado)).setText(currentUser.getNumeroAsociado());
            ((TextView) findViewById(R.id.dataEstadoCivil)).setText(currentUser.getEstadoCivil());
            ((TextView) findViewById(R.id.dataEmail)).setText(currentUser.getEmail());
            ((TextView) findViewById(R.id.dataTelefono)).setText(currentUser.getTelefono());
            ((TextView) findViewById(R.id.dataDomicilio)).setText(currentUser.getDomicilio());
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
