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

public class AdapterHomeVerClaseHistorial extends RecyclerView.Adapter<AdapterHomeVerClaseHistorial.ViewHolder> {

    List<Clase> clases;
    Context context;

    public AdapterHomeVerClaseHistorial(List<Clase> clases, Context context) {
        this.clases = clases;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterHomeVerClaseHistorial.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_curso_clase, parent, false);
        return new AdapterHomeVerClaseHistorial.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHomeVerClaseHistorial.ViewHolder holder, int position) {
        Clase clase = clases.get(position);
        holder.titulo.setText(clase.getTitulo());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("cursos");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            holder.subtitulo.setText("Usuario no autenticado");
        }else{
            docRef.whereEqualTo("idCurso", clase.getIdCurso()).
                    limit(1).get().addOnSuccessListener( queryDocumentSnapshots -> {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                Curso curso = doc.toObject(Curso.class);
                                holder.subtitulo.setText(curso.getTitulo());

                                Glide.with(context)
                                        .load(curso.getImagen())
                                        .placeholder(R.drawable.loading)
                                        .error(R.drawable.img_defaultclass)
                                        .into(holder.imagen);
                            }
                        }
                    }).addOnFailureListener( onFailureListener -> {
                        holder.subtitulo.setText("Error al obtener el curso");
                        Log.e("Firestore😶‍🌫️😶‍🌫️😶‍🌫️", "Error al obtener el curso", onFailureListener);
                    });

        }

        if(clase.getFechaAcceso() != null){
            Date fecha = clase.getFechaAcceso().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.descripcion.setText(sdf.format(fecha));

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Ver_clases.class);
                intent.putExtra("idClase", clase.getIdClase());
                intent.putExtra("idCurso", clase.getIdCurso());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView titulo;
        TextView subtitulo;
        TextView descripcion;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_textprincipal);
            subtitulo = itemView.findViewById(R.id.tv_complementario);
            descripcion = itemView.findViewById(R.id.tv_textsegundo);
            imagen = itemView.findViewById(R.id.cardImage);
        }
    }
}
