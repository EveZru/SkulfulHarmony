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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btn_vercursos_verdenuncias.setOnClickListener(v -> {
            actualizarColorBotones(btn_vercursos_verdenuncias);
            db.collection("denuncias")
                    .whereEqualTo("formato", "curso")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Denuncia> denunciasCursos = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot) {
                            Denuncia d = doc.toObject(Denuncia.class);
                            if (d != null) denunciasCursos.add(d);
                        }
                        recycler_demandas.setLayoutManager(new LinearLayoutManager(this));
                        recycler_demandas.setAdapter(new AdapterAdminVerCursosDenunciados(denunciasCursos));
                    })
                    .addOnFailureListener(e -> Log.e("DENUNCIAS_DB", "Error al obtener denuncias de cursos", e));
        });

        btn_verclases_verdenuncias.setOnClickListener(v -> {
            actualizarColorBotones(btn_verclases_verdenuncias);
            db.collection("denuncias")
                    .whereEqualTo("formato", "clase")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Denuncia> denunciasClases = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot) {
                            Denuncia d = doc.toObject(Denuncia.class);
                            if (d != null) denunciasClases.add(d);
                        }
                        recycler_demandas.setLayoutManager(new LinearLayoutManager(this));
                        recycler_demandas.setAdapter(new AdapterAdminVerClasesDenunciadas(denunciasClases));
                    })
                    .addOnFailureListener(e -> Log.e("DENUNCIAS_DB", "Error al obtener denuncias de clases", e));
        });

        btn_vercomentarios_verdenuncias.setOnClickListener(v -> {
            actualizarColorBotones(btn_vercomentarios_verdenuncias);
            db.collection("denuncias")
                    .whereEqualTo("formato", "comentario")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Denuncia> denunciasComentarios = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot) {
                            Denuncia d = doc.toObject(Denuncia.class);
                            if (d != null) denunciasComentarios.add(d);
                        }
                        recycler_demandas.setLayoutManager(new LinearLayoutManager(this));
                        recycler_demandas.setAdapter(new AdapterAdminVerComentarioComoAdministrador(denunciasComentarios));
                    })
                    .addOnFailureListener(e -> Log.e("DENUNCIAS_DB", "Error al obtener denuncias de comentarios", e));
        });

        barra_navegacion1.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_denuncias) return true;
            if (itemId == R.id.menu_estadisticas) {
                startActivity(new Intent(this, admi_populares.class));
                overridePendingTransition(0, 0);
                return true;
            }
            if (itemId == R.id.menu_perfil) {
                startActivity(new Intent(this, perfil_admin.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
    private void actualizarColorBotones(Button seleccionado) {
        int colorSeleccionado = getResources().getColor(R.color.rojo);
        int colorDefault = getResources().getColor(R.color.white);

        btn_vercursos_verdenuncias.setTextColor(btn_vercursos_verdenuncias == seleccionado ? colorSeleccionado : colorDefault);
        btn_verclases_verdenuncias.setTextColor(btn_verclases_verdenuncias == seleccionado ? colorSeleccionado : colorDefault);
        btn_vercomentarios_verdenuncias.setTextColor(btn_vercomentarios_verdenuncias == seleccionado ? colorSeleccionado : colorDefault);
    }
}
