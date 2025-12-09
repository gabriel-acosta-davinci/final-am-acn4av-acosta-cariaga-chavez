package com.example.medicalshift;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.UserResponse;
import com.example.medicalshift.utils.TokenManager;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MisDatosActivity extends AppCompatActivity {

    private User currentUser;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        tokenManager = new TokenManager(this);
        
        // Cargar datos del usuario desde backend
        loadCurrentUserFromBackend();

        Button btnEditar = findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(v -> {
            if (currentUser != null) {
                EditarDatosBottomSheetFragment bottomSheet = EditarDatosBottomSheetFragment.newInstance(currentUser);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    private void loadCurrentUserFromBackend() {
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
                            
                            // Convertir UserResponse a User para compatibilidad
                            try {
                                JSONObject userJson = new JSONObject();
                                userJson.put("Nombre Completo", userResponse.getFullName() != null ? userResponse.getFullName() : "");
                                userJson.put("Número de documento", userResponse.getDocumentNumber() != null ? userResponse.getDocumentNumber() : "");
                                userJson.put("Número de teléfono", userResponse.getPhoneNumber() != null ? userResponse.getPhoneNumber() : "");
                                userJson.put("Email", userResponse.getEmail() != null ? userResponse.getEmail() : "");
                                userJson.put("Plan", userResponse.getPlan() != null ? userResponse.getPlan() : "");
                                userJson.put("Número de asociado", userResponse.getAssociateNumber() != null ? userResponse.getAssociateNumber() : "");
                                userJson.put("CBU", userResponse.getCbu() != null ? userResponse.getCbu() : "");
                                
                                // Fecha de nacimiento - puede venir como Timestamp de Firestore
                                String fechaNacimiento = "";
                                try {
                                    Object dateOfBirth = userResponse.getDateOfBirth();
                                    if (dateOfBirth != null) {
                                        // Intentar parsear como Map (formato Firestore)
                                        if (dateOfBirth instanceof java.util.Map) {
                                            @SuppressWarnings("unchecked")
                                            java.util.Map<String, Object> dateMap = (java.util.Map<String, Object>) dateOfBirth;
                                            if (dateMap.containsKey("_seconds")) {
                                                Long seconds = ((Number) dateMap.get("_seconds")).longValue();
                                                Date date = new Date(seconds * 1000);
                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                                fechaNacimiento = sdf.format(date);
                                            }
                                        } else if (dateOfBirth instanceof Long) {
                                            // Si viene como timestamp directo
                                            Date date = new Date((Long) dateOfBirth * 1000);
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                            fechaNacimiento = sdf.format(date);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                userJson.put("Fecha de nacimiento", fechaNacimiento);
                                
                                // Estado civil
                                userJson.put("Estado Civil", userResponse.getMaritalStatus() != null ? userResponse.getMaritalStatus() : "");
                                userJson.put("contraseña", "");
                                userJson.put("token", "");
                                
                                // Domicilio
                                JSONObject domicilio = new JSONObject();
                                if (userResponse.getAddress() != null) {
                                    UserResponse.Address address = userResponse.getAddress();
                                    domicilio.put("Calle", address.getStreet() != null ? address.getStreet() : "");
                                    domicilio.put("Número", address.getNumber() != null ? address.getNumber() : 0);
                                    domicilio.put("Piso", address.getFloor() != null ? address.getFloor() : "");
                                    domicilio.put("Dpto", address.getApartment() != null ? address.getApartment() : "");
                                    domicilio.put("Localidad", address.getCity() != null ? address.getCity() : "");
                                    domicilio.put("Provincia", address.getProvince() != null ? address.getProvince() : "");
                                } else {
                                    domicilio.put("Calle", "");
                                    domicilio.put("Número", 0);
                                    domicilio.put("Localidad", "");
                                    domicilio.put("Provincia", "");
                                }
                                userJson.put("Domicilio de Residencia", domicilio);
                                
                                currentUser = new User(userJson);
                                updateUI();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MisDatosActivity.this, "Error al procesar datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MisDatosActivity.this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(MisDatosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
    }

    private void updateUI() {
        if (currentUser != null) {
            TextView tvNombre = findViewById(R.id.dataNombreCompleto);
            TextView tvDocumento = findViewById(R.id.dataNumeroDocumento);
            TextView tvFechaNac = findViewById(R.id.dataFechaNacimiento);
            TextView tvPlan = findViewById(R.id.dataPlan);
            TextView tvAsociado = findViewById(R.id.dataNumeroAsociado);
            TextView tvEstadoCivil = findViewById(R.id.dataEstadoCivil);
            TextView tvEmail = findViewById(R.id.dataEmail);
            TextView tvTelefono = findViewById(R.id.dataTelefono);
            TextView tvDomicilio = findViewById(R.id.dataDomicilio);
            
            if (tvNombre != null) tvNombre.setText(currentUser.getNombreCompleto());
            if (tvDocumento != null) tvDocumento.setText(currentUser.getNumeroDocumento());
            if (tvFechaNac != null) tvFechaNac.setText(currentUser.getFechaNacimiento());
            if (tvPlan != null) tvPlan.setText(currentUser.getPlan());
            if (tvAsociado != null) tvAsociado.setText(currentUser.getNumeroAsociado());
            if (tvEstadoCivil != null) tvEstadoCivil.setText(currentUser.getEstadoCivil());
            if (tvEmail != null) tvEmail.setText(currentUser.getEmail());
            if (tvTelefono != null) tvTelefono.setText(currentUser.getTelefono());
            if (tvDomicilio != null) tvDomicilio.setText(currentUser.getDomicilio());
        }
    }

    public void reloadUserData() {
        loadCurrentUserFromBackend();
    }
}
