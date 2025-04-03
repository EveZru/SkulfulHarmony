package com.example.skulfulharmony.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.Pregunta;

import java.util.List;

public class AdapterPreguntasDeClasesOriginales extends RecyclerView.Adapter<AdapterPreguntasDeClasesOriginales.PreguntaViewHolder> {

        private List<Pregunta> listaPreguntas;

        // Constructor
        public AdapterPreguntasDeClasesOriginales(List<Pregunta> listaPreguntas) {
            this.listaPreguntas = listaPreguntas;
        }

        // ViewHolder
        public static class PreguntaViewHolder extends RecyclerView.ViewHolder {
            TextView textViewPregunta;
            RadioGroup textViewFecha;

            public PreguntaViewHolder(View itemView) {
                super(itemView);

                textViewPregunta = itemView.findViewById(R.id.tv_preguntatexto);
                textViewFecha = itemView.findViewById(R.id.rg_opcionespreg);
            }
        }

        // Métodos del adaptador
        @Override
        public PreguntaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_preguntasinicio, parent, false);
            return new PreguntaViewHolder(vista);
        }

        @Override
        public void onBindViewHolder(PreguntaViewHolder holder, int position) {
            Pregunta pregunta = listaPreguntas.get(position);
            holder.textViewPregunta.setText(pregunta.getPregunta());
            holder.textViewFecha.setText(pregunta.getFecha() != null ? pregunta.getFecha().toString() : "Sin fecha");
            // Aquí puedes asignar más datos a las vistas correspondientes
        }

        @Override
        public int getItemCount() {
            return listaPreguntas.size();
        }
    }



