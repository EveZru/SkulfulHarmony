package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.Act_guitarra;
import com.example.skulfulharmony.Clase_Fundamentos;
import com.example.skulfulharmony.EscribirPartiturasAct;
import com.example.skulfulharmony.Home;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_clases;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class AdapterClasesOriginales extends RecyclerView.Adapter<AdapterClasesOriginales.ClaseViewHolder> implements Serializable {
    private Context context;
    private List<Curso> listaCursos;

    public AdapterClasesOriginales(Context context, List<Curso> listaCursos) {
        this.context = context;
        this.listaCursos = listaCursos;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new ClaseViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvNombreClase.setText(curso.getTitulo());

        holder.itemView.setOnClickListener(v -> {
            String tituloClase = curso.getTitulo();
            Intent intent = new Intent(context, Clase_Fundamentos.class);
            int imagenResId = R.drawable.loading; // Valor por defecto
            List<PreguntaCuestionario> listaDePreguntas = new ArrayList<>();

            if (tituloClase.equals("Clase 1")) {
                imagenResId = R.drawable.perfil_logo;
                listaDePreguntas.add(new PreguntaCuestionario("Pregunta 1 de Clase 1", Arrays.asList("A", "B", "C"), 0));
                listaDePreguntas.add(new PreguntaCuestionario("Pregunta 2 de Clase 1", Arrays.asList("X", "Y", "Z"), 1));
            } else if (tituloClase.equals("Clase 2")) {
                imagenResId = R.drawable.perfil_icono;
                listaDePreguntas.add(new PreguntaCuestionario("Pregunta A de Clase 2", Arrays.asList("1", "2", "3"), 2));
                listaDePreguntas.add(new PreguntaCuestionario("Pregunta B de Clase 2", Arrays.asList("T", "F"), 0));
            } else if (tituloClase.equals("Clase 3")) {
                imagenResId = R.drawable.loading;
                listaDePreguntas.add(new PreguntaCuestionario("¿Primera pregunta de la tercera?", Arrays.asList("Sí", "No"), 1));
                listaDePreguntas.add(new PreguntaCuestionario("¿Segunda?", Arrays.asList("Op1", "Op2", "Op3", "Op4"), 3));
            } else {
                imagenResId = R.drawable.loading;
                listaDePreguntas.add(new PreguntaCuestionario("Pregunta por defecto", Arrays.asList("Uno", "Dos"), 0));
            }

            intent.putExtra("imagen_resource", imagenResId);
            intent.putExtra("lista_preguntas", (Serializable) listaDePreguntas);
            intent.putExtra("nombre_curso", tituloClase); // Pasar el título para usarlo en Clase_Fundamentos
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class ClaseViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombreClase;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreClase = itemView.findViewById(R.id.txt_principal_verlista_ccc);
        }
    }
}