package com.example.medicalshift;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {

    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = getArguments() != null ? getArguments().getString("LOGGED_IN_USER_ID") : null;
        loadCurrentUser(userId);

        RecyclerView recyclerOpciones = view.findViewById(R.id.recyclerPerfilOpciones);
        recyclerOpciones.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> opciones = new ArrayList<>();
        opciones.add("Mis Datos");
        opciones.add("Seguridad");
        opciones.add("Mis documentos");
        opciones.add("Cuenta de reintegro");
        opciones.add("Ver facturas / Pagar");
        opciones.add("Resumen de cuenta / Pagos");

        PerfilOpcionAdapter adapter = new PerfilOpcionAdapter(opciones, opcion -> {
            Intent intent;
            switch (opcion) {
                case "Mis Datos":
                    intent = new Intent(getActivity(), MisDatosActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
                    startActivity(intent);
                    break;
                case "Seguridad":
                    intent = new Intent(getActivity(), SeguridadActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
                    startActivity(intent);
                    break;
                case "Mis documentos":
                    intent = new Intent(getActivity(), MisDocumentosActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
                    startActivity(intent);
                    break;
                case "Cuenta de reintegro":
                    intent = new Intent(getActivity(), ReintegrosActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
                    startActivity(intent);
                    break;
                case "Ver facturas / Pagar":
                    intent = new Intent(getActivity(), PagarFacturaActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
                    startActivity(intent);
                    break;
                case "Resumen de cuenta / Pagos":
                    intent = new Intent(getActivity(), ResumenPagosActivity.class);
                    intent.putExtra("LOGGED_IN_USER_ID", currentUser.getNumeroDocumento());
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getContext(), "Abriendo sección: " + opcion, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        recyclerOpciones.setAdapter(adapter);

        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    private String loadJSONFromAsset(String fileName) throws IOException {
        if (getContext() == null) return null;
        InputStream is = getContext().getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    // --- ADAPTADOR PARA LAS OPCIONES DEL PERFIL ---

    private interface OnPerfilOpcionClickListener {
        void onPerfilOpcionClick(String opcion);
    }

    private static class PerfilOpcionAdapter extends RecyclerView.Adapter<PerfilOpcionAdapter.ViewHolder> {
        private final List<String> opciones;
        private final OnPerfilOpcionClickListener listener;

        public PerfilOpcionAdapter(List<String> opciones, OnPerfilOpcionClickListener listener) {
            this.opciones = opciones;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_especialidad_buscada, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String opcion = opciones.get(position);
            holder.nombreOpcion.setText(opcion);
            holder.iconOpcion.setVisibility(View.GONE);
            holder.subtituloOpcion.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> listener.onPerfilOpcionClick(opcion));
        }

        @Override
        public int getItemCount() {
            return opciones.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView iconOpcion;
            final TextView nombreOpcion, subtituloOpcion;

            public ViewHolder(View view) {
                super(view);
                iconOpcion = view.findViewById(R.id.iconEspecialidad);
                nombreOpcion = view.findViewById(R.id.nombreEspecialidad);
                subtituloOpcion = view.findViewById(R.id.tipoSolicitud);
            }
        }
    }
}
