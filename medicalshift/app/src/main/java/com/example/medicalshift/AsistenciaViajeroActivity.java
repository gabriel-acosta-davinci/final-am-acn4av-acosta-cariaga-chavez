package com.example.medicalshift;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AsistenciaViajeroActivity extends AppCompatActivity {

    private boolean isAsistenciaActiva = false; 
    private MaterialButton btnActivarAsistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_viajero);

        btnActivarAsistencia = findViewById(R.id.btnActivarAsistencia);

        btnActivarAsistencia.setOnClickListener(v -> {
            if (isAsistenciaActiva) {
                mostrarDialogoDeConfirmacion("desactivar");
            } else {
                mostrarDialogoDeConfirmacion("activar");
            }
        });
    }

    private void mostrarDialogoDeConfirmacion(String accion) {
        String mensaje = accion.equals("activar") ? "¿Estás seguro de que deseas activar la asistencia al viajero?" : "¿Estás seguro de que deseas desactivar la asistencia al viajero?";
        
        new AlertDialog.Builder(this)
            .setMessage(mensaje)
            .setPositiveButton("Sí", (dialog, which) -> {
                if (accion.equals("activar")) {
                    isAsistenciaActiva = true;
                    btnActivarAsistencia.setText("Desactivar");
                } else {
                    isAsistenciaActiva = false;
                    btnActivarAsistencia.setText("Activar");
                }
            })
            .setNegativeButton("No", null)
            .show();
    }
}
