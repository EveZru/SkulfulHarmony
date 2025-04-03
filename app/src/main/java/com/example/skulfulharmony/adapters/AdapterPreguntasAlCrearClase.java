package com.example.skulfulharmony.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import java.util.ArrayList;
public class AdapterPreguntasAlCrearClase extends RecyclerView.Adapter<AdapterPreguntasAlCrearClase.PreguntaViewHolder> {

    private ArrayList<PreguntaCuestionario> listaPreguntas;

    // Constructor del Adapter
    public AdapterPreguntasAlCrearClase(ArrayList<PreguntaCuestionario> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    @NonNull
    @Override
    public PreguntaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_opciones, parent, false);
        return new PreguntaViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PreguntaViewHolder holder, int position) {
        PreguntaCuestionario preguntaCuestionario;
        PreguntaCuestionario pregunta =  listaPreguntas.get(position);
        holder.etPregunta.setText(pregunta.getPregunta());
        holder.ll_respuestas_espacio.removeAllViews();

        //  el layout tiene el EditText antes de inflarlo en el RecyclerView
        View testView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.holder_precuntasalcrearclase, null);
        EditText testEditText = testView.findViewById(R.id.et_respuesta);

        if (testEditText == null) {
            Log.e("Adapter", "Error: et_respuesta no se encontró en holder_precuntasalcrearclase.xml");
            Toast.makeText(holder.itemView.getContext(), "Error: et_respuesta no está en el layout", Toast.LENGTH_LONG).show();
            return;
        } else {
            Log.i("Adapter", " et_respuesta encontrado correctamente en holder_precuntasalcrearclase.xml");
        }

        // Iterar sobre las respuestas y agregarlas dinámicamente
        for (String respuesta : pregunta.getRespuestas()) {
            try {
                // Infla el layout de la respuesta
                View itemRespuesta = LayoutInflater.from(holder.itemView.getContext())
                        .inflate(R.layout.holder_precuntasalcrearclase, holder.ll_respuestas_espacio, false);

                // Buscar el EditText dentro de la vista inflada
                EditText editText = itemRespuesta.findViewById(R.id.et_respuesta);

                // Verifica si el EditText es nulo antes de asignarle un valor
                if (editText == null) {
                    throw new NullPointerException("No se encontró et_respuesta en el layout holder_precuntasalcrearclase");
                }

                // Asignar el texto a la respuesta
                editText.setText(respuesta);

                // Agregar la vista inflada al LinearLayout
                holder.ll_respuestas_espacio.addView(itemRespuesta);
            } catch (Exception e) {
                // Mostrar error en un Toast y en Logcat
                Toast.makeText(holder.itemView.getContext(), "Error en respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Adapter", "Error en onBindViewHolder: ", e);
            }
        }
    }



    @Override
    public int getItemCount() {
        return listaPreguntas.size();
    }

    public static class PreguntaViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_respuestas_espacio;
        EditText etPregunta;

        public PreguntaViewHolder(@NonNull View itemView) {
            super(itemView);
            etPregunta = itemView.findViewById(R.id.et_pregunta);
            ll_respuestas_espacio = itemView.findViewById(R.id.ll_respuestas_espacio);
        }
    }
}
