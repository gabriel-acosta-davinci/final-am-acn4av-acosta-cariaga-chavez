package com.example.medicalshift;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        TextView tvNumeroEmergencias = findViewById(R.id.tvNumeroEmergencias);
        TextView tvNumeroAtencion = findViewById(R.id.tvNumeroAtencion);
        TextView tvEmailContacto = findViewById(R.id.tvEmailContacto);
        TextView tvWhatsapp = findViewById(R.id.tvWhatsapp);

        tvNumeroEmergencias.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tvNumeroEmergencias.getText().toString()));
            startActivity(intent);
        });

        tvNumeroAtencion.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tvNumeroAtencion.getText().toString()));
            startActivity(intent);
        });

        tvEmailContacto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + tvEmailContacto.getText().toString()));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se encontró una aplicación de correo.", Toast.LENGTH_SHORT).show();
            }
        });

        tvWhatsapp.setOnClickListener(v -> {
            try {
                String telefono = "+5491122334455"; // Número de teléfono de ejemplo
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + telefono));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir WhatsApp. ¿Está instalado?", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
