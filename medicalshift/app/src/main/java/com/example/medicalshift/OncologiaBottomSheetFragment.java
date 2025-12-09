package com.example.medicalshift;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medicalshift.utils.GestionHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class OncologiaBottomSheetFragment extends BottomSheetDialogFragment implements AttachmentHelper.AttachmentListener {

    private EditText etUserName;
    private EditText etFechaAplicacion;
    private TextView tvArchivoAdjunto;
    private AttachmentHelper attachmentHelper;
    private Uri attachedFileUri; // Para guardar la referencia al archivo
    private MaterialButton btnSolicitar;

    public static OncologiaBottomSheetFragment newInstance(String userName) {
        OncologiaBottomSheetFragment fragment = new OncologiaBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_oncologia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar el Helper
        attachmentHelper = new AttachmentHelper(this, this);

        // Encontrar Vistas
        etUserName = view.findViewById(R.id.etUserName);
        etFechaAplicacion = view.findViewById(R.id.etFechaAplicacion);
        tvArchivoAdjunto = view.findViewById(R.id.tvArchivoAdjunto);
        MaterialButton btnTomarFoto = view.findViewById(R.id.btnTomarFoto);
        MaterialButton btnAdjuntarArchivo = view.findViewById(R.id.btnAdjuntarArchivo);
        btnSolicitar = view.findViewById(R.id.btnSolicitar);

        // Cargar nombre de usuario
        if (getArguments() != null) {
            String userName = getArguments().getString("USER_NAME");
            etUserName.setText(userName);
        }

        // Configurar Listeners
        etFechaAplicacion.setOnClickListener(v -> showDatePickerDialog());
        btnTomarFoto.setOnClickListener(v -> attachmentHelper.dispatchTakePictureIntent());
        btnAdjuntarArchivo.setOnClickListener(v -> attachmentHelper.dispatchOpenDocumentIntent());

        btnSolicitar.setOnClickListener(v -> {
            android.util.Log.d("OncologiaBottomSheet", "Botón Solicitar presionado");
            
            // Validar campos
            String fechaAplicacion = etFechaAplicacion.getText().toString().trim();
            if (fechaAplicacion.isEmpty()) {
                etFechaAplicacion.setError("Seleccioná una fecha");
                android.util.Log.w("OncologiaBottomSheet", "Fecha no seleccionada");
                return;
            }

            android.util.Log.d("OncologiaBottomSheet", "Fecha seleccionada: " + fechaAplicacion);
            android.util.Log.d("OncologiaBottomSheet", "Archivo adjunto: " + (attachedFileUri != null ? attachedFileUri.toString() : "null"));

            // Crear gestión y subir archivo
            crearGestionYSubirArchivo("Oncología", fechaAplicacion);
        });
    }

    private void crearGestionYSubirArchivo(String nombreGestion, String fechaAplicacion) {
        android.util.Log.d("OncologiaBottomSheet", "Iniciando creación de gestión: " + nombreGestion);
        
        btnSolicitar.setEnabled(false);
        btnSolicitar.setText("Enviando...");

        GestionHelper.crearGestionYSubirArchivo(
            requireContext(),
            nombreGestion,
            fechaAplicacion,
            attachedFileUri,
            new GestionHelper.GestionCallback() {
                @Override
                public void onSuccess(String gestionId) {
                    android.util.Log.d("OncologiaBottomSheet", "Gestión creada exitosamente: " + gestionId);
                    btnSolicitar.setEnabled(true);
                    btnSolicitar.setText("Solicitar");
                    Toast.makeText(getContext(), "Solicitud enviada correctamente", Toast.LENGTH_LONG).show();
                    dismiss();
                }

                @Override
                public void onError(String message) {
                    android.util.Log.e("OncologiaBottomSheet", "Error: " + message);
                    btnSolicitar.setEnabled(true);
                    btnSolicitar.setText("Solicitar");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (v, year, month, day) -> {
                    String selectedDate = day + "/" + (month + 1) + "/" + year;
                    etFechaAplicacion.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onFileAttached(String fileName, Uri fileUri) {
        tvArchivoAdjunto.setText(fileName);
        tvArchivoAdjunto.setVisibility(View.VISIBLE);
        this.attachedFileUri = fileUri;
    }
}
