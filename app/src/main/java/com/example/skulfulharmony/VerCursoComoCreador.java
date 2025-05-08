package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterCrearVerClasesCreadas;
import com.example.skulfulharmony.adapters.AdapterCrearVerCursosCreados;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VerCursoComoCreador extends AppCompatActivity {

    ImageView imagenTitulo;
    TextView tituloCurso, descripcionCurso, fechaCreacion;
    RecyclerView rvClases;
    FloatingActionButton bttnAgregarClase;

    FirebaseFirestore firestore;
    int idCurso;
    Toolbar menu_cursos;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_curso_como_creador);

        imagenTitulo = findViewById(R.id.imgen_vercursocreador_imagentitulo);
        tituloCurso = findViewById(R.id.text_vercursocreador_title);
        descripcionCurso = findViewById(R.id.text_vercursocreador_descripcion);
        fechaCreacion = findViewById(R.id.text_vercursocreador_fecha);
        rvClases = findViewById(R.id.rv_verclasesencursocomocreador);
        bttnAgregarClase = findViewById(R.id.bttn_vercursocreador_agregarclase);

        firestore = FirebaseFirestore.getInstance();

        menu_cursos = findViewById(R.id.toolbar_ver_cursos);

        rvClases.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Obtener idCurso del intent
        idCurso = getIntent().getIntExtra("idCurso",1);

        if (idCurso != -1) {
            cargarClases();
        } else {
            Toast.makeText(this, "Error al obtener la informacion del curso", Toast.LENGTH_SHORT).show();
        }

        bttnAgregarClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerCursoComoCreador.this, CrearClase.class);
                intent.putExtra("idCurso", idCurso);
                startActivity(intent);
            }
        });
    }

    private void cargarClases() {
        ArrayList<Clase> listaClases = new ArrayList<>();
        CollectionReference clasesRef = firestore.collection("clases");
        Query query = clasesRef.whereEqualTo("idCurso", idCurso).orderBy("fechaCreacionf", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Clase clase = document.toObject(Clase.class);
                if (clase != null) {
                    listaClases.add(clase);
                }
            }
            AdapterCrearVerClasesCreadas adapter = new AdapterCrearVerClasesCreadas(listaClases, VerCursoComoCreador.this);
            rvClases.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(VerCursoComoCreador.this, "Error al cargar clases: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error: ", e);
        });
    }

}



