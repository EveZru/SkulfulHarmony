package com.example.skulfulharmony.adapters;

import android.content.Context;
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
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterBibliotecaVerCursosActualizacion extends RecyclerView.Adapter<AdapterBibliotecaVerCursosActualizacion.CursoViewHolder> {

    private Context context;
    private List<Curso> seguidos = new ArrayList<>();
    private DbUser dbUser;
    private String correo;

    public AdapterBibliotecaVerCursosActualizacion(Context context) {
        this.context = context;
        dbUser = new DbUser(this.context);
        correo = dbUser.getCorreoUser();
        cargarSeguidos();
    }

    private void cargarSeguidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuario")
                .document(correo)
                .collection("cursosSeguidos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    seguidos.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String idCurso = document.getId();
                        String nombreCurso = document.getString("nombreCurso");
                        String imagenUrl = document.getString("imagenCurso");

                        Timestamp timestamp = document.getTimestamp("fechaActualizacion");
                        Date fechaActualizacion = timestamp != null ? timestamp.toDate() : null;


                        seguidos.add(new Curso(Integer.parseInt(idCurso), nombreCurso, imagenUrl, fechaActualizacion));
                    }
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores si es necesario
                });
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = seguidos.get(position);

        holder.nombreCurso.setText(curso.getTitulo());
        holder.fechaActualizacion.setText(""+curso.getCalificacion().getPuntuacion());

        // Cargar imagen con Glide
        Glide.with(context)
                .load(curso.getImagen())
                .placeholder(R.drawable.loading) // Imagen de carga
                .error(R.drawable.img_defaultclass) // Imagen en caso de error
                .into(holder.imagenCurso);
    }

    @Override
    public int getItemCount() {
        return seguidos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreCurso, fechaActualizacion;
        ImageView imagenCurso;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreCurso = itemView.findViewById(R.id.tv_textprincipal);
            fechaActualizacion = itemView.findViewById(R.id.tv_textsegundo);
            imagenCurso = itemView.findViewById(R.id.cardImage);
        }
    }
}
