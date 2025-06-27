package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterBibliotecaVerCursosActualizacion;
import com.example.skulfulharmony.adapters.AdapterBibliotecaVerCursosHistorial;
import com.example.skulfulharmony.adapters.AdapterCursosDescargados;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca extends AppCompatActivity {

    private DbUser dbUser = new DbUser(this);
    private BottomNavigationView bottomNavigationView;

    private RecyclerView rvDescargados;
    private Button btnVerDescargas;
    private Button btnVerSeguidos;
    private Button btnVerHistorial;

    private static final int REQUEST_VER_CURSO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        rvDescargados = findViewById(R.id.rv_bliblioteca);
        btnVerDescargas = findViewById(R.id.btn_gotodescargados);
        btnVerSeguidos = findViewById(R.id.btn_gotoseguidos);
        btnVerHistorial = findViewById(R.id.btn_gotohistorial);

        rvDescargados.setLayoutManager(new LinearLayoutManager(this));
        btnVerDescargas.setOnClickListener(v -> cargarCursosDescargados());
        btnVerSeguidos.setOnClickListener(v -> cargarCursosSeguidos());
        btnVerHistorial.setOnClickListener(v -> cargarCursosHistorial());

        cargarCursosSeguidos();

        bottomNavigationView = findViewById(R.id.barra_navegacion);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    startActivity(new Intent(Biblioteca.this, Home.class));
                    return true;
                } else if (itemId == R.id.it_new) {
                    startActivity(new Intent(Biblioteca.this, VerCursosCreados.class));
                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    startActivity(new Intent(Biblioteca.this, Perfil.class));
                    return true;
                }
                return false;
            });

            bottomNavigationView.setSelectedItemId(R.id.it_seguidos);
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }
    }

    private void cargarCursosHistorial() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            db.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(onQuerySnapshot -> {
                        if (!onQuerySnapshot.isEmpty()) {
                            Usuario usuario = onQuerySnapshot.getDocuments().get(0).toObject(Usuario.class);
                            List<Curso> cursosHistorial = new ArrayList<>();
                            if (usuario != null) {
                                cursosHistorial = usuario.getHistorialCursos();
                            }

                                AdapterBibliotecaVerCursosHistorial adapter = new AdapterBibliotecaVerCursosHistorial(cursosHistorial);
                                rvDescargados.setAdapter(adapter);

                        }
                    })
                    .addOnFailureListener( e -> {
                        Log.e("Error", "Error al obtener los cursos del historial", e);
                    });
        }
    }

    private void cargarCursosSeguidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null ){
            db.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(onQuerySnapshot -> {
                        if (!onQuerySnapshot.isEmpty()) {
                            Usuario usuario = onQuerySnapshot.getDocuments().get(0).toObject(Usuario.class);
                            List<Integer> cursosSeguidos = new ArrayList<>();
                            if (usuario != null && usuario.getCursosSeguidos() != null) {
                                cursosSeguidos = usuario.getCursosSeguidos();
                            }
                            AdapterBibliotecaVerCursosActualizacion adapter = new AdapterBibliotecaVerCursosActualizacion(cursosSeguidos);
                            rvDescargados.setAdapter(adapter);

                        }
                    })
                    .addOnFailureListener( e -> {
                        Log.e("Error", "Error al obtener los cursos seguidos", e);
                    });
        }
    }

    private void cargarCursosDescargados() {
        DbHelper dbHelper = new DbHelper(this);
        List<Curso> cursos = dbHelper.obtenerCursosDescargados();

        AdapterCursosDescargados adapter = new AdapterCursosDescargados(cursos, curso -> {
            Intent intent = new Intent(Biblioteca.this, VerCursoDescargado.class);
            intent.putExtra("curso_id", curso.getId());
            startActivityForResult(intent, REQUEST_VER_CURSO);
        });
        rvDescargados.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VER_CURSO && resultCode == RESULT_OK) {
            cargarCursosDescargados();
        }
    }
}