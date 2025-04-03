package com.example.skulfulharmony.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skulfulharmony.ClasesOriginales;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

import kotlin.Unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class AdapterHomeVerCursos extends RecyclerView.Adapter<AdapterHomeVerCursos.CursoViewHolder> {
    private Context context;
    private List<Curso> listaCursos;


    // Constructor
    public AdapterHomeVerCursos(List<Curso> listaCursos, Context context) {
        this.listaCursos = listaCursos;
        this.context = context;
    }


    @Override
    public CursoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CursoViewHolder holder, int position) {
        // Obtenemos el curso de la lista
        Curso curso = listaCursos.get(position);
        // Asignamos el título del curso
        holder.tvTextPrincipal.setText(curso.getTitulo());
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    // ViewHolder
    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public TextView tvTextPrincipal;
        public TextView tvTextSegundo;

        public CursoViewHolder(View itemView) {
            super(itemView);
            // Referencias a las vistas del CardView
            cardImage = itemView.findViewById(R.id.cardImage);
            tvTextPrincipal = itemView.findViewById(R.id.tv_textprincipal);
            tvTextSegundo = itemView.findViewById(R.id.tv_textsegundo);
        }
    }
}
