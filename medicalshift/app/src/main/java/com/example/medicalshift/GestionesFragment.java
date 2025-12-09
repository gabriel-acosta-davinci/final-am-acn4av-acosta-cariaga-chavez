package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.GestionResponse;
import com.example.medicalshift.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class GestionesFragment extends Fragment {

    private String userId;
    private TokenManager tokenManager;
    private RecyclerView recyclerGestiones;
    private GestionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gestiones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = new TokenManager(requireContext());
        userId = tokenManager.getUserId();
        
        if (userId == null) {
            userId = getArguments() != null ? getArguments().getString("LOGGED_IN_USER_ID") : null;
            if (userId != null) {
                tokenManager.saveUserId(userId);
            }
        }

        recyclerGestiones = view.findViewById(R.id.recyclerGestionesList);
        recyclerGestiones.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar con lista vacía
        adapter = new GestionAdapter(new ArrayList<>());
        recyclerGestiones.setAdapter(adapter);

        // Cargar gestiones desde backend
        loadGestionesFromBackend();

        MaterialButton btnNuevaGestion = view.findViewById(R.id.btnNuevaGestion);
        btnNuevaGestion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevaGestionActivity.class);
            intent.putExtra("LOGGED_IN_USER_ID", userId);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar gestiones cuando vuelve a la pantalla (por si se creó una nueva)
        if (userId != null) {
            loadGestionesFromBackend();
        }
    }

    private void loadGestionesFromBackend() {
        if (userId == null) {
            android.util.Log.e("GestionesFragment", "userId es null");
            Toast.makeText(getContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            android.util.Log.e("GestionesFragment", "token es null");
            Toast.makeText(getContext(), "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;
        
        android.util.Log.d("GestionesFragment", "Cargando gestiones para userId: " + userId);

        RetrofitClient.getInstance().getApiService().getGestiones(authHeader, userId, null, 20)
                .enqueue(new Callback<GestionResponse>() {
                    @Override
                    public void onResponse(Call<GestionResponse> call, Response<GestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            GestionResponse gestionResponse = response.body();
                            List<Gestion> gestiones = gestionResponse.getGestiones();
                            
                            android.util.Log.d("GestionesFragment", "Gestiones recibidas: " + (gestiones != null ? gestiones.size() : 0));
                            
                            if (gestiones != null && !gestiones.isEmpty()) {
                                // Filtrar gestiones por userId como medida de seguridad adicional
                                List<Gestion> gestionesFiltradas = new ArrayList<>();
                                for (Gestion gestion : gestiones) {
                                    android.util.Log.d("GestionesFragment", "Gestión: nombre=" + gestion.getNombre() + ", userId=" + gestion.getUserId() + ", fecha=" + gestion.getFecha());
                                    if (userId.equals(gestion.getUserId())) {
                                        gestionesFiltradas.add(gestion);
                                    } else {
                                        android.util.Log.w("GestionesFragment", "Gestión filtrada (userId no coincide): " + gestion.getNombre() + " (userId: " + gestion.getUserId() + " vs esperado: " + userId + ")");
                                    }
                                }
                                
                                android.util.Log.d("GestionesFragment", "Gestiones después del filtro: " + gestionesFiltradas.size());
                                
                                adapter = new GestionAdapter(gestionesFiltradas);
                                recyclerGestiones.setAdapter(adapter);
                            } else {
                                android.util.Log.d("GestionesFragment", "No hay gestiones para mostrar");
                                adapter = new GestionAdapter(new ArrayList<>());
                                recyclerGestiones.setAdapter(adapter);
                            }
                        } else {
                            android.util.Log.e("GestionesFragment", "Error en respuesta: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    android.util.Log.e("GestionesFragment", "Error body: " + errorBody);
                                } catch (Exception e) {
                                    android.util.Log.e("GestionesFragment", "Error leyendo error body", e);
                                }
                            }
                            Toast.makeText(getContext(), "Error al cargar gestiones", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GestionResponse> call, Throwable t) {
                        android.util.Log.e("GestionesFragment", "Error de conexión", t);
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
    }
}
