package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.ArrayList;
import java.util.List;

public class AdapterPreguntasEnVerClase extends RecyclerView.Adapter<AdapterPreguntasEnVerClase.ViewHolder> {

    Context context;
    List<PreguntaCuestionario> preguntas = new ArrayList<>();
    List<Integer> respuestasSeleccionadas = new ArrayList<>();

    public AdapterPreguntasEnVerClase( List<PreguntaCuestionario> preguntas, Context context) {
        this.context = context;
        this.preguntas = preguntas;
        for (int i = 0; i < preguntas.size(); i++) {
            respuestasSeleccionadas.add(-1); // -1 indica "sin respuesta"
        }
    }

    @NonNull
    @Override
    public AdapterPreguntasEnVerClase.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_listadepreguntas_verclase, parent, false);
        return new AdapterPreguntasEnVerClase.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPreguntasEnVerClase.ViewHolder holder, int position) {
        PreguntaCuestionario pregunta = preguntas.get(position);
        holder.pregunta.setText(pregunta.getPregunta());

        holder.respuestas.removeAllViews();

        for (String respuesta : pregunta.getRespuestas()) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(respuesta);
            radioButton.setTextColor(context.getResources().getColor(R.color.white));
            radioButton.setTextSize(15);
            radioButton.setButtonTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.radiobutton_tint));
            radioButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gray2));
            holder.respuestas.addView(radioButton);

        }

        holder.respuestas.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = holder.respuestas.findViewById(i);
            if (radioButton != null) {
                int radioButtonIndex = holder.respuestas.indexOfChild(radioButton);
                respuestasSeleccionadas.set(position, radioButtonIndex);
            }
        });

    }

    @Override
    public int getItemCount() {
        return preguntas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pregunta;
        RadioGroup respuestas;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pregunta = itemView.findViewById(R.id.txt_pregunta_verpregunta);
            respuestas = itemView.findViewById(R.id.radioGroupRespuestas_verpregunta);
        }
    }

    public List<Integer> getRespuestas() {
        return respuestasSeleccionadas;
    }
}
