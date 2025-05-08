package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilMostrar extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_correo, tv_DescripcionUsuario, tv_No_Cursos, tv_Seguidores, tv_Seguido;
    private Button btnCompartirPerfil;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mostrar); // Asegúrate de que el layout esté correctamente configurado

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Obtener el ID del usuario autenticado
        } else {
            // Si el usuario no está autenticado, redirige al login
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
            return;
        }

        // Referencias UI
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        tv_DescripcionUsuario = findViewById(R.id.tv_DescripcionUsuario);
        tv_correo = findViewById(R.id.tv_correo);
        tv_Seguidores = findViewById(R.id.tv_Seguidores);
        tv_Seguido = findViewById(R.id.tv_Seguido);
        btnCompartirPerfil = findViewById(R.id.btnCompartirPerfil);

        cargarDatosUsuario();

        // Configurar el botón de compartir perfil
        btnCompartirPerfil.setOnClickListener(v -> compartirPerfil());
    }

    private void cargarDatosUsuario() {
        if (userId == null) return;

        // Obtener los datos del usuario desde Firestore
        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Cargar los datos del usuario en los TextViews
                tv_NombreUsuario.setText(documentSnapshot.getString("nombre"));
                tv_correo.setText(documentSnapshot.getString("correo"));
                tv_DescripcionUsuario.setText(documentSnapshot.getString("descripcion"));
                tv_No_Cursos.setText("Cursos Creados: " + documentSnapshot.getLong("cursos"));

                // Cargar seguidores y seguidos
                long seguidores = documentSnapshot.getLong("seguidores");
                long seguidos = documentSnapshot.getLong("seguidos");

                tv_Seguidores.setText("Seguidores: " + seguidores);
                tv_Seguido.setText("Seguidos: " + seguidos);

                // Cargar la foto de perfil desde la URL almacenada en Firestore
                String fotoUrl = documentSnapshot.getString("fotoPerfil");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .into(ivProfilePicture); // Se mantiene la foto incluso después de cerrar y abrir la app
                }
            } else {
                Toast.makeText(this, "No se encontró el perfil", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
    }

    // Método para compartir el perfil
    private void compartirPerfil() {
        // Simulando que tienes una URL del perfil del usuario
        String perfilUrl = "https://tuapp.com/perfil?usuario=" + userId;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Mira el perfil de este usuario: " + perfilUrl);

        // Verifica que hay aplicaciones que puedan manejar este Intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Compartir perfil"));
        } else {
            Toast.makeText(this, "No se puede compartir en este momento", Toast.LENGTH_SHORT).show();
        }
    }
}