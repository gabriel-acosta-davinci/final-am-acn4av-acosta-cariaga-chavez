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

    private RecyclerView recyclerDocumentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_documentos);

        recyclerDocumentos = findViewById(R.id.recyclerMisDocumentos);
        recyclerDocumentos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar con lista vacía
        DocumentoAdapter adapter = new DocumentoAdapter(new ArrayList<>());
        recyclerDocumentos.setAdapter(adapter);

        // Cargar documentos desde backend
        loadRecentDocumentsForUser();
    }

    private void loadCurrentUser(String userId) {
        // Ya no necesitamos cargar usuario desde JSON, se usa el token
    }

    private void loadRecentDocumentsForUser() {
        com.example.medicalshift.utils.TokenManager tokenManager = new com.example.medicalshift.utils.TokenManager(this);
        String token = tokenManager.getToken();
        
        if (token == null) {
            android.util.Log.e("MisDocumentosActivity", "Token es null");
            Toast.makeText(this, "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;
        android.util.Log.d("MisDocumentosActivity", "Cargando documentos...");

        com.example.medicalshift.api.RetrofitClient.getInstance().getApiService()
                .getDocuments(authHeader, null, 50)
                .enqueue(new retrofit2.Callback<com.example.medicalshift.models.DocumentListResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.medicalshift.models.DocumentListResponse> call, 
                                         retrofit2.Response<com.example.medicalshift.models.DocumentListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.medicalshift.models.DocumentListResponse docResponse = response.body();
                            List<com.example.medicalshift.models.DocumentListResponse.DocumentItem> documents = docResponse.getDocuments();
                            
                            android.util.Log.d("MisDocumentosActivity", "Documentos recibidos: " + (documents != null ? documents.size() : 0));
                            
                            List<Documento> userDocuments = new ArrayList<>();
                            if (documents != null) {
                                for (com.example.medicalshift.models.DocumentListResponse.DocumentItem item : documents) {
                                    try {
                                        android.util.Log.d("MisDocumentosActivity", "Documento: " + item.getOriginalName() + ", gestionId: " + item.getGestionId() + ", uploadedAt: " + item.getUploadedAt());
                                        userDocuments.add(new Documento(item));
                                    } catch (Exception e) {
                                        android.util.Log.e("MisDocumentosActivity", "Error creando Documento", e);
                                        e.printStackTrace();
                                    }
                                }
                            }
                            
                            android.util.Log.d("MisDocumentosActivity", "Documentos procesados: " + userDocuments.size());
                            
                            DocumentoAdapter adapter = new DocumentoAdapter(userDocuments);
                            recyclerDocumentos.setAdapter(adapter);
                            
                            if (userDocuments.isEmpty()) {
                                Toast.makeText(MisDocumentosActivity.this, 
                                    "No se encontraron documentos en los últimos 3 meses.", 
                                    Toast.LENGTH_LONG).show();
                            }
                        } else {
                            android.util.Log.e("MisDocumentosActivity", "Error en respuesta: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    android.util.Log.e("MisDocumentosActivity", "Error body: " + errorBody);
                                } catch (Exception e) {
                                    android.util.Log.e("MisDocumentosActivity", "Error leyendo error body", e);
                                }
                            }
                            Toast.makeText(MisDocumentosActivity.this, 
                                "Error al cargar documentos: " + response.code(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.medicalshift.models.DocumentListResponse> call, Throwable t) {
                        android.util.Log.e("MisDocumentosActivity", "Error de conexión", t);
                        Toast.makeText(MisDocumentosActivity.this, 
                            "Error de conexión: " + t.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
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

        public Documento(com.example.medicalshift.models.DocumentListResponse.DocumentItem item) {
            this.nombreArchivo = item.getOriginalName() != null ? item.getOriginalName() : item.getFileName();
            this.tipoGestion = item.getGestionId() != null ? "Gestión: " + item.getGestionId() : "Documento";
            
            // Convertir timestamp a Date
            if (item.getUploadedAt() != null) {
                this.fecha = new Date(item.getUploadedAt());
            } else {
                this.fecha = new Date();
            }
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
