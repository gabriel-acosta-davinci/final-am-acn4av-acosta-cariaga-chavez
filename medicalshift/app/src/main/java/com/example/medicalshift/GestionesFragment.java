package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GestionesFragment extends Fragment {

    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gestiones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadCurrentUser(); // Cargar usuario actual

        RecyclerView recyclerGestiones = view.findViewById(R.id.recyclerGestionesList);
        recyclerGestiones.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Gestion> listaGestiones = loadGestionesForUser();
        GestionAdapter adapter = new GestionAdapter(listaGestiones);
        recyclerGestiones.setAdapter(adapter);

        MaterialButton btnNuevaGestion = view.findViewById(R.id.btnNuevaGestion);
        btnNuevaGestion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevaGestionActivity.class);
            startActivity(intent);
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

    private List<Gestion> loadGestionesForUser() {
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
        return userGestiones;
    }

    private String loadJSONFromAsset(String fileName) {
        if (getContext() == null) return null;
        try (InputStream is = getContext().getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
