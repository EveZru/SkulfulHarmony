package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterAdminVerClasesDenunciadas;
import com.example.skulfulharmony.adapters.AdapterAdminVerComentarioComoAdministrador;
import com.example.skulfulharmony.adapters.AdapterAdminVerCursosDenunciados;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class admi_denuncias extends AppCompatActivity {

    Button btn_vercursos_verdenuncias;
    Button btn_verclases_verdenuncias;
    Button btn_vercomentarios_verdenuncias;
    RecyclerView recycler_demandas;
    BottomNavigationView barra_navegacion1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admi_denuncias);

        btn_vercursos_verdenuncias = findViewById(R.id.btn_vercursos_verdenuncias);
        btn_verclases_verdenuncias = findViewById(R.id.btn_verclases_verdenuncias);
        btn_vercomentarios_verdenuncias = findViewById(R.id.btn_vercomentarios_verdenuncias);
        recycler_demandas = findViewById(R.id.recycler_demandas);
        barra_navegacion1 = findViewById(R.id.barra_navegacionadmi);

        btn_vercursos_verdenuncias.setOnClickListener(v -> {
            List<Denuncia> denunciasCursos = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("denuncias")
                    .whereEqualTo("idComentario", -1)
                    .whereEqualTo("idClase", -1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Denuncia denuncia = document.toObject(Denuncia.class);
                            if (denuncia != null) {
                                denunciasCursos.add(denuncia);
                            }
                        }
                        recycler_demandas.setLayoutManager(new LinearLayoutManager(admi_denuncias.this, LinearLayoutManager.VERTICAL, false));
                        recycler_demandas.setAdapter(new AdapterAdminVerCursosDenunciados(denunciasCursos));
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error", "Error al obtener denuncias de cursos" + e, e);
                    });
        });

        btn_verclases_verdenuncias.setOnClickListener(view -> {
            List<Denuncia> denunciasClases = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("denuncias")
                    .whereEqualTo("idComentario",-1)
                    .whereNotEqualTo("idClase", -1)
                    .get()
                    .addOnSuccessListener( QueryDocumentSnapshot -> {
                       if(!QueryDocumentSnapshot.isEmpty()){
                           for(DocumentSnapshot document : QueryDocumentSnapshot){
                               Denuncia denuncia = document.toObject(Denuncia.class);
                               if(denuncia != null){
                                   denunciasClases.add(denuncia);
                               }
                           }
                           recycler_demandas.setLayoutManager(new LinearLayoutManager(admi_denuncias.this, LinearLayoutManager.VERTICAL, false));
                           recycler_demandas.setAdapter(new AdapterAdminVerClasesDenunciadas(denunciasClases));
                       }
                    })
                    .addOnFailureListener( e -> {
                        Log.e("Error", "Error al obtener denuncias de clases" + e, e);
                    });
        });

        btn_vercomentarios_verdenuncias.setOnClickListener(view -> {
            List<Denuncia> denunciasComentarios = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("denuncias")
                    .whereNotEqualTo("idComentario",-1)
                    .get()
                    .addOnSuccessListener(QueryDocumentSnapshot->{
                        if(!QueryDocumentSnapshot.isEmpty()){
                            for (DocumentSnapshot document : QueryDocumentSnapshot ){
                                Denuncia denuncia = document.toObject(Denuncia.class);
                                if(denuncia != null){
                                    denunciasComentarios.add(denuncia);
                                }
                            }
                            recycler_demandas.setLayoutManager(new LinearLayoutManager( admi_denuncias.this, LinearLayoutManager.VERTICAL, false));
                            recycler_demandas.setAdapter(new AdapterAdminVerComentarioComoAdministrador(denunciasComentarios));
                        }
                    })
                    .addOnFailureListener(e->{
                        Log.e("Error", "Error al obtener denuncias de Comentarios" + e ,e);
                    });
        });

        barra_navegacion1.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_denuncias) {
                return true;
            } else if (itemId == R.id.menu_estadisticas) {
                startActivity(new Intent(this, admi_populares.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.menu_perfil) {
                startActivity(new Intent(this, perfil_admin.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });


    }

}
