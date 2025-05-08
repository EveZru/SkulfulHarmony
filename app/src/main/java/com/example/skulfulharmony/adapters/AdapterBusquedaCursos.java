package com.example.skulfulharmony.adapters;

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

public class AdapterBusquedaCursos extends RecyclerView.Adapter<AdapterBusquedaCursos.CursoViewHolder> {

    private List<Curso> cursos;
    private OnItemClickListener onItemClickListener;

    public AdapterBusquedaCursos(List<Curso> cursos, OnItemClickListener listener) {
        this.cursos = cursos;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_curso_clase, parent, false); // Usa el layout adecuado para los cursos
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = cursos.get(position);
        holder.tituloCurso.setText(curso.getTitulo());
        holder.descripcionCurso.setText(curso.getDescripcion());

        // Usamos Glide para cargar la imagen (si existe una URL de imagen)
        Glide.with(holder.itemView)
                .load(curso.getImagen())  // Aquí puedes cargar la imagen del curso
                .placeholder(R.drawable.logo_sh)  // Imagen de curso predeterminada
                .into(holder.imagenCurso);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(curso);  // Pasa el curso al hacer clic
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView tituloCurso, descripcionCurso;
        ImageView imagenCurso;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenCurso = itemView.findViewById(R.id.iv_perfil); // Usa la imagen de tu layout de cursos
            tituloCurso = itemView.findViewById(R.id.tv_nombre_usuario); // Usamos el TextView para el nombre
            descripcionCurso = itemView.findViewById(R.id.tv_descripcion_usuario); // Usamos el TextView para la descripción
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Curso curso); // Definimos qué hacer al hacer clic en un curso
    }
}
