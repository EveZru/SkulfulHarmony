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

public class AdapterCursosDescargados extends RecyclerView.Adapter<AdapterCursosDescargados.ViewHolder> {

    public interface OnCursoClickListener {
        void onClick(Curso curso);
    }

    private List<Curso> lista;
    private OnCursoClickListener listener;

    public AdapterCursosDescargados(List<Curso> lista, OnCursoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso_descargado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Curso curso = lista.get(position);
        holder.titulo.setText(curso.getTitulo());
        holder.descripcion.setText(curso.getDescripcion());

        String imagenUrl = curso.getImagen();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagenUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.img_defaultclass)
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.img_defaultclass);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(curso));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_titulo_curso_descargado);
            descripcion = itemView.findViewById(R.id.tv_descripcion_curso_descargado);
            imagen = itemView.findViewById(R.id.img_curso_descargado);
        }
    }
}