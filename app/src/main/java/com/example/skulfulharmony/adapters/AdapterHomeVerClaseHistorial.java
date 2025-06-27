package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button; // Importa Button
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.Biblioteca;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_clases;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterHomeVerClaseHistorial extends RecyclerView.Adapter<RecyclerView.ViewHolder> { // Cambia el tipo genérico a RecyclerView.ViewHolder

    private static final int VIEW_TYPE_CLASS = 0;
    private static final int VIEW_TYPE_BUTTON = 1;

    List<Clase> clases;
    Context context;
    boolean showSeeMoreButton; // Nueva variable para controlar la visibilidad del botón

    // --- CAMBIO 1: Constructor modificado ---
    public AdapterHomeVerClaseHistorial(List<Clase> clases, Context context, boolean showSeeMoreButton) {
        this.clases = clases;
        this.context = context;
        this.showSeeMoreButton = showSeeMoreButton;
    }

    @Override
    public int getItemViewType(int position) {
        if (showSeeMoreButton && position == clases.size()) {
            return VIEW_TYPE_BUTTON; // Es el último elemento y es el botón
        } else {
            return VIEW_TYPE_CLASS; // Es una clase normal
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // Cambia el tipo de retorno
        if (viewType == VIEW_TYPE_CLASS) {
            View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
            return new ClaseViewHolder(view);
        } else { // VIEW_TYPE_BUTTON
            // --- CAMBIO 2: Crea un nuevo layout para el botón "Ver más" ---
            // Necesitarás crear un layout `holder_ver_mas_button.xml`
            View view = LayoutInflater.from(context).inflate(R.layout.holder_boton_extra, parent, false);
            return new ButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { // Cambia el tipo de parámetro
        if (holder.getItemViewType() == VIEW_TYPE_CLASS) {
            ClaseViewHolder claseHolder = (ClaseViewHolder) holder;
            Clase clase = clases.get(position);
            claseHolder.titulo.setText(clase.getTitulo());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference docRef = db.collection("cursos");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                claseHolder.subtitulo.setText("Usuario no autenticado");
            } else {
                docRef.whereEqualTo("idCurso", clase.getIdCurso()).
                        limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Curso curso = doc.toObject(Curso.class);
                                    claseHolder.subtitulo.setText(curso.getTitulo());

                                    Glide.with(context)
                                            .load(curso.getImagen())
                                            .placeholder(R.drawable.loading)
                                            .error(R.drawable.img_defaultclass)
                                            .into(claseHolder.imagen);
                                }
                            }
                        }).addOnFailureListener(onFailureListener -> {
                            claseHolder.subtitulo.setText("Error al obtener el curso");
                            Log.e("Firestore😶‍🌫️😶‍🌫️😶‍🌫️", "Error al obtener el curso", onFailureListener);
                        });

            }

            if (clase.getFechaAcceso() != null) {
                Date fecha = clase.getFechaAcceso().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                claseHolder.descripcion.setText("\n" + sdf.format(fecha));

            }
            claseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Ver_clases.class);
                    intent.putExtra("idClase", clase.getIdClase());
                    intent.putExtra("idCurso", clase.getIdCurso());
                    context.startActivity(intent);
                }
            });
        } else { // VIEW_TYPE_BUTTON
            ButtonViewHolder buttonHolder = (ButtonViewHolder) holder;
            buttonHolder.btnVerMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // --- CAMBIO 3: Define la acción del botón "Ver más" ---
                    // Aquí inicias la nueva actividad
                    Toast.makeText(context, "Ver más clases", Toast.LENGTH_SHORT).show(); // Ejemplo
                     Intent intent = new Intent(context, Biblioteca.class);
                     context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // --- CAMBIO 4: Ajusta el conteo de elementos para incluir el botón ---
        return clases.size() + (showSeeMoreButton ? 1 : 0);
    }

    // --- CAMBIO 5: Crea un ViewHolder para el botón ---
    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button btnVerMas;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            btnVerMas = itemView.findViewById(R.id.btn_ver_mas); // Asegúrate de que este ID exista en holder_ver_mas_button.xml
        }
    }

    // --- CAMBIO 6: Renombra o usa el ViewHolder existente para las clases ---
    public static class ClaseViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView titulo;
        TextView subtitulo;
        TextView descripcion;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_textprincipal);
            subtitulo = itemView.findViewById(R.id.tv_complementario);
            descripcion = itemView.findViewById(R.id.tv_textsegundo);
            imagen = itemView.findViewById(R.id.cardImage);
            subtitulo.setTextSize(11);
        }
    }
}