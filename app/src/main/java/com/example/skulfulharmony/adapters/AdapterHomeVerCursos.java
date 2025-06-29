package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterHomeVerCursos extends RecyclerView.Adapter<AdapterHomeVerCursos.CursoViewHolder> {
    private Context context;
    private List<Curso> listaCursos;

    public AdapterHomeVerCursos(List<Curso> listaCursos, Context context) {
        this.listaCursos = listaCursos;
        this.context = context;
    }

    @Override
    public CursoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);

        if (curso == null) {
            Log.e("AdapterHomeVerCursos", "Curso en posición " + position + " es null");
            return;
        }

        // Título del curso
        holder.tvTextPrincipal.setText(curso.getTitulo());
        holder.tvTextComplementario.setText(curso.getDescripcion());

        // Mostrar el nombre del autor
        if (curso.getCreador() != null && !curso.getCreador().isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("usuarios");
            usersRef.whereEqualTo("correo", curso.getCreador())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String nombre = queryDocumentSnapshots.getDocuments().get(0).getString("nombre");
                            holder.tvTextSegundo.setText("Publicado por: " + nombre);
                        } else {
                            holder.tvTextSegundo.setText("Autor desconocido");
                            Log.w("Firebase", "No se encontró autor con correo: " + curso.getCreador());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error al obtener el nombre del autor", e);
                        holder.tvTextSegundo.setText("Autor desconocido");
                    });
        } else {
            holder.tvTextSegundo.setText("Autor desconocido");
        }

        // Imagen con Glide
        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.cardImage);

        Log.d("AdapterHomeVerCursos", "Curso cargado: " + curso.getTitulo());

        // Click para abrir actividad de clases
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Ver_cursos.class);
            intent.putExtra("idCurso", curso.getId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public TextView tvTextPrincipal;
        public TextView tvTextSegundo;
        public TextView tvTextComplementario;

        public CursoViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            tvTextPrincipal = itemView.findViewById(R.id.tv_textprincipal);
            tvTextSegundo = itemView.findViewById(R.id.tv_textsegundo);
            tvTextComplementario = itemView.findViewById(R.id.tv_complementario);
        }
    }
}