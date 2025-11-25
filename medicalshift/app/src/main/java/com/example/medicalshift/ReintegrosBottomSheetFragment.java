package com.example.medicalshift;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class ReintegrosBottomSheetFragment extends BottomSheetDialogFragment implements AttachmentHelper.AttachmentListener {

    private EditText etUserName, etUserCBU;
    private TextView tvArchivoAdjunto1, tvArchivoAdjunto2, tvArchivoAdjunto3;
    private AttachmentHelper attachmentHelper;
    private Uri attachedFileUri1, attachedFileUri2, attachedFileUri3;
    private int currentAttachmentRequest;

    public static ReintegrosBottomSheetFragment newInstance(String userName, String userCbu) {
        ReintegrosBottomSheetFragment fragment = new ReintegrosBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);
        args.putString("USER_CBU", userCbu);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_reintegros, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attachmentHelper = new AttachmentHelper(this, this);

        // Encontrar Vistas
        etUserName = view.findViewById(R.id.etUserName);
        etUserCBU = view.findViewById(R.id.etUserCBU);
        Spinner spinnerTipo = view.findViewById(R.id.spinnerTipoReintegro);
        tvArchivoAdjunto1 = view.findViewById(R.id.tvArchivoAdjunto1);
        tvArchivoAdjunto2 = view.findViewById(R.id.tvArchivoAdjunto2);
        tvArchivoAdjunto3 = view.findViewById(R.id.tvArchivoAdjunto3);
        MaterialButton btnTomarFoto1 = view.findViewById(R.id.btnTomarFoto1);
        MaterialButton btnAdjuntar1 = view.findViewById(R.id.btnAdjuntarArchivo1);
        MaterialButton btnTomarFoto2 = view.findViewById(R.id.btnTomarFoto2);
        MaterialButton btnAdjuntar2 = view.findViewById(R.id.btnAdjuntarArchivo2);
        MaterialButton btnTomarFoto3 = view.findViewById(R.id.btnTomarFoto3);
        MaterialButton btnAdjuntar3 = view.findViewById(R.id.btnAdjuntarArchivo3);
        MaterialButton btnSolicitar = view.findViewById(R.id.btnSolicitar);

        // Cargar datos de usuario
        if (getArguments() != null) {
            etUserName.setText(getArguments().getString("USER_NAME"));
            etUserCBU.setText(getArguments().getString("USER_CBU"));
        }

        // Configurar Spinner
        String[] tipos = {"Reintegros Generales", "Reintegros Odontología", "Subsidio Fallecimiento y Sepelio"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_black, tipos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(spinnerAdapter);

        // Configurar Listeners
        btnTomarFoto1.setOnClickListener(v -> { currentAttachmentRequest = 1; attachmentHelper.dispatchTakePictureIntent(); });
        btnAdjuntar1.setOnClickListener(v -> { currentAttachmentRequest = 1; attachmentHelper.dispatchOpenDocumentIntent(); });
        btnTomarFoto2.setOnClickListener(v -> { currentAttachmentRequest = 2; attachmentHelper.dispatchTakePictureIntent(); });
        btnAdjuntar2.setOnClickListener(v -> { currentAttachmentRequest = 2; attachmentHelper.dispatchOpenDocumentIntent(); });
        btnTomarFoto3.setOnClickListener(v -> { currentAttachmentRequest = 3; attachmentHelper.dispatchTakePictureIntent(); });
        btnAdjuntar3.setOnClickListener(v -> { currentAttachmentRequest = 3; attachmentHelper.dispatchOpenDocumentIntent(); });

        btnSolicitar.setOnClickListener(v -> {
            String message = "Solicitud de Reintegro enviada";
            if (attachedFileUri1 != null || attachedFileUri2 != null || attachedFileUri3 != null) {
                message += " con archivos adjuntos";
            }
            Toast.makeText(getContext(), message + " (simulación)", Toast.LENGTH_LONG).show();
            dismiss();
        });
    }

    @Override
    public void onFileAttached(String fileName, Uri fileUri) {
        switch (currentAttachmentRequest) {
            case 1:
                tvArchivoAdjunto1.setText(fileName);
                tvArchivoAdjunto1.setVisibility(View.VISIBLE);
                this.attachedFileUri1 = fileUri;
                break;
            case 2:
                tvArchivoAdjunto2.setText(fileName);
                tvArchivoAdjunto2.setVisibility(View.VISIBLE);
                this.attachedFileUri2 = fileUri;
                break;
            case 3:
                tvArchivoAdjunto3.setText(fileName);
                tvArchivoAdjunto3.setVisibility(View.VISIBLE);
                this.attachedFileUri3 = fileUri;
                break;
        }
    }
}
