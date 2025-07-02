package com.example.skulfulharmony;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;


import com.example.skulfulharmony.adapters.AdapterHomeVerClaseHistorial;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursosOriginales;
import com.example.skulfulharmony.adapters.AdapterHomeVerTodosLosCursos;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity {

    private int backPressCount = 0; // Contador de veces que se presiona atrás
    private Handler backPressHandler = new Handler();
    private AppCompatButton et_buscarhome;
    private List<Curso> listaCursos;
    private RecyclerView rv_populares,rv_homevercursos, rv_homehistorial,rv_homeclasesoriginales;
    RecyclerView recyclerView2;
    private RecyclerView verTodosLosCursos;
    private AdapterHomeVerCursosOriginales adapterHomeVerCursosOriginales;
    private Handler handler = new Handler();
    private int currentPosition = 0;
    private SQLiteDatabase localDatabase;
    private ImageView iv_buscar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_homme);
        rv_homevercursos = findViewById(R.id.rv_homevercursos);
        rv_homehistorial = findViewById(R.id.rv_historial_home);
        et_buscarhome=findViewById(R.id.et_buscarhome);
        recyclerView2 = findViewById(R.id.rv_homeclasesoriginales);
        rv_populares = findViewById(R.id.rv_populares_homme);
        rv_populares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_homehistorial.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_homevercursos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        et_buscarhome=findViewById(R.id.et_buscarhome);
        iv_buscar=findViewById(R.id.iv_buscarhome);
        verTodosLosCursos=findViewById(R.id.rv_hometodosloscursos);

        Boolean primeraVez = false;
        primeraVez = getIntent().getBooleanExtra("primeraVez", false);

        //-------Parte de los cursos de clases originales -------
        listaCursos = new ArrayList<>();
        // Cursos originale
        Curso curso1 = new Curso("Fundamentos", null, null, null, null);
        Curso curso2 = new Curso("Partituras", null, null, null, null);
        Curso curso3 = new Curso("Piano", null, null, null, null);
        Curso curso4 = new Curso("Flauta", null, null, null, null);
        Curso curso5 = new Curso("Guitarra", null, null, null, null);
        Curso curso6 = new Curso("Afinador", null, null, null, null);

        listaCursos.add(curso1);
        listaCursos.add(curso2);
        listaCursos.add(curso3);
        listaCursos.add(curso4);
        listaCursos.add(curso5);
        listaCursos.add(curso6);

        RecyclerView recyclerView2 = findViewById(R.id.rv_homeclasesoriginales);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterHomeVerCursosOriginales=new AdapterHomeVerCursosOriginales((Context) this, listaCursos);

        recyclerView2.setAdapter(adapterHomeVerCursosOriginales);
