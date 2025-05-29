package com.example.skulfulharmony;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.WriteBatch;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class perfil_mostrar extends AppCompatActivity {

    ImageView ivProfilePicture;
    TextView tvNombreUsuario, tvCorreo, tvDescripcion, tvNoCursos, tvSeguidores, tvSeguidos;
    Button btnSeguirUsuario;
    RecyclerView rvCursosUsuario;

    Usuario usuarioPerfil; // Usuario cuyo perfil se muestra
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    boolean siguiendo = false; // estado seguimiento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mostrar);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvNombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tvCorreo = findViewById(R.id.tv_correo);
        tvDescripcion = findViewById(R.id.tv_DescripcionUsuario);
        tvNoCursos = findViewById(R.id.tv_No_Cursos);
        tvSeguidores = findViewById(R.id.tv_Seguidores);
        tvSeguidos = findViewById(R.id.tv_Seguido);
        btnSeguirUsuario = findViewById(R.id.btnSeguirUsuario);
        rvCursosUsuario = findViewById(R.id.rvCursosUsuario);

        usuarioPerfil = (Usuario) getIntent().getSerializableExtra("usuario");

        if (usuarioPerfil == null) {
            Toast.makeText(this, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String correoActual = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
        if (correoActual == null) {
            Toast.makeText(this, "Error: usuario actual no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mostrar datos usuario
        Glide.with(this)
                .load(usuarioPerfil.getFotoPerfil())
                .placeholder(R.drawable.default_profile)
                .into(ivProfilePicture);

        tvNombreUsuario.setText(usuarioPerfil.getNombre() != null ? usuarioPerfil.getNombre() : "Sin Nombre");
        tvCorreo.setText(usuarioPerfil.getCorreo() != null ? usuarioPerfil.getCorreo() : "Sin correo");
        tvDescripcion.setText(usuarioPerfil.getDescripcion() != null ? usuarioPerfil.getDescripcion() : "Sin descripción");
        tvNoCursos.setText("Cursos Creados: " + usuarioPerfil.getCursos());

        // Mostrar contadores seguidores y seguidos
        tvSeguidores.setText("Seguidores: " + usuarioPerfil.getSeguidores());
        tvSeguidos.setText("Seguidos: " + usuarioPerfil.getSeguidos());

        // Configurar RecyclerView de cursos hechos por el usuario
        rvCursosUsuario.setLayoutManager(new LinearLayoutManager(this));
        cargarCursosUsuario(usuarioPerfil.getCorreo());

        // Verificar si el usuario actual ya sigue al usuarioPerfil
        verificarSiSigue(correoActual, usuarioPerfil.getCorreo());

        // Botón seguir
        btnSeguirUsuario.setOnClickListener(v -> {
            if (!siguiendo) {
                seguirUsuario(correoActual, usuarioPerfil);
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

    private void cargarCursosUsuario(String correoUsuario) {
        db.collection("cursos")
                .whereEqualTo("creadorCorreo", correoUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Curso> cursos = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Curso curso = doc.toObject(Curso.class);
                        cursos.add(curso);
                    }
                    AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(cursos, this);
                    rvCursosUsuario.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar cursos", Toast.LENGTH_SHORT).show());
    }

    private void verificarSiSigue(String correoActual, String correoPerfil) {
        db.collection("usuarios").document(correoActual)
                .collection("seguidosname").document(correoPerfil)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    siguiendo = documentSnapshot.exists();
                    actualizarBotonSeguir();
                })
                .addOnFailureListener(e -> {
                    siguiendo = false;
                    actualizarBotonSeguir();
                });
    }

    private void actualizarBotonSeguir() {
        if (siguiendo) {
            btnSeguirUsuario.setText("Seguido");
            btnSeguirUsuario.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            btnSeguirUsuario.setText("Seguir");
            btnSeguirUsuario.setBackgroundColor(getResources().getColor(R.color.rojo)); // Cambia a tu rojo
        }
    }

    private void seguirUsuario(String correoActual, Usuario usuarioPerfil) {
        String correoPerfil = usuarioPerfil.getCorreo();
        String nombrePerfil = usuarioPerfil.getNombre();
        String nombreActual = auth.getCurrentUser().getDisplayName() != null ? auth.getCurrentUser().getDisplayName() : correoActual;

        DocumentReference refPerfil = db.collection("usuarios").document(correoPerfil);
        DocumentReference refActual = db.collection("usuarios").document(correoActual);

        // Corregido: crear WriteBatch explícitamente
        WriteBatch batch = db.batch();
        batch.update(refPerfil, "seguidores", FieldValue.increment(1));
        batch.update(refActual, "seguidos", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> dataSeguidor = new HashMap<>();
                    dataSeguidor.put("nombre", nombreActual);
                    dataSeguidor.put("correo", correoActual);

                    Map<String, Object> dataSeguido = new HashMap<>();
                    dataSeguido.put("nombre", nombrePerfil);
                    dataSeguido.put("correo", correoPerfil);

                    db.collection("usuarios").document(correoPerfil)
                            .collection("seguidoresname").document(correoActual)
                            .set(dataSeguidor);

                    db.collection("usuarios").document(correoActual)
                            .collection("seguidosname").document(correoPerfil)
                            .set(dataSeguido);

                    siguiendo = true;
                    actualizarBotonSeguir();
                    Toast.makeText(this, "Ahora sigues a " + nombrePerfil, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al seguir usuario", Toast.LENGTH_SHORT).show());
    }

    private void dejarDeSeguir(String correoActual, String correoPerfil) {
        DocumentReference refPerfil = db.collection("usuarios").document(correoPerfil);
        DocumentReference refActual = db.collection("usuarios").document(correoActual);

        // Corregido: crear WriteBatch explícitamente
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