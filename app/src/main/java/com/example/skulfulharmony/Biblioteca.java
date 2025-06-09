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

import com.example.skulfulharmony.adapters.AdapterCursosDescargados;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class Biblioteca extends AppCompatActivity {

    private DbUser dbUser = new DbUser(this);
    private BottomNavigationView bottomNavigationView;

    private RecyclerView rvDescargados;
    private Button btnVerDescargas;

    private static final int REQUEST_VER_CURSO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        rvDescargados = findViewById(R.id.rv_bliblioteca);
        btnVerDescargas = findViewById(R.id.btn_gotodescargados);

        rvDescargados.setLayoutManager(new LinearLayoutManager(this));
        btnVerDescargas.setOnClickListener(v -> cargarCursosDescargados());

        cargarCursosDescargados(); // Mostrar cursos al iniciar

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