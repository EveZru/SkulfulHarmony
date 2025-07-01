package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterCrearVerCursosCreados;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class VerCursosCreados extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_cursos_creados);


        bottomNavigationView = findViewById(R.id.barra_navegacion1);
        floatingActionButton = findViewById(R.id.bttn_vercursocreador_agregarclase);
        recyclerView = findViewById(R.id.rv_cursos_creados);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



        cargarCursosFirebase();

        floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(VerCursosCreados.this, CrearCurso.class));
        });


        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_homme);



        if (bottomNavigationView1 != null) {
            // Configurar el listener para los ítems seleccionados
            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    // Acción para Home
                    startActivity(new Intent(VerCursosCreados.this, Home.class));
                    finish();
                    return true;
                } else if (itemId == R.id.it_new) {
                    // Navegar a la actividad para crear un curso

                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    // Navegar a la actividad para ver la Biblioteca
                    startActivity(new Intent(VerCursosCreados.this, Biblioteca.class));
                    finish();
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    // Navegar a la actividad para buscar perfiles
                    startActivity(new Intent(VerCursosCreados.this, Perfil.class));
                    finish();
                    return true;
                }
                return false;
            });
            // Establecer el ítem seleccionado al inicio (si es necesario)
            bottomNavigationView1.setSelectedItemId(R.id.it_new);
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }

    }

    private void cargarCursosFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            CollectionReference cursosRef = db.collection("cursos");

            Query cursosQuery = cursosRef
                    .whereEqualTo("creador", user.getEmail())
                    .orderBy("fechaCreacionf", Query.Direction.ASCENDING);


            cursosQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {

                ArrayList<Curso> listaCursost = new ArrayList<>();


                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Curso curso = document.toObject(Curso.class);
                    if (curso != null && !curso.getTitulo().startsWith("✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧")) {
                        listaCursost.add(curso);
                    }
                }

                AdapterCrearVerCursosCreados adapter = new AdapterCrearVerCursosCreados(listaCursost, VerCursosCreados.this);
                recyclerView.setAdapter(adapter);

            }).addOnFailureListener(e -> {

                Toast.makeText(VerCursosCreados.this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                Log.e("Firestore", "Error: ", e);
            });
        } else {

            Toast.makeText(VerCursosCreados.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCursosFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cursos, menu);
        return true;
    }


}