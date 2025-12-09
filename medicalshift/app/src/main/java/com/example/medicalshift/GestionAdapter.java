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
        
        // Formatear fecha para mostrar de forma legible
        String fechaFormateada = formatearFecha(gestion.getFecha());
        holder.fecha.setText(fechaFormateada);
        
        // Capitalizar primera letra del estado
        String estado = gestion.getEstado();
        if (estado != null && !estado.isEmpty()) {
            estado = estado.substring(0, 1).toUpperCase() + estado.substring(1).toLowerCase();
        }
        holder.estado.setText(estado);

        // Cambiar el FONDO del estado dinámicamente
        String estadoLower = gestion.getEstado() != null ? gestion.getEstado().toLowerCase() : "";
        switch (estadoLower) {
            case "aprobado":
                holder.estado.setBackgroundResource(R.drawable.bg_estado_aprobado);
                break;
            case "pendiente":
                holder.estado.setBackgroundResource(R.drawable.bg_estado_pendiente);
                break;
            case "rechazado":
                holder.estado.setBackgroundResource(R.drawable.bg_estado_rechazado);
                break;
            default:
                holder.estado.setBackgroundResource(R.drawable.bg_estado_pendiente);
                break;
        }
    }
    
    private String formatearFecha(String fechaISO) {
        if (fechaISO == null || fechaISO.isEmpty()) {
            return "Fecha no disponible";
        }
        
        try {
            // Intentar parsear como ISO string (ej: "2025-12-16T00:00:00.000Z")
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            
            java.util.Date date = inputFormat.parse(fechaISO);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            android.util.Log.w("GestionAdapter", "Error formateando fecha: " + fechaISO, e);
        }
        
        // Si falla, devolver la fecha original
        return fechaISO;
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
