package com.example.skulfulharmony;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.viewpager2.widget.ViewPager2;


import com.example.skulfulharmony.adapters.AdapterHomeVerClaseHistorial;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursosOriginales;
import com.example.skulfulharmony.adapters.AdapterPopulares;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private ViewPager2 viewPager;


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

        listaCursos = new ArrayList<>();


        // Cursos originale creados de forma estatica
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


        //____coso de habrir lo de las preguntas incorrectas--------------------
        SharedPreferences prefs = getSharedPreferences("mi_pref", MODE_PRIVATE);
        String lastDate = prefs.getString("last_open_date", "");

        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        if (!today.equals(lastDate)) {
            // Primera vez que abres hoy la app, lanzas PreguntasIncorrectas
            Intent intent = new Intent(this, PreguntasIncorrectas.class);
            startActivity(intent);

            // Guardas la fecha para no volver a lanzar hoy
            prefs.edit().putString("last_open_date", today).apply();
        }
        // para populares  cargar los populares de firebase -------------
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listaCursos = new ArrayList<>();
       // List<Curso> listaCursos = new ArrayList<>();

        db.collection("cursos")
                .orderBy("popularidad", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Curso curso = doc.toObject(Curso.class);
                        listaCursos.add(curso);
                    }
                    mostrarCarrusel(listaCursos); // ← siguiente paso
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                });



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
    private void cargarCursosPopulares() {
        double alpha = 1.0;
        double beta = 2.0;
        double gamma = 3.0;
        double delta = 1.5;
        double epsilon = 0.5;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cursosRef = db.collection("cursos");

        cursosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Curso> listaCursosPopulares = new ArrayList<>();

            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Curso curso = document.toObject(Curso.class);

                // Asegúrate de que todos los campos existen y no son nulos
                double visitas = curso.getVisitas();  // ya no marcaría error

                double interacciones = 0;
                List<Comentario> listaComentarios = curso.getComentarios();
                if (listaComentarios != null) {
                    interacciones = listaComentarios.size();
                }

                double calificaciones = 0;
                List<Integer> listaCalificaciones = curso.getCalificacionCursos();

                if (listaCalificaciones != null && !listaCalificaciones.isEmpty()) {
                    int suma = 0;
                    for (int cal : listaCalificaciones) {
                        suma += cal;
                    }
                    calificaciones = (double) suma / listaCalificaciones.size(); // Promedio
                }

                //   double progreso = curso.getProgreso() != null ? curso.getProgreso() : 0;// olvide decirte que este elemeto no lo temaremos en cuanta
                double descargas = curso.getCantidadDescargas() != null ? curso.getCantidadDescargas() : 0;//aqui all bien

                // Calcula popularidad
                double popularidad = alpha * visitas +
                        beta * interacciones +
                        gamma * calificaciones +
                      //  1.5 * progreso +
                        epsilon * descargas;

                curso.setPopularidad(popularidad); // debes agregar este campo a tu modelo

                listaCursosPopulares.add(curso);
            }

            // Ordenar por popularidad
            Collections.sort(listaCursosPopulares, (c1, c2) -> Double.compare(c2.getPopularidad(), c1.getPopularidad()));

            // Tomar los 10 más populares
            List<Curso> top10 = listaCursosPopulares.subList(0, Math.min(10, listaCursosPopulares.size()));

           // mostrarCarrusel(top10); // lo creamos en el siguiente paso
        }).addOnFailureListener(e -> {
            Toast.makeText(Home.this, "Error al cargar cursos populares", Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error: ", e);
        });
    }

    private void mostrarCarrusel(List<Curso> listaCursos) {
        AdapterPopulares adapterPopulares;
        Handler handler = new Handler();
        adapterPopulares = new AdapterPopulares(listaCursos);
        viewPager = findViewById(R.id.view_populares);
        viewPager.setAdapter(adapterPopulares);

        final int[] currentIndex = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (adapterPopulares.getItemCount() == 0) return;
                viewPager.setCurrentItem(currentIndex[0], true);
                currentIndex[0] = (currentIndex[0] + 1) % adapterPopulares.getItemCount();
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

    }


}