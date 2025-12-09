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

    private com.example.medicalshift.models.UserResponse currentUser;
    private com.example.medicalshift.utils.TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_gestion);

        tokenManager = new com.example.medicalshift.utils.TokenManager(this);
        loadCurrentUserFromBackend();

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
            
            String userName = currentUser.getFullName() != null ? currentUser.getFullName() : "Usuario";
            String cbu = currentUser.getCbu() != null ? currentUser.getCbu() : "";
            
            if (opcion.getNombre().equals("Medicamentos")) {
                Intent intent = new Intent(this, MedicamentosActivity.class);
                startActivity(intent);
            } else if (opcion.getNombre().equals("Autorizaciones Previas")) {
                AutorizacionesPreviasBottomSheetFragment bottomSheet = AutorizacionesPreviasBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.getNombre().equals("Programa de Celiaquía")) {
                CeliaquiaBottomSheetFragment bottomSheet = CeliaquiaBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.getNombre().equals("Reintegros")) {
                ReintegrosBottomSheetFragment bottomSheet = ReintegrosBottomSheetFragment.newInstance(userName, cbu);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.getNombre().equals("Traslados")) {
                TrasladosBottomSheetFragment bottomSheet = TrasladosBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else {
                Toast.makeText(this, "Iniciando gestión de: " + opcion.getNombre(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerGestionOpciones.setAdapter(adapter);
    }

    private void loadCurrentUserFromBackend() {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;
        com.example.medicalshift.api.RetrofitClient.getInstance().getApiService()
                .getCurrentUser(authHeader)
                .enqueue(new retrofit2.Callback<com.example.medicalshift.models.UserResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.medicalshift.models.UserResponse> call,
                                         retrofit2.Response<com.example.medicalshift.models.UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentUser = response.body();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.medicalshift.models.UserResponse> call, Throwable t) {
                        Toast.makeText(NuevaGestionActivity.this, "Error al cargar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
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
