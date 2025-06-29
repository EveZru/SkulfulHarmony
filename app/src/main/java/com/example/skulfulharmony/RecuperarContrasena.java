package com.example.skulfulharmony;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {
    private Button btnrecuperar, btncancelar;
    private FirebaseAuth mAuth;
    private EditText editTextCorreo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperarcontrasena);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editTextCorreo = findViewById(R.id.editTextCorreo);
        btnrecuperar = findViewById(R.id.btn_enviar_correo);
        btncancelar = findViewById(R.id.btn_cancelar_recuperar);

        // Acción del botón "Recuperar contraseña"
        btnrecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextCorreo.getText().toString();
                if (!email.isEmpty() && email.contains("@")
                && !email.equals("") && email.contains(".")) {
                    enviarCorreoRecuperacion(email);
                } else {
                    Toast.makeText(RecuperarContrasena.this, "Por favor, introduce un correo válido", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Acción del botón "Cancelar"
        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Cerrar la actividad de recuperación de contraseña
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Cerrar la actividad de recuperación de contraseña
            }
        });

    }


    private void enviarCorreoRecuperacion(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecuperarContrasena.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecuperarContrasena.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}