package com.example.skulfulharmony.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.ArrayList;
import java.util.List;

public class AdapterBibliotecaVerCursosHistorial extends RecyclerView.Adapter<AdapterBibliotecaVerCursosHistorial.CursoViewHolder> {
    List<Curso> cursos;

    public AdapterBibliotecaVerCursosHistorial(List<Curso> cursos) {
        if (cursos != null) {
            this.cursos = cursos;
        } else {
            this.cursos = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public AdapterBibliotecaVerCursosHistorial.CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new CursoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBibliotecaVerCursosHistorial.CursoViewHolder holder, int position) {
        Curso curso = cursos.get(position);
        holder.nombreCurso.setText(curso.getTitulo());
        if (curso.getFechaAcceso() == null) {
            holder.fechaAcceso.setText("");
        }else {
            holder.fechaAcceso.setText(curso.getFechaAcceso().toDate().toString());
        }
        Glide.with(holder.imagenCurso.getContext())
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.imagenCurso);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), Ver_cursos.class);
            intent.putExtra("idCurso", curso.getId());
            holder.itemView.getContext().startActivity(intent);
        });
        if (position == cursos.size() - 1) {
            holder.itemView.setPadding(0, 0, 0, 100);
        }
        if (position == 0) {
            holder.itemView.setPadding(0, 100, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public class CursoViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenCurso;
        TextView nombreCurso, fechaAcceso;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenCurso = itemView.findViewById(R.id.img_verlista_ccc);
            nombreCurso = itemView.findViewById(R.id.txt_principal_verlista_ccc);
            fechaAcceso = itemView.findViewById(R.id.txt_secundario_verlista_ccc);
        }
    }
}
