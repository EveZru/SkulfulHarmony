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

import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        rvDescargados = findViewById(R.id.rv_bliblioteca); // Asegúrate de corregir el ID si tiene typo
        rvDescargados.setLayoutManager(new LinearLayoutManager(this));

        btnVerDescargas = findViewById(R.id.btn_gotodescargados);
        btnVerDescargas.setOnClickListener(v -> {
            DbHelper dbHelper = new DbHelper(Biblioteca.this);
            List<Curso> cursos = dbHelper.obtenerCursosDescargados();
            AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(cursos, Biblioteca.this);
            rvDescargados.setAdapter(adapter);
        });

        // Mostrar cursos descargados al inicio también (opcional)
        DbHelper dbHelper = new DbHelper(this);
        List<Curso> cursosDescargados = dbHelper.obtenerCursosDescargados();
        AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(cursosDescargados, this);
        rvDescargados.setAdapter(adapter);

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

    @Override
    protected void onResume() {
        super.onResume();
    }
}