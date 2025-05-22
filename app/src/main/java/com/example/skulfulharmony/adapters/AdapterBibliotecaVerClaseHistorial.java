package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AdapterBibliotecaVerClaseHistorial extends RecyclerView.Adapter<AdapterBibliotecaVerClaseHistorial.ViewHolder> {

    private Context context;
    private List<Clase> historial = new ArrayList<>();
    private DbUser dbUser;
    private String correo;

    public AdapterBibliotecaVerClaseHistorial(Context context) {
        this.context = context;
        dbUser = new DbUser(this.context);
        correo = dbUser.getCorreoUser();

        cargarHistorial();
    }

    private void cargarHistorial() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference historialRef = db.collection("usuarios")
                .document(correo)
                .collection("historialClases")
                .document("clase");

        historialRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> historialMap = (List<Map<String, Object>>) documentSnapshot.get("historialClases");
                if (historialMap != null) {
                    for (Map<String, Object> datoMap : historialMap) {
                        String titulo = (String) datoMap.get("titulo");
                        String nombreCurso = (String) datoMap.get("nombreCurso");
                        String idCurso = (String) datoMap.get("idCurso");

                        // Obtener la lista de preguntas del cuestionario
                        List<PreguntaCuestionario> preguntas = new ArrayList<>();
                        List<Map<String, Object>> preguntasMap = (List<Map<String, Object>>) datoMap.get("preguntas");
                        if (preguntasMap != null) {
                            for (Map<String, Object> preguntaData : preguntasMap) {
                                String preguntaTexto = (String) preguntaData.get("pregunta");
                                List<String> respuestas = (List<String>) preguntaData.get("respuestas");
                                Long respuestaCorrecta = (Long) preguntaData.get("respuestaCorrecta");

                                preguntas.add(new PreguntaCuestionario(preguntaTexto, new ArrayList<>(respuestas), respuestaCorrecta != null ? respuestaCorrecta.intValue() : null));
                            }
                        }

                        // Obtener la imagen del curso desde Firestore usando idCurso
                        db.collection("cursos").document(idCurso).get().addOnSuccessListener(courseSnapshot -> {
                            if (courseSnapshot.exists()) {
                                String imagenCursoUrl = (String) courseSnapshot.get("imagenUrl");
                                historial.add(new Clase(titulo, nombreCurso, imagenCursoUrl, preguntas));
                                notifyDataSetChanged();
                            }
                        }).addOnFailureListener(e -> {
                            historial.add(new Clase(titulo, nombreCurso,null, preguntas)); // Si falla, imagen vacía
                            notifyDataSetChanged();
                        });
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Manejo de errores
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clase clase = historial.get(position);
        holder.titulo.setText(clase.getTitulo());
        //holder.fecha.setText(clase.getFechaCreacion().toString());
        if(clase.getImagenCurso() != null){
            Glide.with(context)
                    .load(clase.getImagenCurso())
                    .placeholder(R.drawable.img_defaultclass) // Imagen por defecto
                    .into(holder.imagen);
        }
    }

    @Override
    public int getItemCount() {
        return historial.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, fecha;
        ImageView imagen;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_textprincipal);
            fecha = itemView.findViewById(R.id.tv_textsegundo);
            imagen = itemView.findViewById(R.id.cardImage);
        }
    }
}
