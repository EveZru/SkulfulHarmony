package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class eliminar_cuenta extends AppCompatActivity {

    private Button btnConfirmarEliminar, btnCancelarEliminar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eliminar_cuenta);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los botones del XML
        btnConfirmarEliminar = findViewById(R.id.btn_confirmar_eliminar);
        btnCancelarEliminar = findViewById(R.id.btn_cancelar_eliminar);

        // Listener para eliminar la cuenta
        btnConfirmarEliminar.setOnClickListener(v -> eliminarCuenta());

        // Listener para cancelar y volver atrás
        btnCancelarEliminar.setOnClickListener(v -> finish());

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void eliminarCuenta() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(eliminar_cuenta.this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show();

                    // Cerrar sesión y redirigir a login
                    mAuth.signOut();
                    Intent intent = new Intent(eliminar_cuenta.this, login.class);
                    startActivity(intent);
                    finish(); // Cierra esta actividad
                } else {
                    Toast.makeText(eliminar_cuenta.this, "Error al eliminar la cuenta: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}