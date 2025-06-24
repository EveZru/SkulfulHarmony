package com.example.skulfulharmony.adapters;

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

    public interface OnClaseClickListener {
        void onClick(ClaseFirebase clase);
    }

    private final List<ClaseFirebase> lista;
    private final OnClaseClickListener listener;

    public AdapterClasesLocales(List<ClaseFirebase> lista, OnClaseClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo_clase);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase_local, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClaseFirebase clase = lista.get(position);
        String titulo = clase.getTitulo();
        holder.tvTitulo.setText(titulo != null ? titulo : "Sin título");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(clase);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
