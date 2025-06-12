package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.VerClaseOffline;
import com.example.skulfulharmony.modooffline.ClaseFirebase;

import java.io.File;
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
        ImageView imgVista;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo_clase);
            imgVista = itemView.findViewById(R.id.img_clase_local);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase_local, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClaseFirebase clase = lista.get(position);
        holder.tvTitulo.setText(clase.getTitulo());

        File imagenLocal = new File(clase.getImagenUrl());
        if (imagenLocal.exists()) {
            Glide.with(holder.itemView.getContext()).load(imagenLocal).into(holder.imgVista);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(clase);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}