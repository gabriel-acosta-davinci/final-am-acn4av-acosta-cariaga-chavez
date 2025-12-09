package com.example.medicalshift;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medicalshift.api.RetrofitClient;
import com.example.medicalshift.models.UpdateUserRequest;
import com.example.medicalshift.models.UserResponse;
import com.example.medicalshift.utils.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarDatosBottomSheetFragment extends BottomSheetDialogFragment {

    private Spinner spinnerProvincia;
    private TextInputEditText etLocalidad;
    private List<String> provincias;
    private JSONObject localidadesJson;
    private String provinciaSeleccionada;

    public static EditarDatosBottomSheetFragment newInstance(User user) {
        EditarDatosBottomSheetFragment fragment = new EditarDatosBottomSheetFragment();
        Bundle args = new Bundle();
        // Pasamos los datos al fragmento
        args.putString("telefono", user.getTelefono());
        args.putString("email", user.getEmail());
        args.putString("estadoCivil", user.getEstadoCivil());
        args.putString("calle", user.getCalle());
        args.putString("numero", user.getNumero());
        args.putString("piso", user.getPiso());
        args.putString("dpto", user.getDpto());
        args.putString("localidad", user.getLocalidad());
        args.putString("provincia", user.getProvincia());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_editar_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Encontrar Vistas
        TextInputEditText etTelefono = view.findViewById(R.id.etEditTelefono);
        TextInputEditText etEmail = view.findViewById(R.id.etEditEmail);
        TextInputEditText etEstadoCivil = view.findViewById(R.id.etEditEstadoCivil);
        TextInputEditText etCalle = view.findViewById(R.id.etEditCalle);
        TextInputEditText etNumero = view.findViewById(R.id.etEditNumero);
        TextInputEditText etPiso = view.findViewById(R.id.etEditPiso);
        TextInputEditText etDpto = view.findViewById(R.id.etEditDpto);
        spinnerProvincia = view.findViewById(R.id.spinnerProvincia);
        etLocalidad = view.findViewById(R.id.etEditLocalidad);
        MaterialButton btnGuardar = view.findViewById(R.id.btnGuardarCambios);

        // Rellenar formulario con datos actuales
        if (getArguments() != null) {
            etTelefono.setText(getArguments().getString("telefono"));
            etEmail.setText(getArguments().getString("email"));
            etEstadoCivil.setText(getArguments().getString("estadoCivil"));
            etCalle.setText(getArguments().getString("calle"));
            etNumero.setText(getArguments().getString("numero"));
            etPiso.setText(getArguments().getString("piso"));
            etDpto.setText(getArguments().getString("dpto"));
            etLocalidad.setText(getArguments().getString("localidad"));
        }

        // Cargar localidades desde JSON (esto configurará el spinner)
        loadLocalidades();

        btnGuardar.setOnClickListener(v -> {
            // Validar campos
            String telefono = etTelefono.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String estadoCivil = etEstadoCivil.getText().toString().trim();
            String calle = etCalle.getText().toString().trim();
            String numeroStr = etNumero.getText().toString().trim();
            String piso = etPiso.getText().toString().trim();
            String dpto = etDpto.getText().toString().trim();
            String localidad = etLocalidad.getText().toString().trim();
            String provincia = provinciaSeleccionada;

            if (telefono.isEmpty() || email.isEmpty() || calle.isEmpty() || numeroStr.isEmpty() || 
                localidad.isEmpty() || provincia == null) {
                Toast.makeText(getContext(), "Por favor, completá todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer numero;
            try {
                numero = Integer.parseInt(numeroStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "El número debe ser un valor numérico", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear request
            UpdateUserRequest request = new UpdateUserRequest();
            request.setPhoneNumber(telefono);
            request.setEmail(email);
            request.setMaritalStatus(estadoCivil);
            request.setStreet(calle);
            request.setNumber(numero);
            request.setFloor(piso.isEmpty() ? null : piso);
            request.setApartment(dpto.isEmpty() ? null : dpto);
            request.setCity(localidad);
            request.setProvince(provincia);

            // Actualizar en backend
            updateUserData(request);
        });
    }

    private void loadLocalidades() {
        try {
            String json = loadJSONFromAsset("localidades.json");
            if (json != null) {
                localidadesJson = new JSONObject(json);
                
                // Obtener lista de provincias
                provincias = new ArrayList<>();
                java.util.Iterator<String> keys = localidadesJson.keys();
                while (keys.hasNext()) {
                    provincias.add(keys.next());
                }
                Collections.sort(provincias);

                // Configurar spinner de provincias
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    provincias
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProvincia.setAdapter(adapter);

                // Seleccionar provincia actual si existe
                if (getArguments() != null) {
                    String provinciaActual = getArguments().getString("provincia");
                    if (provinciaActual != null && provincias.contains(provinciaActual)) {
                        int position = provincias.indexOf(provinciaActual);
                        spinnerProvincia.setSelection(position);
                        provinciaSeleccionada = provinciaActual;
                    }
                }

                // Listener para cuando se selecciona una provincia
                spinnerProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0 || provincias.size() > 0) {
                            provinciaSeleccionada = provincias.get(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        provinciaSeleccionada = null;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al cargar localidades", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(UpdateUserRequest request) {
        TokenManager tokenManager = new TokenManager(requireContext());
        String token = tokenManager.getToken();
        
        if (token == null) {
            Toast.makeText(getContext(), "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        String authHeader = "Bearer " + token;
        MaterialButton btnGuardar = getView().findViewById(R.id.btnGuardarCambios);
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        RetrofitClient.getInstance().getApiService().updateCurrentUser(authHeader, request)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText("Guardar Cambios");

                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                            
                            // Notificar a la actividad padre para que recargue los datos
                            if (getActivity() instanceof MisDatosActivity) {
                                ((MisDatosActivity) getActivity()).reloadUserData();
                            }
                            
                            dismiss();
                        } else {
                            // Intentar leer el mensaje de error del cuerpo de la respuesta
                            String errorMessage = "Error al actualizar datos (Código: " + response.code() + ")";
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    android.util.Log.e("UpdateUser", "Error response body: " + errorBody);
                                    android.util.Log.e("UpdateUser", "Error code: " + response.code());
                                    
                                    // Intentar parsear el JSON del error
                                    try {
                                        JSONObject errorJson = new JSONObject(errorBody);
                                        if (errorJson.has("error")) {
                                            errorMessage = errorJson.getString("error");
                                        } else if (errorJson.has("message")) {
                                            errorMessage = errorJson.getString("message");
                                        }
                                    } catch (Exception e) {
                                        // Si no es JSON, usar el texto completo
                                        if (errorBody.length() > 100) {
                                            errorBody = errorBody.substring(0, 100) + "...";
                                        }
                                        errorMessage = "Error: " + errorBody;
                                    }
                                } else {
                                    android.util.Log.e("UpdateUser", "Error code: " + response.code() + ", no error body");
                                }
                            } catch (IOException e) {
                                android.util.Log.e("UpdateUser", "Error reading error body", e);
                            }
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText("Guardar Cambios");
                        android.util.Log.e("UpdateUser", "Error de conexión", t);
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    private String loadJSONFromAsset(String fileName) {
        try {
            InputStream is = requireContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
