package com.example.medicalshift;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class EditarDatosBottomSheetFragment extends BottomSheetDialogFragment {

    public static EditarDatosBottomSheetFragment newInstance(User user) {
        EditarDatosBottomSheetFragment fragment = new EditarDatosBottomSheetFragment();
        Bundle args = new Bundle();
        // Pasamos los datos al fragmento
        args.putString("telefono", user.getTelefono());
        args.putString("email", user.getEmail());
        args.putString("estadoCivil", user.getEstadoCivil());
        args.putString("calle", user.getCalle());
        args.putString("numero", user.getNumero());
        args.putString("piso", user.getPiso());
        args.putString("dpto", user.getDpto());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_editar_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Encontrar Vistas
        EditText etTelefono = view.findViewById(R.id.etEditTelefono);
        EditText etEmail = view.findViewById(R.id.etEditEmail);
        EditText etEstadoCivil = view.findViewById(R.id.etEditEstadoCivil);
        EditText etCalle = view.findViewById(R.id.etEditCalle);
        EditText etNumero = view.findViewById(R.id.etEditNumero);
        EditText etPiso = view.findViewById(R.id.etEditPiso);
        EditText etDpto = view.findViewById(R.id.etEditDpto);
        MaterialButton btnGuardar = view.findViewById(R.id.btnGuardarCambios);

        // Rellenar formulario con datos actuales
        if (getArguments() != null) {
            etTelefono.setText(getArguments().getString("telefono"));
            etEmail.setText(getArguments().getString("email"));
            etEstadoCivil.setText(getArguments().getString("estadoCivil"));
            etCalle.setText(getArguments().getString("calle"));
            etNumero.setText(getArguments().getString("numero"));
            etPiso.setText(getArguments().getString("piso"));
            etDpto.setText(getArguments().getString("dpto"));
        }

        btnGuardar.setOnClickListener(v -> {
            // Aquí iría la lógica para guardar los datos en el backend
            Toast.makeText(getContext(), "Cambios guardados (simulación)", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
