package com.example.skulfulharmony.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.modooffline.ClaseFirebase;

import java.util.List;
import java.util.Set;

public class AdapterDescarga extends RecyclerView.Adapter<AdapterDescarga.ViewHolder> {

    public interface OnDescargaClickListener {
        void onDescargarClick(ClaseFirebase clase);
    }

    private List<ClaseFirebase> lista;
    private Set<String> clasesDescargadas;
    private OnDescargaClickListener listener;

    public AdapterDescarga(List<ClaseFirebase> lista, Set<String> clasesDescargadas, OnDescargaClickListener listener) {
        this.lista = lista;
        this.clasesDescargadas = clasesDescargadas;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        ImageView iconoDescarga;
        Button botonDescargar;

        public ViewHolder(View view) {
            super(view);
            titulo = view.findViewById(R.id.tituloClase);
            iconoDescarga = view.findViewById(R.id.iconoEstado);
            botonDescargar = view.findViewById(R.id.botonDescargar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase_descarga, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClaseFirebase clase = lista.get(position);
        holder.titulo.setText(clase.getTitulo());

        boolean descargada = clasesDescargadas.contains(clase.getTitulo());
        holder.iconoDescarga.setImageResource(descargada ? R.drawable.ic_check : R.drawable.ic_cloud_download);
        holder.botonDescargar.setEnabled(!descargada);

        holder.botonDescargar.setOnClickListener(v -> {
            if (!descargada) listener.onDescargarClick(clase);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}

