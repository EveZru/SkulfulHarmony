package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterCursosUsuario;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilMostrar extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_correo, tv_DescripcionUsuario, tv_No_Cursos, tv_Seguidores, tv_Seguido;
    private Button btnCompartirPerfil;

    ImageView ivProfilePic;
    TextView tvNombreUsuario, tvCorreo, tvDescripcion, tvNoCursos, tvSeguidores, tvSeguidos;
    Button btnSeguirUsuario;
    RecyclerView rvCursosUsuario;

    Usuario usuarioPerfil; // Usuario cuyo perfil se muestra
    FirebaseAuth auth = FirebaseAuth.getInstance();

    boolean siguiendo = false; // estado seguimiento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mostrar);

        db = FirebaseFirestore.getInstance();

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        tv_DescripcionUsuario = findViewById(R.id.tv_DescripcionUsuario);
        tv_correo = findViewById(R.id.tv_correo);
        tv_Seguidores = findViewById(R.id.tv_Seguidores);
        tv_Seguido = findViewById(R.id.tv_Seguido);
        btnCompartirPerfil = findViewById(R.id.btnCompartirPerfil);

        // Componentes para perfil con seguimiento y cursos
        ivProfilePic = ivProfilePicture;
        tvNombreUsuario = tv_NombreUsuario;
        tvCorreo = tv_correo;
        tvDescripcion = tv_DescripcionUsuario;
        tvNoCursos = tv_No_Cursos;
        tvSeguidores = tv_Seguidores;
        tvSeguidos = tv_Seguido;
        btnSeguirUsuario = findViewById(R.id.btnSeguirUsuario);
        rvCursosUsuario = findViewById(R.id.rvCursosUsuario);
        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);

        // Intent puede enviar usuario completo o solo ID, aquí comprobamos
        usuarioPerfil = (Usuario) getIntent().getSerializableExtra("usuario");
        String usuarioId = getIntent().getStringExtra("usuarioId");

        if (usuarioPerfil != null) {
            mostrarDatosUsuarioCompleto(usuarioPerfil);
        } else if (usuarioId != null) {
            cargarDatosUsuario(usuarioId);
        } else {
            Toast.makeText(this, "Error: usuario no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (usuarioPerfil == null && usuarioId != null) {
            // Si no tenemos objeto Usuario pero sí ID, cargamos después y asignamos usuarioPerfil en cargarDatosUsuario
            usuarioPerfil = new Usuario();
            usuarioPerfil.setCorreo(usuarioId); // Para usar en carga de cursos
        }

        // Mostrar cursos y botón seguir solo si tenemos usuarioPerfil con correo
        if (usuarioPerfil != null && usuarioPerfil.getCorreo() != null) {
            rvCursosUsuario.setLayoutManager(new LinearLayoutManager(this));
            cargarCursosUsuario(usuarioPerfil.getCorreo());

            String correoActual = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
            if (correoActual != null) {
                // Aquí verificamos si ya sigue antes de asignar el listener
                verificarSiSigue(correoActual, usuarioPerfil.getCorreo());

                btnSeguirUsuario.setOnClickListener(v -> {
                    if (!siguiendo) {
                        seguirUsuarioPorNombre(correoActual, usuarioPerfil.getNombre());
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Dejar de seguir")
                                .setMessage("¿Estás seguro que quieres dejar de seguir a " + usuarioPerfil.getNombre() + "?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    dejarDeSeguir(correoActual, usuarioPerfil.getCorreo());
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });
            }
        } else {
            btnSeguirUsuario.setVisibility(Button.GONE);
            rvCursosUsuario.setVisibility(RecyclerView.GONE);
        }

        btnCompartirPerfil.setOnClickListener(v -> compartirPerfil());


//        if (bottomNavigationView1 != null) {
//            // Configurar el listener para los ítems seleccionados
//            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
//                int itemId = item.getItemId();
//                if (itemId == R.id.it_homme) {
//                    // Acción para Home
//                    startActivity(new Intent(PerfilMostrar.this, Home.class));
//                    return true;
//                } else if (itemId == R.id.it_new) {
//                    // Navegar a la actividad para crear un curso
//                    startActivity(new Intent(PerfilMostrar.this, VerCursosCreados.class));
//                    return true;
//                } else if (itemId == R.id.it_seguidos) {
//                    // Navegar a la actividad para ver la Biblioteca
//                    startActivity(new Intent(PerfilMostrar.this, Biblioteca.class));
//                    return true;
//                } else if (itemId == R.id.it_perfil) {
//                    // Navegar a la actividad para buscar perfiles
//                    startActivity(new Intent(PerfilMostrar.this, Perfil.class));
//                    return true;
//                }
//                return false;
//            });
//            // Establecer el ítem seleccionado al inicio (si es necesario)
//            bottomNavigationView1.setSelectedItemId(R.id.it_homme);
//        } else {
//            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
//        }
    }

    private void mostrarDatosUsuarioCompleto(Usuario usuario) {
        Glide.with(this)
                .load(usuario.getFotoPerfil())
                .placeholder(R.drawable.default_profile)
                .into(ivProfilePic);

        tvNombreUsuario.setText(usuario.getNombre() != null ? usuario.getNombre() : "Sin Nombre");
        tvCorreo.setText(usuario.getCorreo() != null ? usuario.getCorreo() : "Sin correo");
        tvDescripcion.setText(usuario.getDescripcion() != null ? usuario.getDescripcion() : "Sin descripción");
        tvNoCursos.setText("Cursos Creados: " + usuario.getCursos());
        tvSeguidores.setText("Seguidores: " + usuario.getSeguidores());
        tvSeguidos.setText("Seguidos: " + usuario.getSeguidos());
    }

    private void cargarDatosUsuario(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String nombre = documentSnapshot.getString("nombre");
                String correo = documentSnapshot.getString("correo");
                String descripcion = documentSnapshot.getString("descripcion");
                Long cursos = documentSnapshot.getLong("cursos");
                Long seguidores = documentSnapshot.getLong("seguidores");
                Long seguidos = documentSnapshot.getLong("seguidos");
                String fotoUrl = documentSnapshot.getString("fotoPerfil");

                tvNombreUsuario.setText(nombre);
                tvCorreo.setText(correo);
                tvDescripcion.setText(descripcion);
                tvNoCursos.setText("Cursos Creados: " + (cursos != null ? cursos : 0));
                tvSeguidores.setText("Seguidores: " + (seguidores != null ? seguidores : 0));
                tvSeguidos.setText("Seguidos: " + (seguidos != null ? seguidos : 0));

                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .into(ivProfilePic);
                }

                // Crear objeto Usuario para el resto de funciones
                usuarioPerfil = new Usuario();
                usuarioPerfil.setNombre(nombre);
                usuarioPerfil.setCorreo(correo);
                usuarioPerfil.setDescripcion(descripcion);
                usuarioPerfil.setCursos(cursos != null ? cursos.intValue() : 0);
                usuarioPerfil.setSeguidores(seguidores != null ? seguidores.intValue() : 0);
                usuarioPerfil.setSeguidos(seguidos != null ? seguidos.intValue() : 0);
                usuarioPerfil.setFotoPerfil(fotoUrl);

                // Cambiar el LayoutManager para horizontal
                rvCursosUsuario.setLayoutManager(
                        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                );
                cargarCursosUsuario(correo);

                String correoActual = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
                if (correoActual != null) {
                    verificarSiSigue(correoActual, correo);
                }

                btnSeguirUsuario.setOnClickListener(v -> {
                    if (!siguiendo) {
                        seguirUsuarioPorNombre(correoActual, usuarioPerfil.getNombre());
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Dejar de seguir")
                                .setMessage("¿Estás seguro que quieres dejar de seguir a " + usuarioPerfil.getNombre() + "?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    dejarDeSeguir(correoActual, usuarioPerfil.getCorreo());
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

            } else {
                Toast.makeText(this, "No se encontró el perfil", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void compartirPerfil() {
        String usuarioId = usuarioPerfil != null && usuarioPerfil.getCorreo() != null ? usuarioPerfil.getCorreo() : "";
        String perfilUrl = "https://tuapp.com/perfil?usuario=" + usuarioId;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Mira el perfil de este usuario: " + perfilUrl);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Compartir perfil"));
        } else {
            Toast.makeText(this, "No se puede compartir en este momento", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCursosUsuario(String correoUsuario) {
        Log.d("PerfilMostrar", "Buscando cursos creados por: " + correoUsuario);
        db.collection("cursos")
                .whereEqualTo("creador", correoUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Curso> cursos = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Curso curso = doc.toObject(Curso.class);
                        cursos.add(curso);
                    }
                    Log.d("PerfilMostrar", "Cursos encontrados: " + cursos.size());
                    AdapterCursosUsuario adapter = new AdapterCursosUsuario(this, cursos);
                    rvCursosUsuario.setAdapter(adapter);
                    rvCursosUsuario.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                    Log.e("PerfilMostrar", "Error al cargar cursos", e);
                });
    }

    private void verificarSiSigue(String correoActual, String correoPerfilBuscado) {
        db.collection("usuarios").document(correoActual)
                .collection("seguidosname")
                .document(correoPerfilBuscado) // Si el ID del documento es el correo
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        siguiendo = task.getResult().exists();
                        Log.d("DEBUG", "Documento existe? " + siguiendo);
                    } else {
                        Log.e("ERROR", "Error al verificar", task.getException());
                        siguiendo = false;
                    }
                    actualizarBotonSeguir();
                });
    }

    private void actualizarBotonSeguir() {
        if (siguiendo) {
            btnSeguirUsuario.setText("Eliminar seguimiento");
            btnSeguirUsuario.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            btnSeguirUsuario.setText("Seguir");
            btnSeguirUsuario.setBackgroundColor(getResources().getColor(R.color.verde)); // Cambia a tu color rojo
        }
    }

    private void seguirUsuarioPorNombre(String correoActual, String nombrePerfilBuscado) {
        Log.d("PerfilMostrar", "Inicia seguirUsuarioPorNombre: correoActual=" + correoActual + ", nombrePerfilBuscado=" + nombrePerfilBuscado);

        db.collection("usuarios")
                .whereEqualTo("nombre", nombrePerfilBuscado)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot docPerfil = querySnapshot.getDocuments().get(0);
                        String correoPerfil = docPerfil.getString("correo");
                        String nombrePerfil = docPerfil.getString("nombre");

                        db.collection("usuarios")
                                .whereEqualTo("correo", correoActual)
                                .get()
                                .addOnSuccessListener(querySnapshotActual -> {
                                    if (querySnapshotActual.isEmpty()) {
                                        Toast.makeText(this, "Tu usuario no existe en Firestore", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    DocumentSnapshot docActual = querySnapshotActual.getDocuments().get(0);
                                    String idDocActual = docActual.getId();
                                    DocumentReference refActual = db.collection("usuarios").document(idDocActual);
                                    DocumentReference refPerfil = db.collection("usuarios").document(docPerfil.getId());

                                    // Datos para agregar en arrays
                                    Map<String, Object> seguidorData = new HashMap<>();
                                    seguidorData.put("nombre", docActual.getString("nombre") != null ? docActual.getString("nombre") : correoActual);
                                    seguidorData.put("correo", correoActual);

                                    Map<String, Object> seguidoData = new HashMap<>();
                                    seguidoData.put("nombre", nombrePerfil);
                                    seguidoData.put("correo", correoPerfil);

                                    // Actualizar arrays con arrayUnion (agrega sin borrar)
                                    refPerfil.update("seguidoresname", FieldValue.arrayUnion(seguidorData));
                                    refActual.update("seguidosname", FieldValue.arrayUnion(seguidoData));

                                    // Actualizar contadores seguidores y seguidos
                                    WriteBatch batch = db.batch();
                                    batch.update(refPerfil, "seguidores", FieldValue.increment(1));
                                    batch.update(refActual, "seguidos", FieldValue.increment(1));
                                    batch.commit()
                                            .addOnSuccessListener(aVoid -> {
                                                siguiendo = true;
                                                actualizarBotonSeguir();
                                                Toast.makeText(this, "Ahora sigues a " + nombrePerfil, Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al seguir usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                Log.e("PerfilMostrar", "Error seguir usuario", e);
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al verificar documento de usuario actual", Toast.LENGTH_LONG).show();
                                    Log.e("PerfilMostrar", "Error buscando usuario actual por correo", e);
                                });

                    } else {
                        Toast.makeText(this, "No se encontró usuario con ese nombre", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("PerfilMostrar", "Error buscando usuario por nombre", e);
                });
    }


    private void dejarDeSeguir(String correoActual, String correoPerfil) {
        DocumentReference refPerfil = db.collection("usuarios").document(correoPerfil);
        DocumentReference refActual = db.collection("usuarios").document(correoActual);

        WriteBatch batch = db.batch();
        batch.update(refPerfil, "seguidores", FieldValue.increment(-1));
        batch.update(refActual, "seguidos", FieldValue.increment(-1));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    db.collection("usuarios").document(correoPerfil)
                            .collection("seguidoresname").document(correoActual)
                            .delete();

                    db.collection("usuarios").document(correoActual)
                            .collection("seguidosname").document(correoPerfil)
                            .delete();

                    siguiendo = false;
                    actualizarBotonSeguir();
                    Toast.makeText(this, "Has dejado de seguir a este usuario", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al dejar de seguir", Toast.LENGTH_SHORT).show());
    }
}