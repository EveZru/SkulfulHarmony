package com.example.skulfulharmony.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        ImageView imagen;

        public ViewHolder(View view) {
            super(view);
            titulo = view.findViewById(R.id.tv_titulo_curso_descargado);
            descripcion = view.findViewById(R.id.tv_descripcion_curso_descargado);
            imagen = view.findViewById(R.id.img_curso_descargado);
        }
    }

    @Override
    public AdapterCursosDescargados.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso_descargado, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterCursosDescargados.ViewHolder holder, int position) {
        Curso curso = lista.get(position);
        holder.titulo.setText(curso.getTitulo());
        holder.descripcion.setText(curso.getDescripcion());

        Glide.with(holder.itemView.getContext())
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.imagen);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(curso);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}