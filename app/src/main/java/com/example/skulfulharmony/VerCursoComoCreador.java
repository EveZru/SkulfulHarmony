package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.google.firebase.firestore.DocumentReference;
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

    ImageView imagenTitulo,menu;
    TextView tv_tituloCurso, descripcionCurso, fechaCreacion;
    RecyclerView rvClases;
    FloatingActionButton bttnAgregarClase;

    FirebaseFirestore firestore;
    int idCurso;


    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_curso_como_creador);

        imagenTitulo = findViewById(R.id.imgen_vercursocreador_imagentitulo);
        tv_tituloCurso = findViewById(R.id.text_vercursocreador_title);
        descripcionCurso = findViewById(R.id.tv_descripciomicurso);
        fechaCreacion = findViewById(R.id.text_vercursocreador_fecha);
        rvClases = findViewById(R.id.rv_verclasesencursocomocreador);
        bttnAgregarClase = findViewById(R.id.bttn_vercursocreador_agregarclase);

        firestore = FirebaseFirestore.getInstance();

        menu=findViewById(R.id.iv_despegarmenu);
        // para que el creador puede editar su curso o editarlo
        menu.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {showMenu(view);}
        });

        rvClases.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Obtener idCurso del intent
        idCurso = getIntent().getIntExtra("idCurso",1);

        if (idCurso != -1) {
            cargarClases();
            cargarInfo(idCurso);
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
    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.ver_como_creador, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item->{
            int id = item.getItemId();
            if(id==R.id.it_editar){
                Intent intent = new Intent(VerCursoComoCreador.this, EditarCurso.class);
                intent.putExtra("idCurso", idCurso);
                startActivity(intent);
                return true;
            }else if(id==R.id.it_eliminar){
                Toast.makeText(this, "se supone que elimina el curso ", Toast.LENGTH_SHORT).show();
                return true;
            }else {
                Toast.makeText(VerCursoComoCreador.this, "No se selecciono ninguna opcion", Toast.LENGTH_SHORT).show();

            }
            return false;

        });
        popupMenu.show();
    }
    private void cargarInfo(int id) {
        firestore.collection("cursos")
                .whereEqualTo("idCurso", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String nombre = document.getString("titulo");

                        if (nombre != null) {
                            tv_tituloCurso.setText(nombre);
                        } else {
                            tv_tituloCurso.setText("Sin titulo");
                        }
                    } else {
                        Toast.makeText(VerCursoComoCreador.this, "Curso no encontrado.", Toast.LENGTH_LONG).show();
                         finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VerCursoComoCreador.this, "Error al cargar el curso: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VerCursoComoCreador", "Error cargando curso: " + e.getMessage(), e);
                     // finish();
                });
    }


}



