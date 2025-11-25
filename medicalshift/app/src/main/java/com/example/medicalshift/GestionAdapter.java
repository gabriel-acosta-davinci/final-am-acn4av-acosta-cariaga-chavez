package com.example.medicalshift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GestionAdapter extends RecyclerView.Adapter<GestionAdapter.GestionViewHolder> {

    private List<Gestion> listaGestiones;

    public GestionAdapter(List<Gestion> listaGestiones) {
        this.listaGestiones = listaGestiones;
    }

    @NonNull
    @Override
    public GestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gestion, parent, false);
        return new GestionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull GestionViewHolder holder, int position) {
        Gestion gestion = listaGestiones.get(position);
        holder.titulo.setText(gestion.getTitulo());
        holder.fecha.setText(gestion.getFecha());
        holder.estado.setText(gestion.getEstado());

        // Cambiar el FONDO del estado dinámicamente
        switch (gestion.getEstado()) {
            case "Aprobado":
                holder.estado.setBackgroundResource(R.drawable.bg_estado_aprobado);
                break;
            case "Pendiente":
                holder.estado.setBackgroundResource(R.drawable.bg_estado_pendiente);
                break;
            case "Rechazado":
                holder.estado.setBackgroundResource(R.drawable.bg_estado_rechazado);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaGestiones.size();
    }

    static class GestionViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, fecha, estado;

        public GestionViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.gestionTitle);
            fecha = itemView.findViewById(R.id.gestionDate);
            estado = itemView.findViewById(R.id.gestionStatus);
        }
    }
}
