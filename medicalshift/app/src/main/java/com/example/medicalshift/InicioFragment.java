package com.example.medicalshift;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InicioFragment extends Fragment {

    private OnNavigationRequest callback;
    private ProfesionalAdapter profesionalAdapter;
    private final List<Profesional> listaCompletaProfesionales = new ArrayList<>();
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationRequest) {
            callback = (OnNavigationRequest) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = getArguments() != null ? getArguments().getString("LOGGED_IN_USER_ID") : null;
        loadCurrentUser(userId);
        updateUI(view);

        RecyclerView recyclerGestiones = view.findViewById(R.id.recyclerGestiones);
        recyclerGestiones.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar con lista vacía, se actualizará cuando lleguen los datos del backend
        GestionAdapter gestionAdapter = new GestionAdapter(new ArrayList<>());
        recyclerGestiones.setAdapter(gestionAdapter);
        
        // Cargar las 3 gestiones más recientes desde el backend
        loadPreviewGestionesForUser();

        loadProfesionales();

        RecyclerView recyclerCartilla = view.findViewById(R.id.recyclerCartilla);
        recyclerCartilla.setLayoutManager(new LinearLayoutManager(getContext()));
        
        profesionalAdapter = new ProfesionalAdapter(listaCompletaProfesionales);
        recyclerCartilla.setAdapter(profesionalAdapter);

        EditText searchCartilla = view.findViewById(R.id.searchCartilla);
        searchCartilla.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { if (profesionalAdapter != null) profesionalAdapter.filtrar(s.toString()); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        Button btnVerGestiones = view.findViewById(R.id.btnVerGestiones);
        btnVerGestiones.setOnClickListener(v -> { if (callback != null) callback.navegarA(2); });

        Button btnVerCartilla = view.findViewById(R.id.btnVerCartilla);
        btnVerCartilla.setOnClickListener(v -> { if (callback != null) callback.navegarA(1); });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar las 3 gestiones más recientes cuando vuelve a la pantalla
        // (por si se creó una nueva gestión)
        loadPreviewGestionesForUser();
    }

    private void loadCurrentUser(String userId) {
        // Obtener userId del TokenManager si no viene en argumentos
        com.example.medicalshift.utils.TokenManager tokenManager = new com.example.medicalshift.utils.TokenManager(requireContext());
        String finalUserId = userId != null ? userId : tokenManager.getUserId();
        
        if (finalUserId == null) {
            return;
        }
        
        // Cargar usuario desde backend
        String token = tokenManager.getToken();
        if (token == null) {
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
                            // Convertir UserResponse a User para compatibilidad
                            com.example.medicalshift.models.UserResponse userResponse = response.body();
                            try {
                                JSONObject userJson = new JSONObject();
                                userJson.put("Nombre Completo", userResponse.getFullName());
                                userJson.put("Número de documento", userResponse.getDocumentNumber());
                                userJson.put("Número de teléfono", userResponse.getPhoneNumber());
                                userJson.put("Email", userResponse.getEmail());
                                userJson.put("Plan", userResponse.getPlan());
                                userJson.put("Número de asociado", userResponse.getAssociateNumber());
                                userJson.put("CBU", userResponse.getCbu());
                                userJson.put("Fecha de nacimiento", "");
                                userJson.put("Estado Civil", "");
                                userJson.put("contraseña", "");
                                userJson.put("token", "");
                                
                                // Domicilio
                                JSONObject domicilio = new JSONObject();
                                if (userResponse.getAddress() != null) {
                                    domicilio.put("Calle", userResponse.getAddress().getStreet());
                                    domicilio.put("Número", userResponse.getAddress().getNumber());
                                    domicilio.put("Piso", userResponse.getAddress().getFloor() != null ? userResponse.getAddress().getFloor() : "");
                                    domicilio.put("Dpto", userResponse.getAddress().getApartment() != null ? userResponse.getAddress().getApartment() : "");
                                    domicilio.put("Localidad", userResponse.getAddress().getCity());
                                    domicilio.put("Provincia", userResponse.getAddress().getProvince());
                                } else {
                                    domicilio.put("Calle", "");
                                    domicilio.put("Número", 0);
                                    domicilio.put("Localidad", "");
                                    domicilio.put("Provincia", "");
                                }
                                userJson.put("Domicilio de Residencia", domicilio);
                                
                                currentUser = new User(userJson);
                                updateUI(getView());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.medicalshift.models.UserResponse> call, Throwable t) {
                        // Error silencioso
                    }
                });
    }

    private void loadPreviewGestionesForUser() {
        com.example.medicalshift.utils.TokenManager tokenManager = new com.example.medicalshift.utils.TokenManager(requireContext());
        String userId = tokenManager.getUserId();
        String token = tokenManager.getToken();
        
        if (userId == null || token == null) {
            // Si no hay token o userId, mostrar lista vacía
            RecyclerView recyclerGestiones = getView() != null ? getView().findViewById(R.id.recyclerGestiones) : null;
            if (recyclerGestiones != null) {
                GestionAdapter gestionAdapter = new GestionAdapter(new ArrayList<>());
                recyclerGestiones.setAdapter(gestionAdapter);
            }
            return;
        }

        String authHeader = "Bearer " + token;
        
        // Cargar las 3 gestiones más recientes
        com.example.medicalshift.api.RetrofitClient.getInstance().getApiService()
                .getGestiones(authHeader, userId, null, 3)
                .enqueue(new retrofit2.Callback<com.example.medicalshift.models.GestionResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.medicalshift.models.GestionResponse> call,
                                         retrofit2.Response<com.example.medicalshift.models.GestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.medicalshift.models.GestionResponse gestionResponse = response.body();
                            List<Gestion> gestiones = gestionResponse.getGestiones();
                            
                            // Actualizar el RecyclerView con las gestiones obtenidas
                            RecyclerView recyclerGestiones = getView() != null ? getView().findViewById(R.id.recyclerGestiones) : null;
                            if (recyclerGestiones != null) {
                                if (gestiones != null && !gestiones.isEmpty()) {
                                    GestionAdapter gestionAdapter = new GestionAdapter(gestiones);
                                    recyclerGestiones.setAdapter(gestionAdapter);
                                } else {
                                    // Si no hay gestiones, mostrar lista vacía
                                    GestionAdapter gestionAdapter = new GestionAdapter(new ArrayList<>());
                                    recyclerGestiones.setAdapter(gestionAdapter);
                                }
                            }
                        } else {
                            // Si hay error en la respuesta, mostrar lista vacía
                            RecyclerView recyclerGestiones = getView() != null ? getView().findViewById(R.id.recyclerGestiones) : null;
                            if (recyclerGestiones != null) {
                                GestionAdapter gestionAdapter = new GestionAdapter(new ArrayList<>());
                                recyclerGestiones.setAdapter(gestionAdapter);
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.medicalshift.models.GestionResponse> call, Throwable t) {
                        // En caso de error de conexión, mostrar lista vacía
                        RecyclerView recyclerGestiones = getView() != null ? getView().findViewById(R.id.recyclerGestiones) : null;
                        if (recyclerGestiones != null) {
                            GestionAdapter gestionAdapter = new GestionAdapter(new ArrayList<>());
                            recyclerGestiones.setAdapter(gestionAdapter);
                        }
                        android.util.Log.e("InicioFragment", "Error cargando gestiones", t);
                    }
                });
    }

    private void updateUI(View view) {
        if (currentUser != null) {
            ((TextView) view.findViewById(R.id.greeting)).setText("¡Hola, " + currentUser.getNombreCompleto().split(" ")[0] + "!");
            ((TextView) view.findViewById(R.id.cardTitle)).setText(currentUser.getNombreCompleto());
            ((TextView) view.findViewById(R.id.cardAsociadoValue)).setText(currentUser.getNumeroAsociado());
            ((TextView) view.findViewById(R.id.cardPlanValue)).setText(currentUser.getPlan());
        }

        TextView saludoHora = view.findViewById(R.id.saludoHora);
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        String mensaje = (hora >= 6 && hora < 12) ? "Que tengas una excelente mañana ☀️" : (hora >= 12 && hora < 18) ? "¡Buena tarde! 🌤️" : (hora >= 18 && hora < 22) ? "Disfrutá tu noche 🌙" : "Es hora de descansar 😴";
        saludoHora.setText(mensaje);
    }

    private void loadProfesionales() { /* ... (código existente) ... */ }

    private String loadJSONFromAsset(String fileName) throws IOException {
        if (getContext() == null) return null;
        InputStream is = getContext().getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public interface OnNavigationRequest { void navegarA(int posicion); }
}
