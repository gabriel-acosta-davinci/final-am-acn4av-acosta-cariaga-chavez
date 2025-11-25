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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NuevaGestionActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_gestion);

        loadCurrentUser();

        RecyclerView recyclerGestionOpciones = findViewById(R.id.recyclerGestionOpciones);
        recyclerGestionOpciones.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista de opciones con los nombres de íconos corregidos
        List<GestionOpcion> opciones = new ArrayList<>();
        opciones.add(new GestionOpcion("Medicamentos", R.drawable.ic_medication));
        opciones.add(new GestionOpcion("Autorizaciones Previas", R.drawable.ic_prescriptions));
        opciones.add(new GestionOpcion("Programa de Celiaquía", R.drawable.ic_celiac_disease));
        opciones.add(new GestionOpcion("Reintegros", R.drawable.ic_payments));
        opciones.add(new GestionOpcion("Traslados", R.drawable.ic_moving_ministry));

        GestionOpcionAdapter adapter = new GestionOpcionAdapter(opciones, opcion -> {
            if (currentUser == null) {
                Toast.makeText(this, "Cargando datos de usuario...", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (opcion.getNombre().equals("Medicamentos")) {
                Intent intent = new Intent(this, MedicamentosActivity.class);
                startActivity(intent);
            } else if (opcion.getNombre().equals("Autorizaciones Previas")) {
                AutorizacionesPreviasBottomSheetFragment bottomSheet = AutorizacionesPreviasBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.getNombre().equals("Programa de Celiaquía")) {
                CeliaquiaBottomSheetFragment bottomSheet = CeliaquiaBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.getNombre().equals("Reintegros")) {
                ReintegrosBottomSheetFragment bottomSheet = ReintegrosBottomSheetFragment.newInstance(currentUser.getNombreCompleto(), currentUser.getCbu());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.getNombre().equals("Traslados")) {
                TrasladosBottomSheetFragment bottomSheet = TrasladosBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else {
                Toast.makeText(this, "Iniciando gestión de: " + opcion.getNombre(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerGestionOpciones.setAdapter(adapter);
    }

    private void loadCurrentUser() {
        String json = loadJSONFromAsset("users.json");
        if (json != null) {
            try {
                JSONArray usersArray = new JSONArray(json);
                if (usersArray.length() > 0) {
                    currentUser = new User(usersArray.getJSONObject(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String loadJSONFromAsset(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // --- CLASES INTERNAS Y ADAPTADOR ---

    private interface OnGestionOpcionClickListener {
        void onGestionOpcionClick(GestionOpcion opcion);
    }

    private static class GestionOpcion {
        private final String nombre;
        private final int iconoResId;

        public GestionOpcion(String nombre, int iconoResId) {
            this.nombre = nombre;
            this.iconoResId = iconoResId;
        }

        public String getNombre() {
            return nombre;
        }

        public int getIconoResId() {
            return iconoResId;
        }
    }

    private static class GestionOpcionAdapter extends RecyclerView.Adapter<GestionOpcionAdapter.ViewHolder> {
        private final List<GestionOpcion> opciones;
        private final OnGestionOpcionClickListener listener;

        public GestionOpcionAdapter(List<GestionOpcion> opciones, OnGestionOpcionClickListener listener) {
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
            GestionOpcion opcion = opciones.get(position);
            holder.nombreOpcion.setText(opcion.getNombre());
            holder.iconOpcion.setImageResource(opcion.getIconoResId());
            holder.subtituloOpcion.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> listener.onGestionOpcionClick(opcion));
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
