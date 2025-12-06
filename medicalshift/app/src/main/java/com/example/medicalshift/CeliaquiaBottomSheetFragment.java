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

public class CeliaquiaBottomSheetFragment extends BottomSheetDialogFragment implements AttachmentHelper.AttachmentListener {

    private EditText etUserName;
    private TextView tvArchivoAdjunto1, tvArchivoAdjunto2;
    private AttachmentHelper attachmentHelper;
    private Uri attachedFileUri1, attachedFileUri2;
    private int currentAttachmentRequest;

    public static CeliaquiaBottomSheetFragment newInstance(String userName) {
        CeliaquiaBottomSheetFragment fragment = new CeliaquiaBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_celiaquia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attachmentHelper = new AttachmentHelper(this, this);

        etUserName = view.findViewById(R.id.etUserName);
        tvArchivoAdjunto1 = view.findViewById(R.id.tvArchivoAdjunto1);
        tvArchivoAdjunto2 = view.findViewById(R.id.tvArchivoAdjunto2);
        MaterialButton btnTomarFoto1 = view.findViewById(R.id.btnTomarFoto1);
        MaterialButton btnAdjuntarArchivo1 = view.findViewById(R.id.btnAdjuntarArchivo1);
        MaterialButton btnTomarFoto2 = view.findViewById(R.id.btnTomarFoto2);
        MaterialButton btnAdjuntarArchivo2 = view.findViewById(R.id.btnAdjuntarArchivo2);
        MaterialButton btnSolicitar = view.findViewById(R.id.btnSolicitar);

        if (getArguments() != null) {
            etUserName.setText(getArguments().getString("USER_NAME"));
        }

        btnTomarFoto1.setOnClickListener(v -> { currentAttachmentRequest = 1; attachmentHelper.dispatchTakePictureIntent(); });
        btnAdjuntarArchivo1.setOnClickListener(v -> { currentAttachmentRequest = 1; attachmentHelper.dispatchOpenDocumentIntent(); });
        btnTomarFoto2.setOnClickListener(v -> { currentAttachmentRequest = 2; attachmentHelper.dispatchTakePictureIntent(); });
        btnAdjuntarArchivo2.setOnClickListener(v -> { currentAttachmentRequest = 2; attachmentHelper.dispatchOpenDocumentIntent(); });

        btnSolicitar.setOnClickListener(v -> {
            String message = "Solicitud para Celiaquía enviada";
            if (attachedFileUri1 != null || attachedFileUri2 != null) {
                message += " con archivos adjuntos";
            }
            Toast.makeText(getContext(), message + " (simulación)", Toast.LENGTH_LONG).show();
            dismiss();
        });
    }

    @Override
    public void onFileAttached(String fileName, Uri fileUri) {
        if (currentAttachmentRequest == 1) {
            tvArchivoAdjunto1.setText(fileName);
            tvArchivoAdjunto1.setVisibility(View.VISIBLE);
            this.attachedFileUri1 = fileUri;
        } else if (currentAttachmentRequest == 2) {
            tvArchivoAdjunto2.setText(fileName);
            tvArchivoAdjunto2.setVisibility(View.VISIBLE);
            this.attachedFileUri2 = fileUri;
        }
    }
}
