package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.Ver_clases;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class AdapterClasesOriginales {
  /*  private Context context;
    private List<String> listaOpciones;
    private String tipoLista;

    public AdapterClasesOriginales(Context context, List<String> listaOpciones, String tipoLista) {
        this.context = context;
        this.listaOpciones = listaOpciones;
        this.tipoLista = tipoLista;
    }

    @NonNull
    @Override
    public OpcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new OpcionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OpcionViewHolder holder, int position) {
        String opcion = listaOpciones.get(position);
        holder.tvOpcion.setText(opcion);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Clic en: " + opcion, Toast.LENGTH_SHORT).show();
            Log.d("AdapterGenerico", "Clic en: " + opcion + ", Tipo de lista: " + tipoLista);

            if (tipoLista.equals("tres_opciones")) {
                if (opcion.equals("Opción 1 del grupo 1")) {
                    Intent intent = new Intent(context, Ver_clases.class);
                    context.startActivity(intent);
                } else if (opcion.equals("Opción 2 del grupo 1")) {
                    Intent intent = new Intent(context, Ver_clases.class);
                    context.startActivity(intent);
                } else if (opcion.equals("Opción 3 del grupo 1")) {
                    Intent intent = new Intent(context, Ver_clases.class);
                    context.startActivity(intent);
                }
            } else if (tipoLista.equals("once_opciones")) {
                if (opcion.equals("Opción 1 del grupo 2")) {
                    Intent intent = new Intent(context,Ver_clases.class);
                    context.startActivity(intent);
                } else if (opcion.equals("Opción 2 del grupo 2")) {
                    Intent intent = new Intent(context, Ver_clases.class);
                    context.startActivity(intent);
                }
                // ... y así sucesivamente para las 11 opciones
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaOpciones.size();
    }

    public static class OpcionViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOpcion;

        public OpcionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOpcion = itemView.findViewById(R.id.tvOpcionSimple); // Asegúrate de tener este ID
        }
    }*/
}
