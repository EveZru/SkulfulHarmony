package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.modooffline.ClaseFirebase;

import java.util.List;

public class AdapterClasesLocales extends RecyclerView.Adapter<AdapterClasesLocales.ViewHolder> {

    private List<ClaseFirebase> lista;
    private OnItemClickListener listener; // Interfaz para manejar clicks

    // Interfaz para el click listener
    public interface OnItemClickListener {
        void onItemClick(ClaseFirebase clase);
    }

    // Constructor modificado para recibir el listener
    public AdapterClasesLocales(List<ClaseFirebase> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase_offline, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClaseFirebase clase = lista.get(position);
        holder.titulo.setText(clase.getTitulo());

        // Manejador de clicks delegado al listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(clase);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloClaseOffline);
        }
    }
}