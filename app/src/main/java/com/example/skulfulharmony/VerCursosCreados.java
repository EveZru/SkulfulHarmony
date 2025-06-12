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
//
//        Window window = getWindow();
//        window.setDecorFitsSystemWindows(false);


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
                    return true;
                } else if (itemId == R.id.it_new) {
                    // Navegar a la actividad para crear un curso

                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    // Navegar a la actividad para ver la Biblioteca
                    startActivity(new Intent(VerCursosCreados.this, Biblioteca.class));
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    // Navegar a la actividad para buscar perfiles
                    startActivity(new Intent(VerCursosCreados.this, Perfil.class));
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
        
        // Verificar que el usuario esté autenticado
        if (user != null) {
            // Crear la referencia de la colección "cursos"
            CollectionReference cursosRef = db.collection("cursos");

            // Crear la consulta con el filtro y orden
            Query cursosQuery = cursosRef
                    .whereEqualTo("creador", user.getEmail()) // Filtro por el email del creador
                    .orderBy("fechaCreacionf", Query.Direction.ASCENDING); // Orden por fecha de creación de forma ascendente

            // Obtener los cursos
            cursosQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                // Crear una lista para almacenar los cursos
                ArrayList<Curso> listaCursost = new ArrayList<>();

                // Recorrer los documentos obtenidos y convertirlos en objetos Curso
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Curso curso = document.toObject(Curso.class);
                    if (curso != null) {
                        listaCursost.add(curso);
                    }
                }

                // Ahora actualizamos el adaptador con la lista de cursos obtenida
                AdapterCrearVerCursosCreados adapter = new AdapterCrearVerCursosCreados(listaCursost, VerCursosCreados.this);
                recyclerView.setAdapter(adapter);

            }).addOnFailureListener(e -> {
                // Manejo de errores al obtener los cursos
                Toast.makeText(VerCursosCreados.this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                Log.e("Firestore", "Error: ", e);
            });
        } else {
            // Si el usuario no está autenticado
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.it_denunciar) {
            startActivity(new Intent(VerCursosCreados.this, CrearClase.class));
            return true;
        } else if (id == R.id.it_descargar) {
            Toast.makeText(this, "se supone que vas a ver lo de descargas", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.it_compartir) {
            Toast.makeText(this, "se supone que se comparte ", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}