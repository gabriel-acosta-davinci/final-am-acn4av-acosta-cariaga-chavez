package com.example.medicalshift;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MisDocumentosActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_documentos);

        String userId = getIntent().getStringExtra("LOGGED_IN_USER_ID");
        loadCurrentUser(userId);

        RecyclerView recyclerDocumentos = findViewById(R.id.recyclerMisDocumentos);
        recyclerDocumentos.setLayoutManager(new LinearLayoutManager(this));

        List<Documento> listaDocumentos = loadRecentDocumentsForUser();
        DocumentoAdapter adapter = new DocumentoAdapter(listaDocumentos);
        recyclerDocumentos.setAdapter(adapter);

        if (listaDocumentos.isEmpty()) {
            Toast.makeText(this, "No se encontraron documentos en el último mes.", Toast.LENGTH_LONG).show();
        }
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
        } catch (JSONException e) { // CORREGIDO: Solo se captura JSONException
            e.printStackTrace();
        }
    }

    private List<Documento> loadRecentDocumentsForUser() {
        List<Documento> userDocuments = new ArrayList<>();
        if (currentUser == null) return userDocuments;

        String json = loadJSONFromAsset("documentos_historial.json");
        if (json != null) {
            try {
                JSONArray documentsArray = new JSONArray(json);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1); 
                Date oneMonthAgo = cal.getTime();

                for (int i = 0; i < documentsArray.length(); i++) {
                    JSONObject docObject = documentsArray.getJSONObject(i);
                    if (docObject.getString("userId").equals(currentUser.getNumeroDocumento())) {
                        Date docDate = sdf.parse(docObject.getString("fecha"));
                        if (docDate.after(oneMonthAgo)) {
                            userDocuments.add(new Documento(docObject));
                        }
                    }
                }
                Collections.sort(userDocuments, (d1, d2) -> d2.getFecha().compareTo(d1.getFecha()));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        return userDocuments;
    }

    private String loadJSONFromAsset(String fileName) {
        try (InputStream is = getAssets().open(fileName)) {
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
    private static class Documento {
        final String nombreArchivo, tipoGestion;
        final Date fecha;

        public Documento(JSONObject object) throws JSONException, ParseException {
            this.nombreArchivo = object.getString("nombreArchivo");
            this.tipoGestion = object.getString("tipoGestion");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            this.fecha = sdf.parse(object.getString("fecha"));
        }

        public String getFechaAsString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(this.fecha);
        }
        public Date getFecha() { return fecha; }
    }

    private static class DocumentoAdapter extends RecyclerView.Adapter<DocumentoAdapter.ViewHolder> {
        private final List<Documento> documentos;

        public DocumentoAdapter(List<Documento> documentos) {
            this.documentos = documentos;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_documento, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Documento documento = documentos.get(position);
            holder.nombre.setText(documento.nombreArchivo);
            holder.tipoGestion.setText(documento.tipoGestion);
            holder.fecha.setText(documento.getFechaAsString());
            holder.btnDescargar.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Descargando " + documento.nombreArchivo, Toast.LENGTH_SHORT).show();
            });
        }

        @Override public int getItemCount() { return documentos.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView nombre, tipoGestion, fecha;
            final ImageButton btnDescargar;
            public ViewHolder(View view) {
                super(view);
                nombre = view.findViewById(R.id.documentoNombre);
                tipoGestion = view.findViewById(R.id.documentoTipoGestion);
                fecha = view.findViewById(R.id.documentoFecha);
                btnDescargar = view.findViewById(R.id.btnDescargar);
            }
        }
    }
}
