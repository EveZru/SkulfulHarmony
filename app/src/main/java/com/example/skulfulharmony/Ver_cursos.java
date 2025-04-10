package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Ver_cursos extends AppCompatActivity {

    private ImageView imagenTitulo;
    private TextView tituloCurso, descripcionCurso;
    private RecyclerView rvClases; // Puedes llenar esto después con clases del curso

    private FirebaseFirestore firestore;
    private int idCurso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cursos);

        // Vincular vistas
        imagenTitulo = findViewById(R.id.imgen_vercurso_imagentitulo);
        tituloCurso = findViewById(R.id.text_vercurso_title);
        descripcionCurso = findViewById(R.id.text_vercurso_descripcion);
        rvClases = findViewById(R.id.rv_verclasesencurso);

        firestore = FirebaseFirestore.getInstance();

        // Obtener idCurso del intent
        idCurso = getIntent().getIntExtra("idCurso", -1);

        if (idCurso != -1) {
            obtenerCurso(idCurso);
        } else {
            Toast.makeText(this, "ID del curso no válido", Toast.LENGTH_SHORT).show();
            finish();
        }
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
