package com.example.skulfulharmony.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.Home;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class AdapterPopulares extends RecyclerView.Adapter<AdapterPopulares.CursoViewHolder> {

    private List<Curso> listaCursos;

    public AdapterPopulares(List<Curso> listaCursos) {
        this.listaCursos = listaCursos;
    }
    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_populares, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.nombre.setText(curso.getTitulo());
        holder.descripcion.setText(curso.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, descripcion;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tv_NombreCurso);
            descripcion = itemView.findViewById(R.id.tv_DescripcionCurso);
        }
    }
}