//_________________________________________________
        DbHelper dbHelper = new DbHelper(Home.this);
        localDatabase = dbHelper.getReadableDatabase();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

            Intent intent = new Intent(Home.this, IniciarSesion.class);
            startActivity(intent);
            finish();
        }


        cargarCursosCluster();
        cargarCursosHistorial();
        cargarCursosPopulares();
        carrucelPopulares();
        cargarTodosLosCursos();

        et_buscarhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Busqueda.class);
                startActivity(intent);
            }
        });
        iv_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Busqueda.class);
                startActivity(intent);
            }
        });
        // Manejo del botón de retroceso con 3 clics
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressCount++;
                if (backPressCount == 3) {
                    moveTaskToBack(true);
                } else {
                    Toast.makeText(Home.this, "Presiona atrás " + (3 - backPressCount) + " veces más para salir", Toast.LENGTH_SHORT).show();
                    backPressHandler.postDelayed(() -> backPressCount = 0, 2000);
                }
            }
        });
        rv_populares = findViewById(R.id.rv_populares_homme);
        rv_populares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv_populares);
        //____ de abrir lo de las preguntas incorrectas--------------------
        SharedPreferences prefs = getSharedPreferences("mi_pref", MODE_PRIVATE);
        String lastDate = prefs.getString("last_open_date", "");

        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        if (!today.equals(lastDate) && !primeraVez) {
            Intent intent = new Intent(this, PreguntasIncorrectas.class);
            startActivity(intent);
            prefs.edit().putString("last_open_date", today).apply();
        }

        if (bottomNavigationView1 != null) {
            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {

                    return true;
                } else if (itemId == R.id.it_new) {

                    startActivity(new Intent(Home.this, VerCursosCreados.class));
                    return true;
                } else if (itemId == R.id.it_seguidos) {

                    startActivity(new Intent(Home.this, Biblioteca.class));
                    return true;
                } else if (itemId == R.id.it_perfil) {

                    startActivity(new Intent(Home.this, Perfil.class));
                    return true;
                }
                return false;
            });
            bottomNavigationView1.setSelectedItemId(R.id.it_homme);
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }
    }

    private void cargarTodosLosCursos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cursos")
                .orderBy("popularidad", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(onSucces->{
                    List<Curso> cursos = new ArrayList<>();
                    for (DocumentSnapshot doc : onSucces) {
                        Curso curso = doc.toObject(Curso.class);
                        if (curso != null && curso.getTitulo() != null && !curso.getTitulo().startsWith("✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧" ) && !curso.getTitulo().isEmpty()
                                && !curso.getTitulo().toLowerCase().startsWith("desabilitado") && !curso.getTitulo().equals("")){
                            cursos.add(curso);
                        }

                    }
                    verTodosLosCursos.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    AdapterHomeVerTodosLosCursos adapter = new AdapterHomeVerTodosLosCursos(cursos, Home.this);
                    verTodosLosCursos.setAdapter(adapter);
                })
                .addOnFailureListener(e->{
                });
    }

    private void cargarCursosCluster() {
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
                        Usuario usuario = doc.toObject(Usuario.class);
                        usuario.setId(doc.getId());

                        usuario.calcularClusterUsuario(db, usuario, usuarioConCluster -> {
                            String instrumentoClave = usuarioConCluster.getInstrumento().keySet().iterator().next();
                            Integer instrumentoValor = usuarioConCluster.getInstrumento().get(instrumentoClave);
                            Integer cluster = usuarioConCluster.getCluster();

                            if (instrumentoClave == null || instrumentoValor == null || cluster == null) {
                                Log.e("Cluster", "Datos incompletos para buscar cursos");
                                return;
                            }

                            db.collection("cursos")
                                    .whereEqualTo("instrumento." + instrumentoClave, instrumentoValor)
                                    .whereEqualTo("cluster", cluster)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        List<Curso> cursos = new ArrayList<>();

                                        for (DocumentSnapshot c : snapshot) {
                                            Curso curso = c.toObject(Curso.class);
                                        
                                            if (curso != null && curso.getTitulo() != null && !curso.getTitulo().toLowerCase().startsWith("desabilitado")&& curso.getTitulo() != null && !curso.getTitulo().startsWith("✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧")) {

                                                cursos.add(c.toObject(Curso.class));
                                            }
                                        }

                                        AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(cursos, Home.this);
                                        rv_homevercursos.setAdapter(adapter);
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar cursos", e));
                        });

                    } else {
                        Log.e("Firestore", "Usuario no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al buscar usuario", e);
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


                                List<Clase> ultimasDiezClases = historialClases.subList(0, Math.min(historialClases.size(), 10));


                                AdapterHomeVerClaseHistorial adapter = new AdapterHomeVerClaseHistorial(ultimasDiezClases, Home.this, true); // Cambia el constructor del adaptador
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
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacion1);
        cargarCursosHistorial();
        if (this instanceof Home) {
            bottomNavigationView.setSelectedItemId(R.id.it_homme);

        }
    }
    private void cargarCursosPopulares() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView rvPopulares = findViewById(R.id.rv_populares_homme);
        rvPopulares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        db.collection("cursos")
                .orderBy("popularidad", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Curso> cursosPopulares = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Curso curso = doc.toObject(Curso.class);
                        if (curso != null && curso.getTitulo() != null && !curso.getTitulo().startsWith("✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧")) {
                            cursosPopulares.add(curso);
                        }
                    }

                    if (!cursosPopulares.isEmpty()) {
                        AdapterPopulares adapter = new AdapterPopulares(cursosPopulares, this);
                        rvPopulares.setAdapter(adapter);
                    } else {
                        mostrarCursoError("No se encontraron cursos populares", "Intenta más tarde");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al cargar cursos populares", e);
                    mostrarCursoError("Error al cargar populares", "Verifica tu conexión");
                });
    }

    private void mostrarCursoError(String titulo, String mensaje) {
        RecyclerView rvPopulares = findViewById(R.id.rv_populares_homme);
        Curso error = new Curso(titulo, mensaje, null, null, null);
        List<Curso> listaError = new ArrayList<>();
        listaError.add(error);
        rvPopulares.setAdapter(new AdapterHomeVerCursos(listaError, this));
    }

    private void carrucelPopulares() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rv_populares.getAdapter() == null || rv_populares.getAdapter().getItemCount() == 0) return;
                int totalItems = rv_populares.getAdapter().getItemCount();
                currentPosition++;
                if (currentPosition >= totalItems) {
                    currentPosition = 0;
                }
                rv_populares.smoothScrollToPosition(currentPosition);
                handler.postDelayed(this, 3000);
            }
        }, 8000);
    }
}