package com.example.skulfulharmony;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContr extends AppCompatActivity {
    private Button btnrecuperar, btncancelar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contr);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnrecuperar = findViewById(R.id.btnrecuperarCont);
        btncancelar = findViewById(R.id.btncancelar_recuperar);

        // Acción del botón "Recuperar contraseña"
        btnrecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoRecuperarContraseña();
            }
        });

        // Acción del botón "Cancelar"
        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Cerrar la actividad de recuperación de contraseña
            }
        });
    }

    private void mostrarDialogoRecuperarContraseña() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");

        // Crear un campo de entrada para el correo
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Botón "Enviar"
        builder.setPositiveButton("Enviar", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                if (!email.isEmpty()) {
                    enviarCorreoRecuperacion(email);
                } else {
                    Toast.makeText(RecuperarContr.this, "Por favor, introduce tu correo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón "Cancelar"
        builder.setNegativeButton("Cancelar", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void enviarCorreoRecuperacion(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecuperarContr.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecuperarContr.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}