package com.example.skulfulharmony.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;

import java.util.List;

public class AdapterOpcionRespuesta extends RecyclerView.Adapter<AdapterOpcionRespuesta.OpcionViewHolder> {

    private Context context;
    private List<String> opciones;
    private int respuestaCorrectaIndex;
    private boolean mostrarResultados;
    private int opcionSeleccionada = -1; // Para rastrear la opción seleccionada

    public AdapterOpcionRespuesta(Context context, List<String> opciones, int respuestaCorrectaIndex, boolean mostrarResultados) {
        this.context = context;
        this.opciones = opciones;
        this.respuestaCorrectaIndex = respuestaCorrectaIndex;
        this.mostrarResultados = mostrarResultados;
    }

    public void setMostrarResultados(boolean mostrarResultados) {
        this.mostrarResultados = mostrarResultados;
        notifyDataSetChanged();
    }

    public int getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    public void setOpcionSeleccionada(int opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OpcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_opciones, parent, false);
        return new OpcionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OpcionViewHolder holder, int position) {
        String opcion = opciones.get(position);
        holder.tvOpcion.setText(opcion);
        holder.cbOpcion.setChecked(position == opcionSeleccionada);

        if (mostrarResultados) {
            if (position == respuestaCorrectaIndex) {
                holder.cbOpcion.setChecked(true);
                holder.cbOpcion.setButtonTintList(context.getResources().getColorStateList(R.color.verde));
            } else if (position == opcionSeleccionada && position != respuestaCorrectaIndex) {
                holder.cbOpcion.setChecked(true);
                holder.cbOpcion.setButtonTintList(context.getResources().getColorStateList(android.R.color.holo_red_light));
            } else {
                holder.cbOpcion.setButtonTintList(context.getResources().getColorStateList(R.color.verde)); // Color por defecto
            }
        } else {
            holder.cbOpcion.setButtonTintList(context.getResources().getColorStateList(R.color.verde)); // Color por defecto
        }

        holder.itemView.setOnClickListener(v -> {
            if (!mostrarResultados) {
                int previousSelection = opcionSeleccionada;
                opcionSeleccionada = holder.getAdapterPosition();
                notifyItemChanged(previousSelection);
                notifyItemChanged(opcionSeleccionada);
            }
        });
    }

    @Override
    public int getItemCount() {
        return opciones.size();
    }

    public static class OpcionViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbOpcion;
        TextView tvOpcion;

        public OpcionViewHolder(@NonNull View itemView) {
            super(itemView);
            cbOpcion = itemView.findViewById(R.id.cb_correcta);
            tvOpcion = itemView.findViewById(R.id.et_opcrespuesta);
        }
    }
}