package com.example.medicalshift;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MisDatosActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        loadCurrentUser();
        updateUI();
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
