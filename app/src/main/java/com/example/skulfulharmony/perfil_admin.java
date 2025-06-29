package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class perfil_admin extends AppCompatActivity {

    private TextView tvNombreAdmin;
    private ImageView ivFotoAdmin;
    private Button btnCerrarSesion;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_admin);


        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referenciar vistas
        tvNombreAdmin = findViewById(R.id.tv_NombreUsuario);
        ivFotoAdmin = findViewById(R.id.ivProfilePicture);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Obtener y mostrar datos del admin
        cargarDatosAdmin();

        // Acción para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> startActivity(new Intent(perfil_admin.this, CerrarSesion.class)));

        // Barra inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacion1);
        bottomNavigationView.setSelectedItemId(R.id.menu_perfil);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_denuncias) {
                startActivity(new Intent(this, admi_denuncias.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.menu_estadisticas) {
                startActivity(new Intent(this, admi_populares.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.menu_perfil) {
                return true;
            }

            return false;
        });
    }

    private void cargarDatosAdmin() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("admins").document(user.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    tvNombreAdmin.setText(documentSnapshot.getString("nombre"));
                    // Para la imagen, si tienes URL, usa Glide o Picasso
                    // Glide.with(this).load(documentSnapshot.getString("foto")).into(ivFotoAdmin);
                } else {
                    tvNombreAdmin.setText("Administrador");
                }
            }).addOnFailureListener(e -> tvNombreAdmin.setText("Administrador"));
        }
    }
}