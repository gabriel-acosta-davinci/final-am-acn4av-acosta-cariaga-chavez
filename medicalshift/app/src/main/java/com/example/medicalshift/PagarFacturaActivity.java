package com.example.medicalshift;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.FacturaResponse;
import com.example.medicalshift.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class PagarFacturaActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private RecyclerView recyclerFacturas;
    private FacturaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar_factura);

        tokenManager = new TokenManager(this);

        recyclerFacturas = findViewById(R.id.recyclerFacturasPendientes);
        recyclerFacturas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar con lista vacía
        adapter = new FacturaAdapter(new ArrayList<>());
        recyclerFacturas.setAdapter(adapter);

        // Cargar facturas pendientes desde backend
        loadFacturasPendientes();
    }

    private void loadFacturasPendientes() {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;

        RetrofitClient.getInstance().getApiService().getFacturas(authHeader, "Pendiente", 50)
                .enqueue(new Callback<FacturaResponse>() {
                    @Override
                    public void onResponse(Call<FacturaResponse> call, Response<FacturaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            FacturaResponse facturaResponse = response.body();
                            List<Factura> facturas = facturaResponse.getFacturas();
                            
                            if (facturas != null && !facturas.isEmpty()) {
                                adapter = new FacturaAdapter(facturas);
                                recyclerFacturas.setAdapter(adapter);
                            } else {
                                Toast.makeText(PagarFacturaActivity.this, 
                                    "No tenés facturas pendientes de pago.", 
                                    Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(PagarFacturaActivity.this, 
                                "Error al cargar facturas", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FacturaResponse> call, Throwable t) {
                        Toast.makeText(PagarFacturaActivity.this, 
                            "Error de conexión", 
                            Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
    }
}
