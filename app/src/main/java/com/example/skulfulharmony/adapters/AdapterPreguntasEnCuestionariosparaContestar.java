package com.example.skulfulharmony.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.List;

public class AdapterPreguntasEnCuestionariosparaContestar extends RecyclerView.Adapter<AdapterPreguntasEnCuestionariosparaContestar.PreguntaViewHolder> {

    private Context context;
    private List<PreguntaCuestionario> listaPreguntas;
    private boolean mostrarResultados = false;
    private List<PreguntaCuestionario> preguntasIncorrectas;

    public AdapterPreguntasEnCuestionariosparaContestar(Context context, List<PreguntaCuestionario> listaPreguntas) {
        this.context = context;
        this.listaPreguntas = listaPreguntas;
        this.preguntasIncorrectas = new java.util.LinkedList<>();
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

        AdapterOpcionRespuesta opcionAdapter = new AdapterOpcionRespuesta(context, pregunta.getRespuestas(), pregunta.getRespuestaCorrecta(), mostrarResultados);
        holder.rvOpciones.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOpciones.setAdapter(opcionAdapter);
    }

    @Override
    public int getItemCount() {
        return listaPreguntas.size();
    }

    public class PreguntaViewHolder extends RecyclerView.ViewHolder {
        TextView tvPregunta;
        RecyclerView rvOpciones;

        public PreguntaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPregunta = itemView.findViewById(R.id.tv_pregunta);
            rvOpciones = itemView.findViewById(R.id.rv_opciones);
        }
    }

    public void comprobarRespuestas() {
        mostrarResultados = true;
        preguntasIncorrectas.clear();
        for (int i = 0; i < listaPreguntas.size(); i++) {
            PreguntaCuestionario pregunta = listaPreguntas.get(i);
            AdapterOpcionRespuesta adapter = (AdapterOpcionRespuesta) ((RecyclerView) ((View) itemViewList.get(i)).findViewById(R.id.rv_opciones)).getAdapter();
            if (adapter != null && adapter.getOpcionSeleccionada() != pregunta.getRespuestaCorrecta()) {
                preguntasIncorrectas.add(pregunta);
            }
        }
        notifyDataSetChanged();
    }

    private List<View> itemViewList = new java.util.ArrayList<>();

    @Override
    public void onViewAttachedToWindow(@NonNull PreguntaViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        itemViewList.add(holder.itemView);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PreguntaViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        itemViewList.remove(holder.itemView);
    }
}