package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

public class AdapterCursosUsuario extends RecyclerView.Adapter<AdapterCursosUsuario.CursoViewHolder> {

    private final Context context;
    private final List<Curso> cursos;

    public AdapterCursosUsuario(Context context, List<Curso> cursos) {
        this.context = context;
        this.cursos = cursos;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = cursos.get(position);

        holder.tvTitulo.setText(curso.getTitulo() != null ? curso.getTitulo() : "Sin título");
        holder.tvDescripcion.setText(curso.getDescripcion() != null ? curso.getDescripcion() : "");

        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.ivImagen);
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvCalificacion;
        ImageView ivImagen;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_textprincipal);
            tvDescripcion = itemView.findViewById(R.id.tv_textsegundo);
            ivImagen = itemView.findViewById(R.id.cardImage);
        }
    }
}