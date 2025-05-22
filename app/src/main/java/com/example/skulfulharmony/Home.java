package com.example.skulfulharmony;

import android.content.Context;
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


import com.example.skulfulharmony.adapters.AdapterHomeVerClaseHistorial;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursosOriginales;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int backPressCount = 0; // Contador de veces que se presiona atrás

    private Handler backPressHandler = new Handler();
    private EditText et_buscarhome;

    private SQLiteDatabase localDatabase;

    private List<Curso> listaCursos;
    private RecyclerView rv_homevercursos, rv_homehistorial;
    private AdapterHomeVerCursos adapterHomeVerCursos;
    private AdapterHomeVerCursosOriginales adapterHomeVerCursosOriginales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_homme);
        rv_homevercursos = findViewById(R.id.rv_homevercursos);
        rv_homehistorial = findViewById(R.id.rv_historial_home);
        rv_homehistorial.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_homevercursos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        et_buscarhome=findViewById(R.id.et_buscarhome);


     // startActivity(new Intent(Home.this, EscribirPartiturasAct.class));

        cargarCursosCluster();
        cargarCursosHistorial();
        //-------Parte de los cursos de clases originales -------
        // Aquí creamos los objetos Curso de forma estática
        listaCursos = new ArrayList<>();


        // Cursos originale
        Curso curso1 = new Curso("Fundamentos", null, null, null, null);
        Curso curso2 = new Curso("Partituras", null, null, null, null);
        Curso curso3 = new Curso("Piano", null, null, null, null);
        Curso curso4 = new Curso("Flauta", null, null, null, null);
        Curso curso5 = new Curso("Guitarra", null, null, null, null);
        Curso curso6 = new Curso("Afinador", null, null, null, null);
        // Agregamos los cursos a la lista
        listaCursos.add(curso1);
        listaCursos.add(curso2);
        listaCursos.add(curso3);
        listaCursos.add(curso4);
        listaCursos.add(curso5);
        listaCursos.add(curso6);
        /*// Ahora actualiza el adaptador
            adapterHomeVerCursos = new AdapterHomeVerCursos(listaCursost, Home.this);
            rv_homevercursos.setAdapter(adapterHomeVerCursos);*/

        RecyclerView recyclerView2 = findViewById(R.id.rv_homeclasesoriginales);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Crea el adaptador y establece el RecyclerView
        adapterHomeVerCursosOriginales=new AdapterHomeVerCursosOriginales((Context) this, listaCursos);
       // AdapterHomeVerCursosOriginales adapter2 = new AdapterHomeVerCursosOriginales(this, listaCursos);
        recyclerView2.setAdapter(adapterHomeVerCursosOriginales);

//_________________________________________________
        DbHelper dbHelper = new DbHelper(Home.this);
        localDatabase = dbHelper.getReadableDatabase();




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


        if (bottomNavigationView1 != null) {
            // Configurar el listener para los ítems seleccionados
            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                 if (itemId == R.id.it_homme) {
                     // Acción para Home
                     return true;
                 } else if (itemId == R.id.it_new) {
                     // Navegar a la actividad para crear un curso
                     startActivity(new Intent(Home.this, VerCursosCreados.class));
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

    private void cargarCursosHistorial() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios")
                .whereEqualTo("correo", user.getEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                        // Convertimos el documento completo a un objeto Usuario
                        Usuario usuario = doc.toObject(Usuario.class);

                        if (usuario != null) {
                            List<Clase> historialClases = usuario.getHistorialClases();

                            if (historialClases != null && !historialClases.isEmpty()) {
                                Log.d("Historial", "Cargadas " + historialClases.size() + " clases del historial");

                                Collections.sort(historialClases, (a, b) -> {
                                    if (a.getFechaAcceso() == null && b.getFechaAcceso() == null) return 0;
                                    if (a.getFechaAcceso() == null) return 1;
                                    if (b.getFechaAcceso() == null) return -1;
                                    return b.getFechaAcceso().compareTo(a.getFechaAcceso()); // Más reciente primero
                                });

                                // Configura el RecyclerView con el historial cargado
                                AdapterHomeVerClaseHistorial adapter = new AdapterHomeVerClaseHistorial(historialClases, Home.this);
                                rv_homehistorial.setAdapter(adapter);
                            } else {
                                Log.d("Historial", "No hay historial de clases para mostrar");
                            }
                        } else {
                            Log.e("Firestore", "No se pudo convertir el documento a Usuario");
                        }
                    } else {
                        Log.e("Firestore", "Usuario no encontrado en la base de datos");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al consultar Firestore", e);
                    Toast.makeText(this, "Error al cargar historial", Toast.LENGTH_SHORT).show();
                });
    }




    private void cargarCursosCluster() {
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
        cargarCursosCluster();
        cargarCursosHistorial();
        // Establece el ítem seleccionado
        if (this instanceof Home) {
            bottomNavigationView.setSelectedItemId(R.id.it_homme);

        }
    }

}