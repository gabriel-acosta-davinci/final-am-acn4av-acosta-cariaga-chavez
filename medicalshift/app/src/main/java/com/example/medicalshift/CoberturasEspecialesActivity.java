package com.example.medicalshift;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CoberturasEspecialesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coberturas_especiales);

        TextInputEditText etDniSolicitante = findViewById(R.id.etDniSolicitante);
        Spinner spinnerTipoCobertura = findViewById(R.id.spinnerTipoCobertura);
        MaterialButton btnIniciarWhatsapp = findViewById(R.id.btnIniciarWhatsapp);

        // Configurar Spinner
        String[] tipos = {"Cobertura por Discapacidad", "Cobertura Materno Infantil"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_black, tipos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoCobertura.setAdapter(spinnerAdapter);

        btnIniciarWhatsapp.setOnClickListener(v -> {
            String dni = etDniSolicitante.getText().toString();
            String tipoCobertura = spinnerTipoCobertura.getSelectedItem().toString();

            if (dni.isEmpty()) {
                etDniSolicitante.setError("Por favor, ingresá un DNI");
                return;
            }

            try {
                String telefono = "+5491122334455"; // Número de teléfono de ejemplo
                String mensaje = "Hola, quiero iniciar una solicitud de " + tipoCobertura + " para el DNI: " + dni;
                
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + telefono + "&text=" + mensaje));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir WhatsApp. ¿Está instalado?", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
