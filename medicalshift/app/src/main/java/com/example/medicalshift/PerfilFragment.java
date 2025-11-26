package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerOpciones = view.findViewById(R.id.recyclerPerfilOpciones);
        recyclerOpciones.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear lista de opciones del perfil
        List<String> opciones = new ArrayList<>();
        opciones.add("Mis Datos");
        opciones.add("Seguridad");
        opciones.add("Mis documentos");
        opciones.add("Cuenta de reintegro");
        opciones.add("Ver facturas / Pagar");
        opciones.add("Resumen de cuenta / Pagos");

        PerfilOpcionAdapter adapter = new PerfilOpcionAdapter(opciones, opcion -> {
            if (opcion.equals("Mis Datos")) {
                startActivity(new Intent(getActivity(), MisDatosActivity.class));
            } else if (opcion.equals("Seguridad")) {
                startActivity(new Intent(getActivity(), SeguridadActivity.class));
            } else if (opcion.equals("Mis documentos")) {
                startActivity(new Intent(getActivity(), MisDocumentosActivity.class));
            } else if (opcion.equals("Cuenta de reintegro")) {
                startActivity(new Intent(getActivity(), ReintegrosActivity.class));
            } else if (opcion.equals("Ver facturas / Pagar")) {
                startActivity(new Intent(getActivity(), PagarFacturaActivity.class));
            } else if (opcion.equals("Resumen de cuenta / Pagos")) {
                startActivity(new Intent(getActivity(), ResumenPagosActivity.class));
            } else {
                Toast.makeText(getContext(), "Abriendo sección: " + opcion, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerOpciones.setAdapter(adapter);

        // Botón de Cerrar Sesión
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navegarA(0);
            }
        });
    }

    // --- ADAPTADOR PARA LAS OPCIONES DEL PERFIL ---

    private interface OnPerfilOpcionClickListener {
        void onPerfilOpcionClick(String opcion);
    }

    private static class PerfilOpcionAdapter extends RecyclerView.Adapter<PerfilOpcionAdapter.ViewHolder> {
        private final List<String> opciones;
        private final OnPerfilOpcionClickListener listener;

        public PerfilOpcionAdapter(List<String> opciones, OnPerfilOpcionClickListener listener) {
            this.opciones = opciones;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_especialidad_buscada, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String opcion = opciones.get(position);
            holder.nombreOpcion.setText(opcion);
            holder.iconOpcion.setVisibility(View.GONE);
            holder.subtituloOpcion.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> listener.onPerfilOpcionClick(opcion));
        }

        @Override
        public int getItemCount() {
            return opciones.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView iconOpcion;
            final TextView nombreOpcion, subtituloOpcion;

            public ViewHolder(View view) {
                super(view);
                iconOpcion = view.findViewById(R.id.iconEspecialidad);
                nombreOpcion = view.findViewById(R.id.nombreEspecialidad);
                subtituloOpcion = view.findViewById(R.id.tipoSolicitud);
            }
        }
    }
}
