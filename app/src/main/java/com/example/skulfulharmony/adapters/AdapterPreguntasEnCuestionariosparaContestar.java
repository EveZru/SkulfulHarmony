package com.example.skulfulharmony.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterPreguntasEnCuestionariosparaContestar extends RecyclerView.Adapter<AdapterPreguntasEnCuestionariosparaContestar.PreguntaViewHolder> {
    private Context context;
    private List<PreguntaCuestionario> listaPreguntas;
    private boolean mostrarResultados = false;
    private List<PreguntaCuestionario> preguntasIncorrectas;
    private List<Integer> respuestasSeleccionadas; // Para guardar la selección de cada pregunta

    public AdapterPreguntasEnCuestionariosparaContestar(Context context, List<PreguntaCuestionario> listaPreguntas) {
        this.context = context;
        this.listaPreguntas = listaPreguntas;
        this.preguntasIncorrectas = new ArrayList<>();
        this.respuestasSeleccionadas = new ArrayList<>(listaPreguntas.size());
        for (int i = 0; i < listaPreguntas.size(); i++) {
            this.respuestasSeleccionadas.add(-1); // Inicializar sin selección (-1)
        }
    }

    public void setMostrarResultados(boolean mostrarResultados) {
        this.mostrarResultados = mostrarResultados;
        notifyDataSetChanged();
    }

    public List<PreguntaCuestionario> getPreguntasIncorrectas() {
        return preguntasIncorrectas;
    }

    @NonNull
    @Override
    public PreguntaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_preguntas_cuestionario, parent, false);
        return new PreguntaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreguntaViewHolder holder, int position) {
        PreguntaCuestionario pregunta = listaPreguntas.get(position);
        holder.tvPregunta.setText(pregunta.getPregunta());

        holder.rgOpciones.removeAllViews(); // Limpiar RadioButtons anteriores

        for (int i = 0; i < pregunta.getRespuestas().size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(pregunta.getRespuestas().get(i));
            radioButton.setId(i); // Usar el índice como ID
            radioButton.setButtonTintList(ContextCompat.getColorStateList(holder.itemView.getContext(),R.color.radiobutton_tint));
            holder.rgOpciones.addView(radioButton);
        }

        holder.rgOpciones.setOnCheckedChangeListener((group, checkedId) -> {
            respuestasSeleccionadas.set(position, checkedId);
        });

        if (mostrarResultados) {
            // Aquí puedes marcar la respuesta correcta en verde y las incorrectas en rojo
            for (int i = 0; i < pregunta.getRespuestas().size(); i++) {
                RadioButton radioButton = (RadioButton) holder.rgOpciones.getChildAt(i);
                radioButton.setEnabled(false); // Deshabilitar la selección

                if (i == pregunta.getRespuestaCorrecta()) {
                    radioButton.setTextColor(context.getResources().getColor(R.color.verde));
                } else if (respuestasSeleccionadas.get(position) == i && i != pregunta.getRespuestaCorrecta()) {
                    radioButton.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                } else {
                    radioButton.setTextColor(context.getResources().getColor(android.R.color.black)); // Color por defecto
                }
            }
        } else {
            // Resetear el color de los RadioButtons al mostrar las preguntas
            for (int i = 0; i < pregunta.getRespuestas().size(); i++) {
                RadioButton radioButton = (RadioButton) holder.rgOpciones.getChildAt(i);
                radioButton.setEnabled(true);
                radioButton.setTextColor(context.getResources().getColor(android.R.color.black));
            }
            holder.rgOpciones.clearCheck(); // Asegurar que no haya selección residual al volver a mostrar
            if (respuestasSeleccionadas.get(position) != -1) {
                RadioButton selectedRadioButton = (RadioButton) holder.rgOpciones.getChildAt(respuestasSeleccionadas.get(position));
                if (selectedRadioButton != null) {
                    selectedRadioButton.setChecked(true);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaPreguntas.size();
    }

    public class PreguntaViewHolder extends RecyclerView.ViewHolder {
        TextView tvPregunta;
        RadioGroup rgOpciones;

        public PreguntaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPregunta = itemView.findViewById(R.id.tv_pregunta);
            rgOpciones = itemView.findViewById(R.id.rg_opciones);
        }
    }

    public void comprobarRespuestas() {
        mostrarResultados = true;
        preguntasIncorrectas.clear();
        for (int i = 0; i < listaPreguntas.size(); i++) {
            int respuestaSeleccionada = respuestasSeleccionadas.get(i);
            if (respuestaSeleccionada != listaPreguntas.get(i).getRespuestaCorrecta()) {
                preguntasIncorrectas.add(listaPreguntas.get(i));
            }
        }
        notifyDataSetChanged();
    }
}