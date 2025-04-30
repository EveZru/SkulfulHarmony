package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.Act_pianoAcordes;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

public class AdapterHomeVerCursosOriginales extends RecyclerView.Adapter<AdapterHomeVerCursosOriginales.CursoViewHolder> {
    //ya que los cursos origunales no se cargan por medio de firebase cre que seria mejor que tubiera un adapter aparte por lo que lo cree por cuestiones de sueño no los termine de vincular porque me daban errores asi que lo deje con lo de firebase por lo que no funciona por que pues no existe jsjsj jiji
    private Context context;
    private List<Curso> listaCursos;

    public AdapterHomeVerCursosOriginales(Context context, List<Curso> listaCursos) {
        this.context = context;
        this.listaCursos = listaCursos;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvTextPrincipal.setText(curso.getTitulo());

        // Opcional: Establecer un icono diferente si lo tienes en tu objeto CursoLocal


        holder.itemView.setOnClickListener(v -> {


        });
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public TextView tvTextPrincipal;
        public TextView tvTextSegundo;

        public CursoViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            tvTextPrincipal = itemView.findViewById(R.id.tv_textprincipal);
            tvTextSegundo = itemView.findViewById(R.id.tv_textsegundo);

        }
    }
}