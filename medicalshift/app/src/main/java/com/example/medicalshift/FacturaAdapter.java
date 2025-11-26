package com.example.medicalshift;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class FacturaAdapter extends RecyclerView.Adapter<FacturaAdapter.ViewHolder> {
    private final List<Factura> facturas;

    public FacturaAdapter(List<Factura> facturas) {
        this.facturas = facturas;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_factura, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Factura factura = facturas.get(position);
        holder.periodo.setText("Período: " + factura.periodo);
        holder.monto.setText(String.format(Locale.getDefault(), "Monto: $%.2f", factura.monto));
        holder.estado.setText("Estado: " + factura.estado);

        if (factura.estado.equals("Pendiente")) {
            holder.btnPagar.setVisibility(View.VISIBLE);
            holder.btnPagar.setOnClickListener(v -> {
                // Crear el Intent para abrir una URL
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mercadopago.com.ar"));
                holder.itemView.getContext().startActivity(browserIntent);
            });
        } else {
            holder.btnPagar.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount() { return facturas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView periodo, monto, estado;
        final MaterialButton btnPagar;
        public ViewHolder(View view) {
            super(view);
            periodo = view.findViewById(R.id.facturaPeriodo);
            monto = view.findViewById(R.id.facturaMonto);
            estado = view.findViewById(R.id.facturaEstado);
            btnPagar = view.findViewById(R.id.btnPagar);
        }
    }
}