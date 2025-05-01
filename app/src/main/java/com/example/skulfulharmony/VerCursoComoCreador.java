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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

        // Obtener idCurso del intent
        idCurso = getIntent().getIntExtra("idCurso", -1);

        if (idCurso != -1) {
            obtenerCurso(idCurso);
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

    private void obtenerCurso(int idCurso) {
        CollectionReference cursosRef = firestore.collection("cursos");

        cursosRef.whereEqualTo("idCurso", idCurso)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Curso curso = doc.toObject(Curso.class);

                            // Mostrar datos
                            tituloCurso.setText(curso.getTitulo());

                            if(curso.getFechaCreacionf() == null){
                                fechaCreacion.setText("Error al cargar fecha de creacion");
                            }else {
                                Date date = curso.getFechaCreacionf().toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                fechaCreacion.setText(sdf.format(date));
                            }

                            if(curso.getDescripcion() == null || curso.getDescripcion().isEmpty() || curso.getDescripcion().equals("")){
                                descripcionCurso.setText("Curso sin descripción");
                            }
                            descripcionCurso.setText(curso.getDescripcion());

                            Glide.with(this)
                                    .load(curso.getImagen())
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.img_defaultclass)
                                    .into(imagenTitulo);

                            // Aquí puedes luego cargar clases del curso si quieres


                        }
                    } else {
                        Toast.makeText(this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener el curso", Toast.LENGTH_SHORT).show();
                });
    }



}



