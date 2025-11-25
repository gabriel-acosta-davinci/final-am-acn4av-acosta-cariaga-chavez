package com.example.medicalshift;

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

public class CronicasBottomSheetFragment extends BottomSheetDialogFragment implements AttachmentHelper.AttachmentListener {

    private EditText etUserName;
    private TextView tvArchivoAdjunto;
    private AttachmentHelper attachmentHelper;
    private Uri attachedFileUri;

    public static CronicasBottomSheetFragment newInstance(String userName) {
        CronicasBottomSheetFragment fragment = new CronicasBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_cronicas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attachmentHelper = new AttachmentHelper(this, this);

        // Encontrar Vistas
        etUserName = view.findViewById(R.id.etUserName);
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
        btnTomarFoto.setOnClickListener(v -> attachmentHelper.dispatchTakePictureIntent());
        btnAdjuntarArchivo.setOnClickListener(v -> attachmentHelper.dispatchOpenDocumentIntent());

        btnSolicitar.setOnClickListener(v -> {
            String message = "Solicitud para Patologías Crónicas enviada";
            if (attachedFileUri != null) {
                message += " con el archivo adjunto: " + tvArchivoAdjunto.getText();
            }
            Toast.makeText(getContext(), message + " (simulación)", Toast.LENGTH_LONG).show();
            dismiss();
        });
    }

    @Override
    public void onFileAttached(String fileName, Uri fileUri) {
        tvArchivoAdjunto.setText(fileName);
        tvArchivoAdjunto.setVisibility(View.VISIBLE);
        this.attachedFileUri = fileUri;
    }
}
