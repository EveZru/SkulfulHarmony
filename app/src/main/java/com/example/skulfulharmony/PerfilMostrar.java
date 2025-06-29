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
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    Usuario usuarioPerfil;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    boolean siguiendo = false;

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

                    AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(cursos, this);
                    rvCursosUsuario.setAdapter(adapter);
                    rvCursosUsuario.setVisibility(View.VISIBLE);
                    rvCursosUsuario.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Si quieres horizontal
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                    Log.e("PerfilMostrar", "Error al cargar cursos", e);
                });
    }


    private void verificarSiSigue(String correoActual, String correoPerfilBuscado) {
        db.collection("usuarios")
                .whereEqualTo("correo", correoActual)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot docUsuario = querySnapshot.getDocuments().get(0);

                        // Opción 1: Si usas lista de seguidos en el documento
                        if (docUsuario.contains("seguidosname")) {
                            List<Map<String, String>> seguidos = (List<Map<String, String>>) docUsuario.get("seguidosname");
                            siguiendo = false;

                            if (seguidos != null) {
                                for (Map<String, String> seguido : seguidos) {
                                    if (correoPerfilBuscado.equals(seguido.get("correo"))) {
                                        siguiendo = true;
                                        break;
                                    }
                                }
                            }
                        }
                        // Opción 2: Si usas subcolección (mejor para muchos seguidos)
                        else {
                            docUsuario.getReference()
                                    .collection("seguidosname")
                                    .document(correoPerfilBuscado)
                                    .get()
                                    .addOnSuccessListener(seguidoDoc -> {
                                        siguiendo = seguidoDoc.exists();
                                        actualizarBotonSeguir();
                                    })
                                    .addOnFailureListener(e -> {
                                        siguiendo = false;
                                        actualizarBotonSeguir();
                                    });
                            return;
                        }

                        actualizarBotonSeguir();
                    } else {
                        siguiendo = false;
                        actualizarBotonSeguir();
                    }
                })
                .addOnFailureListener(e -> {
                    siguiendo = false;
                    actualizarBotonSeguir();
                    Log.e("PerfilMostrar", "Error al verificar seguimiento", e);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!user.getEmail().equalsIgnoreCase(correoActual)) {
            Toast.makeText(this, "No tienes permisos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Busca documento perfil a dejar de seguir
        db.collection("usuarios")
                .whereEqualTo("correo", correoPerfil)
                .get()
                .addOnSuccessListener(queryPerfil -> {
                    if (queryPerfil.isEmpty()) {
                        Toast.makeText(this, "No se encontró el perfil a dejar de seguir", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DocumentSnapshot docPerfil = queryPerfil.getDocuments().get(0);
                    String idDocPerfil = docPerfil.getId();
                    String nombrePerfil = docPerfil.getString("nombre");


                    db.collection("usuarios")
                            .whereEqualTo("correo", correoActual)
                            .get()
                            .addOnSuccessListener(queryActual -> {
                                if (queryActual.isEmpty()) {
                                    Toast.makeText(this, "No se encontró tu usuario", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DocumentSnapshot docActual = queryActual.getDocuments().get(0);
                                String idDocActual = docActual.getId();
                                String nombreActual = docActual.getString("nombre");

                                Map<String, Object> seguidorData = new HashMap<>();
                                seguidorData.put("nombre", nombreActual != null ? nombreActual : correoActual);
                                seguidorData.put("correo", correoActual);

                                Map<String, Object> seguidoData = new HashMap<>();
                                seguidoData.put("nombre", nombrePerfil != null ? nombrePerfil : correoPerfil);
                                seguidoData.put("correo", correoPerfil);

                                DocumentReference refPerfil = db.collection("usuarios").document(idDocPerfil);
                                DocumentReference refActual = db.collection("usuarios").document(idDocActual);

                                WriteBatch batch = db.batch();

                                batch.update(refPerfil, "seguidoresname", FieldValue.arrayRemove(seguidorData));
                                batch.update(refActual, "seguidosname", FieldValue.arrayRemove(seguidoData));

                                batch.update(refPerfil, "seguidores", FieldValue.increment(-1));
                                batch.update(refActual, "seguidos", FieldValue.increment(-1));

                                batch.commit().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, "Dejaste de seguir al usuario", Toast.LENGTH_SHORT).show();

                                        // Aquí reiniciamos la actividad para refrescar la vista
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);

                                    } else {
                                        Log.e("FirestoreError", "Error al dejar de seguir: ", task.getException());
                                        Toast.makeText(this, "Error al dejar de seguir", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Error buscando tu usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("FirestoreError", "Error buscando usuario actual", e);
                            });

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error buscando perfil a dejar de seguir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error buscando perfil", e);
                });
    }

}