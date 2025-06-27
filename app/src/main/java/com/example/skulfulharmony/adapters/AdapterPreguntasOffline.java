package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.ArrayList;
import java.util.List;

public class AdapterPreguntasOffline extends RecyclerView.Adapter<AdapterPreguntasOffline.ViewHolder> {

    private final List<PreguntaCuestionario> listaPreguntas;

    public AdapterPreguntasOffline(List<PreguntaCuestionario> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pregunta_offline, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PreguntaCuestionario pregunta = listaPreguntas.get(position);
        Context context = holder.itemView.getContext();

        holder.tvPregunta.setText((position + 1) + ". " + pregunta.getPregunta());
        holder.rgOpciones.removeAllViews();

        for (int i = 0; i < pregunta.getRespuestas().size(); i++) {
            RadioButton rb = new RadioButton(context);
            rb.setText(pregunta.getRespuestas().get(i));
            rb.setId(View.generateViewId());

            // Si el usuario ya había respondido, seleccionamos la opción
            if (pregunta.getRespuestaUsuario() != null && pregunta.getRespuestaUsuario() == i) {
                rb.setChecked(true);
            }

            final int indexRespuesta = i;
            rb.setOnClickListener(v -> pregunta.setRespuestaUsuario(indexRespuesta));

            holder.rgOpciones.addView(rb);
        }
    }

    @Override
    public int getItemCount() {
        return listaPreguntas.size();
    }

    public List<Integer> getRespuestas() {
        List<Integer> respuestas = new ArrayList<>();
        for (PreguntaCuestionario p : listaPreguntas) {
            respuestas.add(p.getRespuestaUsuario() != null ? p.getRespuestaUsuario() : -1);
        }
        return respuestas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPregunta;
        RadioGroup rgOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPregunta = itemView.findViewById(R.id.tv_pregunta_offline);
            rgOpciones = itemView.findViewById(R.id.rg_respuestas_offline);
        }
    }
}