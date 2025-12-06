package com.example.medicalshift;

import android.content.Intent;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rootContainer = view.findViewById(R.id.menu_root_container);
        View menuPanel = view.findViewById(R.id.menu_panel);

        rootContainer.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        menuPanel.setOnClickListener(null);

        RecyclerView recyclerOpciones = view.findViewById(R.id.recyclerMenuOpciones);
        recyclerOpciones.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> opciones = new ArrayList<>();
        opciones.add("Preguntas frecuentes");
        opciones.add("Asistencia al viajero");
        opciones.add("Coberturas especiales");
        opciones.add("E-doc");
        opciones.add("Contacto");

        MenuOpcionAdapter adapter = new MenuOpcionAdapter(opciones, opcion -> {
            if (opcion.equals("Preguntas frecuentes")) {
                startActivity(new Intent(getActivity(), PreguntasFrecuentesActivity.class));
            } else if (opcion.equals("Asistencia al viajero")) {
                startActivity(new Intent(getActivity(), AsistenciaViajeroActivity.class));
            } else if (opcion.equals("Coberturas especiales")) {
                startActivity(new Intent(getActivity(), CoberturasEspecialesActivity.class));
            } else if (opcion.equals("E-doc")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pacientes.meducar.com/"));
                startActivity(browserIntent);
            } else if (opcion.equals("Contacto")) {
                startActivity(new Intent(getActivity(), ContactoActivity.class));
            } else {
                Toast.makeText(getContext(), "Abriendo " + opcion, Toast.LENGTH_SHORT).show();
            }
            getParentFragmentManager().popBackStack();
        });
        recyclerOpciones.setAdapter(adapter);
    }

    private interface OnMenuOpcionClickListener {
        void onMenuOpcionClick(String opcion);
    }

    private static class MenuOpcionAdapter extends RecyclerView.Adapter<MenuOpcionAdapter.ViewHolder> {
        private final List<String> opciones;
        private final OnMenuOpcionClickListener listener;

        public MenuOpcionAdapter(List<String> opciones, OnMenuOpcionClickListener listener) {
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
            holder.itemView.setOnClickListener(v -> listener.onMenuOpcionClick(opcion));
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
