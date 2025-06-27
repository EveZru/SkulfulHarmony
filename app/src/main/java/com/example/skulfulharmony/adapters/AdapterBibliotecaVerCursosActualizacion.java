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
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterBibliotecaVerCursosActualizacion  extends RecyclerView.Adapter<AdapterBibliotecaVerCursosActualizacion.CursoViewHolder> {

    private List<Integer> seguidos;

    public AdapterBibliotecaVerCursosActualizacion(List<Integer> seguidos) {
        this.seguidos = (seguidos != null) ? seguidos : new ArrayList<>();
    }
    //  this.actualizaciones = (actualizaciones != null) ? actualizaciones : new ArrayList<>();

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verlista_clase_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Integer idCurso = seguidos.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cursos")
                .whereEqualTo("idCurso", idCurso)
                .limit(1)
                .get()
                .addOnSuccessListener(onQuerySnapshot -> {
                    if (!onQuerySnapshot.isEmpty()) {
                        DocumentSnapshot document = onQuerySnapshot.getDocuments().get(0);
                        Curso curso = document.toObject(Curso.class);
                        holder.nombreCurso.setText(curso.getTitulo());
                        if (curso.getFechaActualizacion() == null) {
                            holder.fechaActualizacion.setText("");
                        } else {
                            holder.fechaActualizacion.setText(curso.getFechaActualizacion().toDate().toString());
                        }
                        Glide.with(holder.itemView.getContext())
                                .load(curso.getImagen())
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.img_defaultclass)
                                .into(holder.imagenCurso);
                    }
                })
                .addOnFailureListener( e -> {
                    holder.nombreCurso.setText("Curso desconocido");
                    holder.fechaActualizacion.setText("");
                    Glide.with(holder.itemView.getContext())
                            .load(R.drawable.img_defaultclass)
                            .into(holder.imagenCurso);
                    Log.e("AdapterBibliotecaVerCursosActualizacion", "Error al obtener el curso: " + e.getMessage());
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), Ver_cursos.class);
            intent.putExtra("idCurso", idCurso);
            holder.itemView.getContext().startActivity(intent);
        });
        if (position == seguidos.size() - 1) {
            holder.itemView.setPadding(0, 0, 0, 100);
        }
    }

    @Override
    public int getItemCount() {
        return (seguidos != null) ? seguidos.size() : 0;
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

