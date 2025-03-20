package com.example.skulfulharmony.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterBibliotecaVerCursos extends RecyclerView.Adapter {

    private Context context;
    private List<Curso> seguidos = new ArrayList<>();
    private DbUser dbUser;
    private String correo;

    public AdapterBibliotecaVerCursos(Context context){
        this.context = context;
        dbUser = new DbUser(this.context);
        correo = dbUser.getCorreoUser();

        cargarSeguidos();
    }

    private void cargarSeguidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference seguidosRef = db.collection("usuario")
                .document("correo")
                .collection("cursosSeguidos")
                .document("curso");

        seguidosRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                
            }
        })
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
