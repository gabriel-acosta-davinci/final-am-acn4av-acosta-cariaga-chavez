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

        String userId = getArguments() != null ? getArguments().getString("LOGGED_IN_USER_ID") : null;
        loadCurrentUser(userId);

        RecyclerView recyclerGestiones = view.findViewById(R.id.recyclerGestionesList);
        recyclerGestiones.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Gestion> listaGestiones = loadGestionesForUser();
        GestionAdapter adapter = new GestionAdapter(listaGestiones);
        recyclerGestiones.setAdapter(adapter);

        MaterialButton btnNuevaGestion = view.findViewById(R.id.btnNuevaGestion);
        btnNuevaGestion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevaGestionActivity.class);
            intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
            startActivity(intent);
        });
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

    private List<Gestion> loadGestionesForUser() {
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
        return userGestiones;
    }

    private String loadJSONFromAsset(String fileName) throws IOException {
        if (getContext() == null) return null;
        InputStream is = getContext().getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
