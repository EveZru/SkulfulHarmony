package com.example.skulfulharmony;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int backPressCount = 0; // Contador de veces que se presiona atrás

    private Handler backPressHandler = new Handler();
    private EditText et_buscarhome;

    private SQLiteDatabase localDatabase;

    private List<Curso> listaCursos;
    private RecyclerView rv_homevercursos;
    private AdapterHomeVerCursos adapterHomeVerCursos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_homme);
        rv_homevercursos = findViewById(R.id.rv_homevercursos);

        startActivity(new Intent(Home.this, CrearClase.class));
        rv_homevercursos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        cargarCursosFirebase();
        //-------Parte de los cursos de clases originales -------
        // Aquí creamos los objetos Curso de forma estática
        listaCursos = new ArrayList<>();

        // Ejemplo de objetos Curso
        Curso curso1 = new Curso("Curso de Guitarra", null, null, null, null);
        Curso curso2 = new Curso("Curso de Piano", null, null, null, null);
        Curso curso3 = new Curso("Curso de Batería", null, null, null, null);

        // Agregamos los cursos a la lista
        listaCursos.add(curso1);
        listaCursos.add(curso2);
        listaCursos.add(curso3);


        RecyclerView recyclerView = findViewById(R.id.rv_homeclasesoriginales);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Crea el adaptador y establece el RecyclerView
        AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(listaCursos, this);
        recyclerView.setAdapter(adapter);

        DbHelper dbHelper = new DbHelper(Home.this);
        localDatabase = dbHelper.getReadableDatabase();

        et_buscarhome=findViewById(R.id.et_buscarhome);


        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario está autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Si no hay usuario, redirigir a Login
            Intent intent = new Intent(Home.this, IniciarSesion.class);
            startActivity(intent);
            finish(); // Evita que el usuario vuelva a Home si no está logueado
        }

        //  Listener para abrir la actividad de búsqueda
        et_buscarhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Busqueda.class);
                intent.putExtra("focus", true); // Enviar extra para enfocar el EditText en la otra actividad
                startActivity(intent);
            }
        });


        // Manejo del botón de retroceso con 3 clics
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressCount++;
                if (backPressCount == 3) {
                    moveTaskToBack(true); // 🔹 Minimiza la aplicación en lugar de cerrarla
                } else {
                    Toast.makeText(Home.this, "Presiona atrás " + (3 - backPressCount) + " veces más para salir", Toast.LENGTH_SHORT).show();
                    backPressHandler.postDelayed(() -> backPressCount = 0, 2000); // Reinicia el contador después de 2 segundos
                }
            }
        });

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (bottomNavigationView1 != null) {
            // Configurar el listener para los ítems seleccionados
            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                 if (itemId == R.id.it_homme) {
                     // Acción para Home
                     return true;
                 } else if (itemId == R.id.it_new) {
                     // Navegar a la actividad para crear un curso
                     startActivity(new Intent(Home.this, CrearCurso.class));
                     return true;
                 } else if (itemId == R.id.it_seguidos) {
                     // Navegar a la actividad para ver la Biblioteca
                     startActivity(new Intent(Home.this, Biblioteca.class));
                     return true;
                 } else if (itemId == R.id.it_perfil) {
                        // Navegar a la actividad para buscar perfiles
                        startActivity(new Intent(Home.this, Perfil.class));
                        return true;
                    }
                    return false;
                });
                // Establecer el ítem seleccionado al inicio (si es necesario)
                bottomNavigationView1.setSelectedItemId(R.id.it_homme);
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }

    }

    private void cargarCursosFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cursosRef = db.collection("cursos");

        cursosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Curso> listaCursost = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Curso curso = document.toObject(Curso.class);
                listaCursost.add(curso);
            }

            // Ahora actualiza el adaptador
            adapterHomeVerCursos = new AdapterHomeVerCursos(listaCursost, Home.this);
            rv_homevercursos.setAdapter(adapterHomeVerCursos);
        }).addOnFailureListener(e -> {
            Toast.makeText(Home.this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error: ", e);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacion1);
        cargarCursosFirebase();
        // Establece el ítem seleccionado
        if (this instanceof Home) {
            bottomNavigationView.setSelectedItemId(R.id.it_homme);

        }
    }

}


