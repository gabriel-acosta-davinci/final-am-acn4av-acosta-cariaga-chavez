package com.example.medicalshift;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.UserResponse;
import com.example.medicalshift.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReintegrosActivity extends AppCompatActivity {

    private User currentUser;
    private TextInputEditText etCurrentCbu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reintegros);

        // Encontrar Vistas
        LinearLayout editContainer = findViewById(R.id.editCbuContainer);
        etCurrentCbu = findViewById(R.id.etCurrentCbu);
        MaterialButton btnCambiarCuenta = findViewById(R.id.btnCambiarCuenta);
        TextInputEditText etNuevoCbu = findViewById(R.id.etNuevoCbu);
        MaterialButton btnEnviarSolicitud = findViewById(R.id.btnEnviarSolicitud);

        // Cargar usuario desde backend
        loadCurrentUserFromBackend();

        // Lógica de cambio de vista
        btnCambiarCuenta.setOnClickListener(v -> {
            btnCambiarCuenta.setVisibility(View.GONE);
            editContainer.setVisibility(View.VISIBLE);
        });

        // Lógica de envío de solicitud
        btnEnviarSolicitud.setOnClickListener(v -> {
            String nuevoCbu = etNuevoCbu.getText().toString();
            if (nuevoCbu.isEmpty() || nuevoCbu.length() < 22) {
                etNuevoCbu.setError("Ingresá un CBU válido");
            } else {
                Toast.makeText(this, "Solicitud de cambio de CBU enviada (simulación)", Toast.LENGTH_LONG).show();
                finish(); // Cierra la actividad
            }
        });
    }

    private void loadCurrentUserFromBackend() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        
        if (token == null) {
            Toast.makeText(this, "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            finish();
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
                                
                                // Fecha de nacimiento
                                String fechaNacimiento = "";
                                try {
                                    Object dateOfBirth = userResponse.getDateOfBirth();
                                    if (dateOfBirth != null) {
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
                                
                                // Mostrar CBU Actual
                                if (currentUser != null && etCurrentCbu != null) {
                                    String cbu = currentUser.getCbu();
                                    if (cbu != null && !cbu.isEmpty()) {
                                        etCurrentCbu.setText(cbu);
                                    } else {
                                        etCurrentCbu.setText("No configurado");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ReintegrosActivity.this, "Error al procesar datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReintegrosActivity.this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(ReintegrosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
    }
}
