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

        loadCurrentUser();
        updateUI(view);

        // --- RecyclerView de gestiones (LÓGICA REAL Y ORDENADA) ---
        RecyclerView recyclerGestiones = view.findViewById(R.id.recyclerGestiones);
        recyclerGestiones.setLayoutManager(new LinearLayoutManager(getContext()));
        
        List<Gestion> listaGestiones = loadPreviewGestionesForUser();
        GestionAdapter gestionAdapter = new GestionAdapter(listaGestiones);
        recyclerGestiones.setAdapter(gestionAdapter);

        // --- Búsqueda y RecyclerView de cartilla médica ---
        loadProfesionales();

        RecyclerView recyclerCartilla = view.findViewById(R.id.recyclerCartilla);
        recyclerCartilla.setLayoutManager(new LinearLayoutManager(getContext()));
        
        profesionalAdapter = new ProfesionalAdapter(listaCompletaProfesionales);
        recyclerCartilla.setAdapter(profesionalAdapter);

        EditText searchCartilla = view.findViewById(R.id.searchCartilla);
        searchCartilla.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (profesionalAdapter != null) {
                    profesionalAdapter.filtrar(s.toString());
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- Botones ---
        Button btnVerGestiones = view.findViewById(R.id.btnVerGestiones);
        btnVerGestiones.setOnClickListener(v -> {
            if (callback != null) callback.navegarA(2);
        });

        Button btnVerCartilla = view.findViewById(R.id.btnVerCartilla);
        btnVerCartilla.setOnClickListener(v -> {
            if (callback != null) callback.navegarA(1);
        });
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

    private List<Gestion> loadPreviewGestionesForUser() {
        List<Gestion> userGestiones = new ArrayList<>();
        if (currentUser == null) return userGestiones;

        String json = loadJSONFromAsset("gestiones.json"); 
        if (json != null) {
            try {
                JSONArray gestionesArray = new JSONArray(json);
                for (int i = 0; i < gestionesArray.length(); i++) {
                    JSONObject gestionObject = gestionesArray.getJSONObject(i);
                    if (gestionObject.getString("userId").equals(currentUser.getNumeroDocumento())) {
                        userGestiones.add(new Gestion(gestionObject.getString("nombre"), gestionObject.getString("fecha"), gestionObject.getString("estado")));
                    }
                }
                // Ordenar la lista por fecha, de más reciente a más antigua
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Collections.sort(userGestiones, (g1, g2) -> {
                    try {
                        Date d1 = sdf.parse(g1.getFecha());
                        Date d2 = sdf.parse(g2.getFecha());
                        return d2.compareTo(d1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Devolver solo las primeras 3 (o menos si no hay tantas)
        return userGestiones.subList(0, Math.min(3, userGestiones.size()));
    }

    private void updateUI(View view) {
        if (currentUser != null) {
            TextView greeting = view.findViewById(R.id.greeting);
            greeting.setText("¡Hola, " + currentUser.getNombreCompleto().split(" ")[0] + "!");

            TextView cardTitle = view.findViewById(R.id.cardTitle);
            TextView cardAsociado = view.findViewById(R.id.cardAsociadoValue);
            TextView cardPlan = view.findViewById(R.id.cardPlanValue);
            cardTitle.setText(currentUser.getNombreCompleto());
            cardAsociado.setText(currentUser.getNumeroAsociado());
            cardPlan.setText(currentUser.getPlan());
        }

        TextView saludoHora = view.findViewById(R.id.saludoHora);
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        String mensaje;
        if (hora >= 6 && hora < 12) { mensaje = "Que tengas una excelente mañana ☀️";
        } else if (hora >= 12 && hora < 18) { mensaje = "¡Buena tarde! 🌤️";
        } else if (hora >= 18 && hora < 22) { mensaje = "Disfrutá tu noche 🌙";
        } else { mensaje = "Es hora de descansar 😴"; }
        saludoHora.setText(mensaje);
    }

    private void loadProfesionales() {
        String json = loadJSONFromAsset("professionals.json");
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    listaCompletaProfesionales.add(new Profesional(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String loadJSONFromAsset(String fileName) {
        if (getContext() == null) return null;
        String json;
        try {
            InputStream is = getContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public interface OnNavigationRequest {
        void navegarA(int posicion);
    }
}
