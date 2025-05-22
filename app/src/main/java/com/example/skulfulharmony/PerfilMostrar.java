package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilMostrar extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_correo, tv_DescripcionUsuario, tv_No_Cursos, tv_Seguidores, tv_Seguido;
    private Button btnCompartirPerfil;
    private String usuarioId;

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

        usuarioId = getIntent().getStringExtra("usuarioId");

        if (usuarioId == null) {
            Toast.makeText(this, "ID de usuario no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDatosUsuario(usuarioId);

        btnCompartirPerfil.setOnClickListener(v -> compartirPerfil());
    }

    private void cargarDatosUsuario(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tv_NombreUsuario.setText(documentSnapshot.getString("nombre"));
                tv_correo.setText(documentSnapshot.getString("correo"));
                tv_DescripcionUsuario.setText(documentSnapshot.getString("descripcion"));
                Long cursos = documentSnapshot.getLong("cursos");
                tv_No_Cursos.setText("Cursos Creados: " + (cursos != null ? cursos : 0));

                Long seguidores = documentSnapshot.getLong("seguidores");
                Long seguidos = documentSnapshot.getLong("seguidos");
                tv_Seguidores.setText("Seguidores: " + (seguidores != null ? seguidores : 0));
                tv_Seguido.setText("Seguidos: " + (seguidos != null ? seguidos : 0));

                String fotoUrl = documentSnapshot.getString("fotoPerfil");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .into(ivProfilePicture);
                }
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
}