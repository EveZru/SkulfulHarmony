package com.example.skulfulharmony.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.List;

public class AdapterPreguntasEnClasesOriginales extends RecyclerView.Adapter<AdapterPreguntasEnClasesOriginales.ViewHolder> {
    private List<PreguntaCuestionario> preg;
    private LayoutInflater preginflador;
    private Context context;

    public AdapterPreguntasEnClasesOriginales(List<PreguntaCuestionario> itemlist, Context context) {
        this.preginflador = LayoutInflater.from(context);
        this.preg = itemlist;
        this.context=context;
    }

    @Override
    public int getItemCount() {return preg.size();}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = preginflador.inflate(R.layout.holder_preguntasinicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(preg.get(position));
    }

    public void setItems(List<PreguntaCuestionario> items) {
        preg = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pregunta;
        RadioGroup respuestas;

        ViewHolder(View itemView) {
            super(itemView);
            pregunta = itemView.findViewById(R.id.tv_preguntatexto);
            respuestas = itemView.findViewById(R.id.rg_opcionespreg);
        }

        void bindData(PreguntaCuestionario preguntaItem) {

        }
    }
}
