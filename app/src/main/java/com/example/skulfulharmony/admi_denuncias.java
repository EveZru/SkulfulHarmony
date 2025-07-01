package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterAdminVerClasesDenunciadas;
import com.example.skulfulharmony.adapters.AdapterAdminVerComentarioComoAdministrador;
import com.example.skulfulharmony.adapters.AdapterAdminVerCursosDenunciados;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class admi_denuncias extends AppCompatActivity {

    private RecyclerView recycler_demandas;
    private BottomNavigationView barra_navegacion1;
    private TabLayout tab_denuncias;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admi_denuncias);

        recycler_demandas = findViewById(R.id.recycler_demandas);
        barra_navegacion1 = findViewById(R.id.barra_navegacionadmi);
        tab_denuncias = findViewById(R.id.tab_denuncias);
        db = FirebaseFirestore.getInstance();

        // Agregar pestañas
        tab_denuncias.addTab(tab_denuncias.newTab().setText("Cursos"));
        tab_denuncias.addTab(tab_denuncias.newTab().setText("Comentarios"));
        tab_denuncias.addTab(tab_denuncias.newTab().setText("Clases"));

        // Listener para tabs
        tab_denuncias.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: cargarDenuncias("curso"); break;
                    case 1: cargarDenuncias("comentario"); break;
                    case 2: cargarDenuncias("clase"); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Mostrar cursos por default
        cargarDenuncias("curso");

        barra_navegacion1.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_denuncias) return true;
            if (itemId == R.id.menu_estadisticas) {
                startActivity(new Intent(this, admi_populares.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            if (itemId == R.id.menu_perfil) {
                startActivity(new Intent(this, perfil_admin.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void cargarDenuncias(String tipo) {
        db.collection("denuncias")
                .whereEqualTo("formato", tipo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Denuncia> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Denuncia d = doc.toObject(Denuncia.class);
                        if (d != null) lista.add(d);
                    }

                    recycler_demandas.setLayoutManager(new LinearLayoutManager(this));
                    switch (tipo) {
                        case "curso":
                            recycler_demandas.setAdapter(new AdapterAdminVerCursosDenunciados(lista));
                            break;
                        case "comentario":
                            recycler_demandas.setAdapter(new AdapterAdminVerComentarioComoAdministrador(lista));
                            break;
                        case "clase":
                            recycler_demandas.setAdapter(new AdapterAdminVerClasesDenunciadas(lista));
                            break;
                    }
                })
                .addOnFailureListener(e -> Log.e("DENUNCIAS_DB", "Error " , e));
    }
}
