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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class OncologiaBottomSheetFragment extends BottomSheetDialogFragment implements AttachmentHelper.AttachmentListener {

    private EditText etUserName;
    private EditText etFechaAplicacion;
    private TextView tvArchivoAdjunto;
    private AttachmentHelper attachmentHelper;
    private Uri attachedFileUri; // Para guardar la referencia al archivo

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
        MaterialButton btnSolicitar = view.findViewById(R.id.btnSolicitar);

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
            String message = "Solicitud enviada";
            if (attachedFileUri != null) {
                message += " con el archivo adjunto: " + tvArchivoAdjunto.getText();
            }
            Toast.makeText(getContext(), message + " (simulación)", Toast.LENGTH_LONG).show();
            dismiss(); 
        });
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
