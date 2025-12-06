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
        
        List<Gestion> listaGestiones = loadPreviewGestionesForUser();
        GestionAdapter gestionAdapter = new GestionAdapter(listaGestiones);
        recyclerGestiones.setAdapter(gestionAdapter);

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

    private void loadCurrentUser(String userId) {
        if (userId == null) return;
        try {
            String json = loadJSONFromAsset("users.json");
            JSONArray usersArray = new JSONArray(json);
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                if (userObject.getString("Número de documento").equals(userId)) {
                    currentUser = new User(userObject);
                    break;
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Gestion> loadPreviewGestionesForUser() {
        List<Gestion> userGestiones = new ArrayList<>();
        if (currentUser == null) return userGestiones;
        try {
            String json = loadJSONFromAsset("gestiones.json"); 
            JSONArray gestionesArray = new JSONArray(json);
            for (int i = 0; i < gestionesArray.length(); i++) {
                JSONObject g = gestionesArray.getJSONObject(i);
                if (g.getString("userId").equals(currentUser.getNumeroDocumento())) {
                    userGestiones.add(new Gestion(g.getString("nombre"), g.getString("fecha"), g.getString("estado")));
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Collections.sort(userGestiones, (g1, g2) -> {
                try {
                    Date d1 = sdf.parse(g1.getFecha());
                    Date d2 = sdf.parse(g2.getFecha());
                    return d2.compareTo(d1);
                } catch (ParseException e) { return 0; }
            });
        } catch (IOException | JSONException e) { e.printStackTrace(); }
        return userGestiones.subList(0, Math.min(3, userGestiones.size()));
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
