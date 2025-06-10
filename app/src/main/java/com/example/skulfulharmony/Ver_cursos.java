package com.example.skulfulharmony;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.example.skulfulharmony.modooffline.ClaseFirebase;
import com.example.skulfulharmony.modooffline.DropboxDownloader;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
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
    private Intent visualizaciones,calificaciones;

    //--- contador de visitas
    private final static String Prefers_name="nombreclase";
    private final static String Cuentas_Visitas="Contador";


    private FirebaseFirestore firestore;
    private int idCurso;
    private ImageView desplegarmenu;

    private ImageView[] estrellas;
    private TextView tvPuntuacion;
    private int puntuacionActual = 0;
    private static final int REQUEST_CODE_NOTIF = 1001;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("Ya entro al curso", "creando curso");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cursos);
        crearCanalNotificacion();
        pedirPermisoNotificaciones();


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
        Log.d("Ya entro al curso", "creando curso");
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

       // SharedPreferences sharedPreferences=getsa

        //---- contador de visitas-----
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cursos")
                .whereEqualTo("idCurso", idCurso)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String docId = doc.getId();

                        db.collection("cursos").document(docId)
                                .update("visitas", com.google.firebase.firestore.FieldValue.increment(1))
                                .addOnSuccessListener(aVoid -> Log.d("VISITAS", "Visita incrementada"))
                                .addOnFailureListener(e -> Log.w("VISITAS", "Error al incrementar visitas", e));
                    } else {
                        Log.w("VISITAS", "Curso no encontrado para incrementar visitas");
                    }
                })
                .addOnFailureListener(e -> Log.w("VISITAS", "Error buscando el curso", e));

// subir / actualizar  la popularidad a firebase
     //
        firestore.collection("cursos")
                .whereEqualTo("idCurso", idCurso)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        String docId = doc.getId();
                        Curso curso = doc.toObject(Curso.class);

                        double nuevaPopularidad = calcularPopularidad(curso);

                        firestore.collection("cursos").document(docId)
                                .update("popularidad", nuevaPopularidad)
                                .addOnSuccessListener(aVoid -> Log.d("POP", "Popularidad actualizada"))
                                .addOnFailureListener(e -> Log.e("POP", "Error al actualizar popularidad", e));
                    } else {
                        Log.e("POP", "Curso no encontrado para calcular popularidad");
                    }
                })
                .addOnFailureListener(e -> Log.e("POP", "Error al obtener curso para popularidad", e));

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
//cola
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

    //_____parte del menu _____
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
                descargarCursoCompletoFirestore();
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
    //_______estrellas_____
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

    private void crearCanalNotificacion() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_descarga",
                    "Descarga de cursos",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void pedirPermisoNotificaciones() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIF);
        }
    }

    private void mostrarProgresoCurso(String tituloCurso, int progreso) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_descargas")
                .setSmallIcon(R.drawable.ic_cloud_download)
                .setContentTitle("Descargando curso")
                .setContentText(tituloCurso)
                .setProgress(100, progreso, false)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat.from(this).notify(("CURSO_" + tituloCurso).hashCode(), builder.build());
    }

    private void mostrarFinalizadoCurso(String tituloCurso) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_descargas")
                .setSmallIcon(R.drawable.ic_check)
                .setContentTitle("Curso descargado")
                .setContentText("El curso '" + tituloCurso + "' está listo para usarse sin conexión.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(this).notify(("CURSO_" + tituloCurso).hashCode(), builder.build());
    }

    private void descargarCursoCompletoFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cursos")
                .whereEqualTo("idCurso", idCurso)
                .limit(1)
                .get()
                .addOnSuccessListener(cursoQuery -> {
                    if (!cursoQuery.isEmpty()) {
                        DocumentSnapshot cursoDoc = cursoQuery.getDocuments().get(0);
                        Curso curso = cursoDoc.toObject(Curso.class);
                        if (curso == null) {
                            Toast.makeText(this, "Error al leer el curso", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mostrarProgresoCurso(curso.getTitulo(), 0);

                        DbHelper dbHelper = new DbHelper(this);

                        // Guardamos el curso y recuperamos el ID insertado
                        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
                        ContentValues cursoValues = new ContentValues();
                        cursoValues.put("titulo", curso.getTitulo());
                        cursoValues.put("descripcion", curso.getDescripcion());
                        cursoValues.put("imagen", curso.getImagen());
                        long cursoLocalId = sqlDB.insert("cursodescargado", null, cursoValues);

                        if (cursoLocalId == -1) {
                            Toast.makeText(this, "Error guardando curso local", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.collection("clases")
                                .whereEqualTo("idCurso", idCurso)
                                .get()
                                .addOnSuccessListener(clasesQuery -> {
                                    List<DocumentSnapshot> documentos = clasesQuery.getDocuments();
                                    int total = documentos.size();
                                    if (total == 0) {
                                        mostrarFinalizadoCurso(curso.getTitulo());
                                        return;
                                    }

                                    int[] contador = {0};

                                    for (DocumentSnapshot claseDoc : documentos) {
                                        Clase clase = claseDoc.toObject(Clase.class);
                                        ClaseFirebase claseFirebase = new ClaseFirebase(
                                                clase.getTitulo(),
                                                clase.getContenido(),
                                                clase.getImagen(),
                                                clase.getVideoUrl()
                                        );

                                        String titulo = clase.getTitulo().replace(" ", "_");

                                        DropboxDownloader.descargarArchivo(this, clase.getImagen(), "img_" + titulo + ".jpg", new DropboxDownloader.Callback() {
                                            @Override
                                            public void onSuccess(File imagenLocal) {
                                                claseFirebase.setImagenUrl(imagenLocal.getAbsolutePath());

                                                DropboxDownloader.descargarArchivo(Ver_cursos.this, clase.getVideoUrl(), "video_" + titulo + ".mp4", new DropboxDownloader.Callback() {
                                                    @Override
                                                    public void onSuccess(File videoLocal) {
                                                        claseFirebase.setVideoUrl(videoLocal.getAbsolutePath());

                                                        dbHelper.guardarClaseDescargada(claseFirebase, (int) cursoLocalId);

                                                        contador[0]++;
                                                        mostrarProgresoCurso(curso.getTitulo(), (int) ((contador[0] / (float) total) * 100));

                                                        if (contador[0] == total) {
                                                            mostrarFinalizadoCurso(curso.getTitulo());
                                                            Toast.makeText(Ver_cursos.this, "Curso descargado correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Toast.makeText(Ver_cursos.this, "Error al descargar video de clase", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Toast.makeText(Ver_cursos.this, "Error al descargar imagen de clase", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al obtener clases", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener curso", Toast.LENGTH_SHORT).show();
                });
    }
    private double calcularPopularidad(Curso curso) {
        double alpha = 1.0, beta = 2.0, gamma = 3.0, epsilon = 0.5;

        double visitas = curso.getVisitas();
        double interacciones = 0;
        List<Comentario> comentarios = curso.getComentarios();
        if (comentarios != null) interacciones = comentarios.size();

        double calificaciones = 0;
        List<Integer> califs = curso.getCalificacionCursos();
        if (califs != null && !califs.isEmpty()) {
            int suma = 0;
            for (int c : califs) suma += c;
            calificaciones = (double) suma / califs.size();
        }

        double descargas = curso.getCantidadDescargas() != null ? curso.getCantidadDescargas() : 0;

        return alpha * visitas + beta * interacciones + gamma * calificaciones + epsilon * descargas;
    }


}