package com.example.medicalshift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EspecialidadAdapter extends RecyclerView.Adapter<EspecialidadAdapter.ViewHolder> {
    private final List<Especialidad> especialidades;

    public EspecialidadAdapter(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_especialidad_buscada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Especialidad especialidad = especialidades.get(position);
        holder.nombre.setText(especialidad.getNombre());
        holder.descripcion.setText(especialidad.getDescripcion());
        holder.icono.setImageResource(especialidad.getIconoResId());

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Abriendo: " + especialidad.getNombre(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return especialidades.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icono;
        final TextView nombre, descripcion;

        public ViewHolder(View view) {
            super(view);
            icono = view.findViewById(R.id.iconEspecialidad);
            nombre = view.findViewById(R.id.nombreEspecialidad);
            descripcion = view.findViewById(R.id.tipoSolicitud);
        }
    }
}
