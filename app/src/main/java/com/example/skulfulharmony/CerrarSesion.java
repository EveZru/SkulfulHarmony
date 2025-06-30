package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.google.firebase.auth.FirebaseAuth;

public class CerrarSesion extends AppCompatActivity {
    private Button cancelar_cerrarsesion, cerrarsesion;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cerrarsesion);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los botones
        cancelar_cerrarsesion = findViewById(R.id.btncancelar_cerrarsesion);
        cerrarsesion = findViewById(R.id.btn_cerrarsesion); // 🔹 Corregido el ID

        // Manejar cierre de sesión
        cerrarsesion.setOnClickListener(view -> {
            mAuth.signOut(); // Cierra sesión en Firebase
            DbUser dbUser = new DbUser(this);
            dbUser.deleteUser();
            Intent intent = new Intent(CerrarSesion.this, IniciarSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Botón para cancelar y volver atrás
        cancelar_cerrarsesion.setOnClickListener(view -> finish());

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}