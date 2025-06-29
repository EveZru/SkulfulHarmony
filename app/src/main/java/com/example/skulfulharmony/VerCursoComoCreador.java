package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterCrearVerClasesCreadas;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.modooffline.ClaseFirebase;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VerCursoComoCreador extends AppCompatActivity {

    private String documentIdCurso;

    ImageView imagenTitulo,menu;
    TextView tv_tituloCurso, tv_descripcionCurso, tv_fechaCreacion;
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
        tv_descripcionCurso = findViewById(R.id.tv_descripciomicurso);
        tv_fechaCreacion = findViewById(R.id.tv_fechapublic);
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
            cargarInfo(idCurso);
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

                new AlertDialog.Builder(VerCursoComoCreador.this)// cambie el context
                        .setTitle("Eliminar curso")
                        .setMessage("¿Estás seguro de que quieres eliminar este curso junto con todas sus clases y archivos?")
                        .setPositiveButton("Sí", (dialog, which) -> eliminarCurso(idCurso))
                        .setNegativeButton("Cancelar", null)
                        .show();
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
                        documentIdCurso = document.getId();
                        String nombre = document.getString("titulo");
                        String descripcion = document.getString("descripcion");
                        String fechaCreacionStr = document.getString("fechaCreacion");
                        String urlImagen = document.getString("imagen");
                        if (nombre != null) {
                            tv_tituloCurso.setText(nombre);
                        } else {
                            tv_tituloCurso.setText("Sin titulo");
                        }
                        if (descripcion != null) {
                            tv_descripcionCurso.setText(descripcion);
                        } else {
                            tv_descripcionCurso.setText("Sin desscripcion");
                        }
                        if (fechaCreacionStr != null) {
                            tv_fechaCreacion.setText(fechaCreacionStr);
                        } else {
                            tv_fechaCreacion.setText("Desconocemos la fecha de creacion");
                        }
                        if(urlImagen!=null){
                            Glide.with(this)
                                    .load(urlImagen)
                                    .placeholder(R.drawable.img_encabezado) // opcional
                                    .error(R.drawable.error_button)         // opcional
                                    .into(imagenTitulo);
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


    private void eliminarCurso(int cursoId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Eliminar clases asociadas
        db.collection("clases")
                .whereEqualTo("idCurso", cursoId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String claseId = doc.getId();
                        db.collection("clases").document(claseId).delete();
                    }

                    // 2. Eliminar el curso por su verdadero documentId
                    if (documentIdCurso != null && !documentIdCurso.isEmpty()) {
                        db.collection("cursos").document(documentIdCurso)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(VerCursoComoCreador.this, "Curso eliminado", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(VerCursoComoCreador.this, "Fallo eliminacion, intentando desabilitar ", Toast.LENGTH_SHORT).show();
                                    deshabilitarCurso(db, documentIdCurso);
                                });
                    } else {
                        Toast.makeText(VerCursoComoCreador.this, "ID del curso no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VerCursoComoCreador.this, "Error al buscar clases del curso", Toast.LENGTH_SHORT).show();
                });

        // 3. Eliminar archivos locales
        eliminarArchivosLocales(cursoId);
    }
    private void deshabilitarCurso(FirebaseFirestore db, String documentIdCurso) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("titulo", "✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧"); // Carácter invisible + texto
        updates.put("creador", "✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧");
        updates.put("descripcion", "✩♬ ₊˚.\uD83C\uDFA7⋆☾⋆⁺₊✧Este curso ha sido eliminado");
        updates.put("popularidad",0.0);
        updates.put("visitas",0);
        updates.put("cantidadDescargas",0);
       updates.put( "promedioCalificacion",0.0);
        updates.put("strike1",true);
        updates.put("strike2",true);
        updates.put("strike3",true);

        db.collection("cursos").document(documentIdCurso)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(VerCursoComoCreador.this, "Curso marcado como eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VerCursoComoCreador.this, "Error al deshabilitar curso: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void eliminarArchivosLocales(int cursoId) {
        File claseFolder = new File(getFilesDir(), "clases/" + cursoId);
        if (claseFolder.exists()) {
            File[] files = claseFolder.listFiles();
            if (files != null) {
                for (File archivo : files) {
                    archivo.delete();
                }
                claseFolder.delete();
            }
        }
    }




   /* */
}