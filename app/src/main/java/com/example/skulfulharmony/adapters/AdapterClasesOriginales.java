package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.Clase_Fundamentos;
import com.example.skulfulharmony.EscribirPartiturasAct;
import com.example.skulfulharmony.EscribirPartiturasDo;
import com.example.skulfulharmony.EscribirPartiturasFa;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.io.Serializable;
import java.util.List;
public class AdapterClasesOriginales extends RecyclerView.Adapter<AdapterClasesOriginales.ClaseViewHolder> implements Serializable {
    //eve
    private Context context;
    private List<Curso> listaCursos;

    public AdapterClasesOriginales(Context context, List<Curso> listaCursos) {
        this.context = context;
        this.listaCursos = listaCursos;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new ClaseViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvNombreClase.setText(curso.getTitulo());

        holder.itemView.setOnClickListener(v -> {
            String tituloClase = curso.getTitulo();
            Intent intent = new Intent(context, Clase_Fundamentos.class);
            int imagenResId = R.drawable.loading; // Valor por defecto
          //  List<PreguntaCuestionario> listaDePreguntas = new ArrayList<>();

            if (tituloClase.equals("Clave sol") ) {
                // Navegación a la actividad de escribir partituras
                intent = new Intent(context, EscribirPartiturasAct.class);
            }else if (tituloClase.equals("Clave fa") ) {
                // Navegación a la actividad de escribir partituras
                intent = new Intent(context, EscribirPartiturasFa.class); // Reemplaza con el nombre real de tu Activity
                    }else if (tituloClase.equals("Clave do") ) {
                // Navegación a la actividad de escribir partituras
                intent = new Intent(context, EscribirPartiturasDo.class); // Reemplaza con el nombre real de tu Activity
            }

            else {
                imagenResId = R.drawable.loading;
           //     listaDePreguntas.add(new PreguntaCuestionario("Pregunta por defecto", Arrays.asList("Uno", "Dos"), 0));
            }
           // Log.d("AdapterClases", "Tamaño de listaDePreguntas antes de pasar: " + listaDePreguntas.size());

            intent.putExtra("imagen_resource", imagenResId);
           // intent.putExtra("lista_preguntas", (Serializable) listaDePreguntas);
            intent.putExtra("nombre_curso", tituloClase); // Pasar el título para usarlo en Clase_Fundamentos
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class ClaseViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombreClase;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreClase = itemView.findViewById(R.id.txt_principal_verlista_ccc);
        }
    }
}