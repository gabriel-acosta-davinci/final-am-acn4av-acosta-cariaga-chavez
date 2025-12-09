package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.LoginRequest;
import com.example.medicalshift.models.LoginResponse;
import com.example.medicalshift.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etDocumento;
    private TextInputEditText etContraseña;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);

        etDocumento = findViewById(R.id.etDocumento);
        etContraseña = findViewById(R.id.etContraseña);
        MaterialButton btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> {
            String documento = etDocumento.getText().toString().trim();
            String contraseña = etContraseña.getText().toString();

            if (documento.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Deshabilitar botón mientras se procesa
            btnIngresar.setEnabled(false);
            btnIngresar.setText("Ingresando...");

            // Llamar al backend
            login(documento, contraseña);
        });
    }

    private void login(String documento, String contraseña) {
        LoginRequest request = new LoginRequest(documento, contraseña, "documentNumber");
        
        RetrofitClient.getInstance().getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                MaterialButton btnIngresar = findViewById(R.id.btnIngresar);
                btnIngresar.setEnabled(true);
                btnIngresar.setText("Ingresar");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    
                    // Obtener userId del user object
                    // IMPORTANTE: Usar documentNumber porque las gestiones/facturas usan documentNumber como userId
                    String userId = null;
                    if (loginResponse.getUser() != null) {
                        LoginResponse.UserData user = loginResponse.getUser();
                        // Priorizar documentNumber porque es lo que se usa en gestiones/facturas
                        userId = user.getDocumentNumber() != null ? user.getDocumentNumber() : user.getId();
                    }
                    
                    // Si aún no tenemos userId, usar el documento ingresado
                    if (userId == null || userId.isEmpty()) {
                        userId = documento;
                    }

                    // Guardar token y userId (documentNumber)
                    tokenManager.saveToken(token);
                    tokenManager.saveUserId(userId);
                    
                    android.util.Log.d("LoginActivity", "Token guardado, UserId (documentNumber): " + userId);

                    // Ir a MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", userId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, 
                        "Documento o contraseña incorrectos", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                MaterialButton btnIngresar = findViewById(R.id.btnIngresar);
                btnIngresar.setEnabled(true);
                btnIngresar.setText("Ingresar");

                String errorMessage = "Error de conexión";
                if (t.getMessage() != null) {
                    errorMessage += ": " + t.getMessage();
                }
                
                // Mostrar en Toast y también en Log para debugging
                Toast.makeText(LoginActivity.this, 
                    errorMessage, 
                    Toast.LENGTH_LONG).show();
                
                // Imprimir en Logcat para debugging
                android.util.Log.e("LoginActivity", "Error de conexión", t);
                t.printStackTrace();
            }
        });
    }
}
