package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dropbox.core.v2.clouddocs.UserInfo;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.adapters.AdapterVerClasesCursoOtroUsuario;
import com.example.skulfulharmony.adapters.AdapterVerCursoVerComentarios;
import com.example.skulfulharmony.javaobjects.courses.Clase;

import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Ver_cursos extends AppCompatActivity {

    private ImageView imagenTitulo;
    private TextView tituloCurso, descripcionCurso, autorCurso;
    private RecyclerView rvClases; // Puedes llenar esto después con clases del curso
    private RecyclerView rvComentarios;
    private Button crear_comentario;
    private EditText etcomentario;


    private FirebaseFirestore firestore;
    private int idCurso;
    private ImageView desplegarmenu;

    private ImageView[] estrellas;
    private TextView tvPuntuacion;
    private int puntuacionActual = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cursos);

        // Vincular vistas
        imagenTitulo = findViewById(R.id.imgen_vercurso_imagentitulo);
        tituloCurso = findViewById(R.id.text_vercurso_title);
        descripcionCurso = findViewById(R.id.text_vercurso_descripcion);
        autorCurso = findViewById(R.id.text_vercurso_autor);
        rvClases = findViewById(R.id.rv_verclasesencurso);
        rvComentarios = findViewById(R.id.rv_comentarios_vercurso);
        desplegarmenu = findViewById(R.id.iv_despegarmenu); // Asegúrate de que la 'D' esté en mayúscula
        desplegarmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        rvClases.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        firestore = FirebaseFirestore.getInstance();
        // Obtener idCurso del intent
        idCurso = getIntent().getIntExtra("idCurso", 1);

        if (idCurso != -1) {
            obtenerCurso(idCurso);
        } else {
            Toast.makeText(this, "ID del curso no válido", Toast.LENGTH_SHORT).show();
            finish();
        }
        //___coso de estrella____________________

        tvPuntuacion = findViewById(R.id.tv_puntuacion);
        estrellas = new ImageView[5];

        estrellas[0] = findViewById(R.id.iv_1_estrella);
        estrellas[1] = findViewById(R.id.iv_2_estrella);
        estrellas[2] = findViewById(R.id.iv_3_estrella);
        estrellas[3] = findViewById(R.id.iv_4_estrella);
        estrellas[4] = findViewById(R.id.iv_5_estrella);
        // Establecer OnClickListener para cada estrella
        for (int i = 0; i < estrellas.length; i++) {
            final int estrellaIndex = i;
            estrellas[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actualizarPuntuacion(estrellaIndex + 1);
                }
            });
        }

        // Inicializar la puntuación en 0/5
        actualizarTextoPuntuacion();

        //---- comentarios ------
        crear_comentario=findViewById(R.id.btn_subir_comentario_curso);
        etcomentario=findViewById(R.id.et_comentario_vercurso);

        crear_comentario.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String coment = etcomentario.getText().toString().trim(); // CORREGIDO
            Timestamp fecha = Timestamp.now();

            if (user == null ) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                return;
            }
            if (coment.isEmpty()) {
                Toast.makeText(this, "Comentario vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            Comentario comentario = new Comentario();
            comentario.setUsuario(user.getEmail());
            comentario.setTexto(coment);
            comentario.setFecha(fecha);
            comentario.setIdCurso(idCurso);
            comentario.setIdClase(null);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("cursos")
                    .whereEqualTo("idCurso", idCurso)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            String docId = doc.getId();

                            // Obtener lista actual de comentarios
                            Curso curso = doc.toObject(Curso.class);
                            List<Comentario> comentarios = curso.getComentarios();
                            if (comentarios == null)
                                comentarios = new ArrayList<>();
                            Integer nuevaId = comentarios.size() + 1;
                            comentario.setIdComentario(nuevaId);
                            comentarios.add(comentario);

                            // Subir la nueva lista de comentarios
                            db.collection("cursos")
                                    .document(docId)
                                    .update("comentarios", comentarios)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Comentario agregado", Toast.LENGTH_SHORT).show();
                                        etcomentario.setText(""); // limpiar campo
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al subir comentario", Toast.LENGTH_SHORT).show();
                                    });
                            Toast.makeText(this, "Comentario agregado", Toast.LENGTH_SHORT).show();
                            etcomentario.setText(""); // limpiar campo

                            AdapterVerCursoVerComentarios adapter = new AdapterVerCursoVerComentarios(comentarios, idCurso);
                            rvComentarios.setAdapter(adapter);

                        } else {
                            Toast.makeText(this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al buscar el curso", Toast.LENGTH_SHORT).show();
                    });
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

                            if (curso.getComentarios() == null){
                                curso.setComentarios(new ArrayList<>());
                            }
                            AdapterVerCursoVerComentarios adapterVerCursoVerComentarios = new AdapterVerCursoVerComentarios(curso.getComentarios(),idCurso);
                            rvComentarios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                            rvComentarios.setAdapter(adapterVerCursoVerComentarios);

                            // Mostrar datos
                            tituloCurso.setText(curso.getTitulo());

                            if (curso.getCreador() != null && !curso.getCreador().isEmpty()) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                CollectionReference usersRef = db.collection("usuarios");
                                usersRef.whereEqualTo("correo", curso.getCreador())
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshotsuser -> {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                String nombre = queryDocumentSnapshots.getDocuments().get(0).getString("nombre");
                                                autorCurso.setText("Publicado por: " + nombre);
                                            } else {
                                                autorCurso.setText("Autor desconocido");
                                                Log.w("Firebase", "No se encontró autor con correo: " + curso.getCreador());
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firebase", "Error al obtener el nombre del autor", e);
                                            autorCurso.setText("Autor desconocido");
                                        });
                            } else {
                                autorCurso.setText("Autor desconocido");
                            }
                            if (curso.getDescripcion() == null || curso.getDescripcion().isEmpty()) {
                                descripcionCurso.setText("Curso sin descripción");
                            } else {
                                descripcionCurso.setText(curso.getDescripcion());
                            }

                            Glide.with(this)
                                    .load(curso.getImagen())
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.img_defaultclass)
                                    .into(imagenTitulo);

                            // Aquí puedes luego cargar clases del curso si quieres

                            ArrayList<Clase> listaClases = new ArrayList<>();

                            CollectionReference clasesRef = firestore.collection("clases");
                            Query clasesQuery = clasesRef.whereEqualTo("idCurso", idCurso).orderBy("fechaCreacionf", Query.Direction.ASCENDING);

                            clasesQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                for (QueryDocumentSnapshot doc1 : queryDocumentSnapshots1) {
                                    Clase clase = doc1.toObject(Clase.class);
                                    if (clase != null){
                                        listaClases.add(clase);
                                    }

                                }

                                AdapterVerClasesCursoOtroUsuario adapter = new AdapterVerClasesCursoOtroUsuario(listaClases, Ver_cursos.this);
                                rvClases.setAdapter(adapter);

                            }).addOnFailureListener(onFailureListener -> {
                                Toast.makeText(this, "Error al cargar clases", Toast.LENGTH_SHORT).show();
                                onFailureListener.printStackTrace();
                            });



                        }
                    } else {
                        Toast.makeText(this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener el curso", Toast.LENGTH_SHORT).show();
                });
    }

    //___________
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cursos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.it_denunciar) {
            Intent denuncia = new Intent(Ver_cursos.this, CrearDenuncia.class);
            denuncia.putExtra("idCurso",idCurso);
            startActivity(denuncia);
            return true;
        } else if (id == R.id.it_descargar) {
            Toast.makeText(this, "se supone que vas a ver lo de descargas", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.it_compartir) {
            Toast.makeText(this, "se supone que se comparte ", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showPopupMenu(View view) {
        // Crear el PopupMenu y asociarlo con la vista 'view' (la ImageView desplegarmenu)
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_cursos, popupMenu.getMenu()); // Usamos el mismo menú que para la ActionBar

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.it_denunciar) {
                Intent denuncia = new Intent(Ver_cursos.this, CrearDenuncia.class);
                denuncia.putExtra("idCurso",idCurso);
                startActivity(denuncia);


                return true;
            } else if (id == R.id.it_descargar) {
                Toast.makeText(this, "se supone que vas a ver lo de descargas", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.it_compartir) {
                Toast.makeText(this, "se supone que se comparte ", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false; // Importante devolver false si no se manejó el clic
        });

        // Mostrar el menú
        popupMenu.show();
    }
    //_______estrellas___
    private void actualizarPuntuacion(int nuevaPuntuacion) {
        if (nuevaPuntuacion >= 0 && nuevaPuntuacion <= 5) {
            puntuacionActual = nuevaPuntuacion;
            actualizarTextoPuntuacion();
            actualizarImagenesEstrellas();
        }
    }
    private void actualizarTextoPuntuacion() {
        tvPuntuacion.setText(puntuacionActual + " / 5");
    }

    private void actualizarImagenesEstrellas() {
        for (int i = 0; i < estrellas.length; i++) {
            if (i < puntuacionActual) {
                estrellas[i].setImageResource(R.drawable.estella_llena);
            } else {
                estrellas[i].setImageResource(R.drawable.estrella);
            }
        }
    }

}
