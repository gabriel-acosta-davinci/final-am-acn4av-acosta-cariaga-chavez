package com.example.medicalshift;

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

public class MedicamentosActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        loadCurrentUser();

        RecyclerView recyclerMedicamentosOpciones = findViewById(R.id.recyclerMedicamentosOpciones);
        recyclerMedicamentosOpciones.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista de opciones
        List<String> opciones = new ArrayList<>();
        opciones.add("Oncología");
        opciones.add("Programa Diabetes");
        opciones.add("Programa Patologías Crónicas");
        opciones.add("Medicamentos con autorización previa");

        MedicamentosOpcionAdapter adapter = new MedicamentosOpcionAdapter(opciones, opcion -> {
            if (currentUser == null) {
                Toast.makeText(this, "Cargando datos de usuario...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (opcion.equals("Oncología")) {
                OncologiaBottomSheetFragment bottomSheet = OncologiaBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.equals("Programa Diabetes")) {
                DiabetesBottomSheetFragment bottomSheet = DiabetesBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.equals("Programa Patologías Crónicas")) {
                CronicasBottomSheetFragment bottomSheet = CronicasBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.equals("Medicamentos con autorización previa")) {
                AutorizacionBottomSheetFragment bottomSheet = AutorizacionBottomSheetFragment.newInstance(currentUser.getNombreCompleto());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else {
                Toast.makeText(this, "Abriendo sección: " + opcion, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerMedicamentosOpciones.setAdapter(adapter);
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

    private interface OnMedicamentoOpcionClickListener {
        void onMedicamentoOpcionClick(String opcion);
    }

    private static class MedicamentosOpcionAdapter extends RecyclerView.Adapter<MedicamentosOpcionAdapter.ViewHolder> {
        private final List<String> opciones;
        private final OnMedicamentoOpcionClickListener listener;

        public MedicamentosOpcionAdapter(List<String> opciones, OnMedicamentoOpcionClickListener listener) {
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
            holder.itemView.setOnClickListener(v -> listener.onMedicamentoOpcionClick(opcion));
        }

        @Override
        public int getItemCount() {
            return opciones.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView iconOpcion, arrowEspecialidad;
            final TextView nombreOpcion, subtituloOpcion;

            public ViewHolder(View view) {
                super(view);
                iconOpcion = view.findViewById(R.id.iconEspecialidad);
                nombreOpcion = view.findViewById(R.id.nombreEspecialidad);
                subtituloOpcion = view.findViewById(R.id.tipoSolicitud);
                arrowEspecialidad = view.findViewById(R.id.arrowEspecialidad);
            }
        }
    }
}
