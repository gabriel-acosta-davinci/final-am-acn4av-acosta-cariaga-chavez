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

import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.UserResponse;
import com.example.medicalshift.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicamentosActivity extends AppCompatActivity {

    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        // Cargar nombre del usuario desde backend
        loadUserNameFromBackend();

        RecyclerView recyclerMedicamentosOpciones = findViewById(R.id.recyclerMedicamentosOpciones);
        recyclerMedicamentosOpciones.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista de opciones
        List<String> opciones = new ArrayList<>();
        opciones.add("Oncología");
        opciones.add("Programa Diabetes");
        opciones.add("Programa Patologías Crónicas");
        opciones.add("Medicamentos con autorización previa");

        MedicamentosOpcionAdapter adapter = new MedicamentosOpcionAdapter(opciones, opcion -> {
            // Mostrar el bottom sheet correspondiente
            if (opcion.equals("Oncología")) {
                OncologiaBottomSheetFragment bottomSheet = OncologiaBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.equals("Programa Diabetes")) {
                DiabetesBottomSheetFragment bottomSheet = DiabetesBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.equals("Programa Patologías Crónicas")) {
                CronicasBottomSheetFragment bottomSheet = CronicasBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else if (opcion.equals("Medicamentos con autorización previa")) {
                AutorizacionBottomSheetFragment bottomSheet = AutorizacionBottomSheetFragment.newInstance(userName);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            } else {
                Toast.makeText(this, "Abriendo sección: " + opcion, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerMedicamentosOpciones.setAdapter(adapter);
    }

    private void loadUserNameFromBackend() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        
        if (token == null) {
            Toast.makeText(this, "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;

        RetrofitClient.getInstance().getApiService().getCurrentUser(authHeader)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse userResponse = response.body();
                            userName = userResponse.getFullName() != null ? userResponse.getFullName() : "";
                        } else {
                            userName = "";
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        userName = "";
                        // No mostrar error, simplemente usar string vacío
                    }
                });
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
