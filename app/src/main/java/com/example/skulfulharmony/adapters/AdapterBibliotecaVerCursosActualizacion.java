package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterBibliotecaVerCursosActualizacion extends RecyclerView.Adapter<AdapterBibliotecaVerCursosActualizacion.CursoViewHolder> {

    private List<Curso> listaCursos = new ArrayList<>();
    private Context context;

    public AdapterBibliotecaVerCursosActualizacion(List<Integer> idsCursosSeguidos, String orden) {
        cargarCursos(idsCursosSeguidos, orden);
    }

    private void cargarCursos(List<Integer> ids, String orden) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Integer id : ids) {
            db.collection("cursos")
                    .whereEqualTo("idCurso", id)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            DocumentSnapshot doc = snapshot.getDocuments().get(0);
                            Curso curso = doc.toObject(Curso.class);
                            if (curso != null) {
                                listaCursos.add(curso);

                                if (listaCursos.size() == ids.size()) {
                                    ordenarCursos(orden);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("AdapterCursosSeguidos", "Error cargando curso ID: " + id, e));
        }
    }

    private void ordenarCursos(String orden) {
        switch (orden) {
            case "alfabetico":
                Collections.sort(listaCursos, Comparator.comparing(Curso::getTitulo, String::compareToIgnoreCase));
                break;
            case "popularidad":
                Collections.sort(listaCursos, (a, b) -> Double.compare(b.getPopularidad(), a.getPopularidad()));
                break;
            case "recientes":
            default:
                Collections.sort(listaCursos, (a, b) -> {
                    if (a.getFechaActualizacion() == null || b.getFechaActualizacion() == null) return 0;
                    return b.getFechaActualizacion().compareTo(a.getFechaActualizacion());
                });
                break;
        }
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.nombreCurso.setText(curso.getTitulo());

        if (curso.getFechaActualizacion() != null) {
            holder.fechaActualizacion.setText(curso.getFechaActualizacion().toDate().toString());
        } else {
            holder.fechaActualizacion.setText("");
        }

        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_defaultclass)
                .into(holder.imagenCurso);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Ver_cursos.class);
            intent.putExtra("idCurso", curso.getId());
            context.startActivity(intent);
        });

        if (position == listaCursos.size() - 1) {
            holder.itemView.setPadding(0, 0, 0, 100);
        }
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreCurso, fechaActualizacion;
        ImageView imagenCurso;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreCurso = itemView.findViewById(R.id.txt_principal_verlista_ccc);
            fechaActualizacion = itemView.findViewById(R.id.txt_secundario_verlista_ccc);
            imagenCurso = itemView.findViewById(R.id.img_verlista_ccc);
        }
    }
}
