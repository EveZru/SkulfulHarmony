package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.adapters.AdapterVerClasesCursoOtroUsuario;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Ver_cursos extends AppCompatActivity {

    private ImageView imagenTitulo;
    private TextView tituloCurso, descripcionCurso, autorCurso;
    private RecyclerView rvClases; // Puedes llenar esto después con clases del curso

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
        idCurso = getIntent().getIntExtra("idCurso", -1);

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
                            autorCurso.setText(curso.getCreador()); //Corregir pq esto muestra el correo del creador
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

                            ArrayList<Clase> listaClases = new ArrayList<>();

                            CollectionReference clasesRef = firestore.collection("clases");
                            Query clasesQuery = clasesRef.whereEqualTo("idCurso", idCurso).orderBy("fechaCreacionf", Query.Direction.ASCENDING);

                            clasesQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                for (QueryDocumentSnapshot doc1 : queryDocumentSnapshots1) {
                                    Clase clase = doc1.toObject(Clase.class);
                                    if (clase != null)
                                    listaClases.add(clase);
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
            startActivity(new Intent(Ver_cursos.this, CrearDenuncia.class));
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
                startActivity(new Intent(Ver_cursos.this, CrearDenuncia.class));
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
