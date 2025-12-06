package com.example.medicalshift;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PreguntasFrecuentesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_frecuentes);

        RecyclerView recyclerPreguntas = findViewById(R.id.recyclerPreguntasFrecuentes);
        recyclerPreguntas.setLayoutManager(new LinearLayoutManager(this));

        List<Pregunta> preguntas = new ArrayList<>();
        preguntas.add(new Pregunta("¿Cómo solicito una autorización?", "Podés solicitarla desde la sección Gestiones, opción Autorizaciones Previas. Recordá tener a mano el pedido médico."));
        preguntas.add(new Pregunta("¿Dónde veo el estado de mi gestión?", "Todas tus gestiones, tanto las iniciadas desde la app como las presenciales, las podés ver en la sección Gestiones del menú principal."));
        preguntas.add(new Pregunta("¿Cómo pido un reintegro?", "Desde Gestiones -> Reintegros. Necesitarás la factura o comprobante fiscal y el pedido médico correspondiente."));

        PreguntasAdapter adapter = new PreguntasAdapter(preguntas);
        recyclerPreguntas.setAdapter(adapter);
    }

    // --- CLASES INTERNAS Y ADAPTADOR ---
    private static class Pregunta {
        final String pregunta;
        final String respuesta;
        boolean isExpanded = false;

        public Pregunta(String pregunta, String respuesta) {
            this.pregunta = pregunta;
            this.respuesta = respuesta;
        }
    }

    private static class PreguntasAdapter extends RecyclerView.Adapter<PreguntasAdapter.ViewHolder> {
        private final List<Pregunta> preguntas;

        public PreguntasAdapter(List<Pregunta> preguntas) {
            this.preguntas = preguntas;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pregunta, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pregunta pregunta = preguntas.get(position);
            holder.preguntaText.setText(pregunta.pregunta);
            holder.respuestaText.setText(pregunta.respuesta);

            holder.respuestaText.setVisibility(pregunta.isExpanded ? View.VISIBLE : View.GONE);
            holder.arrowIcon.setRotation(pregunta.isExpanded ? 180f : 0f);

            holder.preguntaContainer.setOnClickListener(v -> {
                pregunta.isExpanded = !pregunta.isExpanded;
                notifyItemChanged(position);
            });
        }

        @Override public int getItemCount() { return preguntas.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView preguntaText, respuestaText;
            final ImageView arrowIcon;
            final View preguntaContainer;

            public ViewHolder(View view) {
                super(view);
                preguntaText = view.findViewById(R.id.pregunta_text);
                respuestaText = view.findViewById(R.id.respuesta_text);
                arrowIcon = view.findViewById(R.id.arrow_icon);
                preguntaContainer = view.findViewById(R.id.pregunta_container);
            }
        }
    }
}
