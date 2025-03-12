package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarPerfil extends AppCompatActivity {
    private EditText et_nuevoNombre, et_nuevaDescripcion;
    private Button btn_actualizar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editarperfil);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Inicializar los componentes
        et_nuevoNombre = findViewById(R.id.et_nombrecambiar);
        et_nuevaDescripcion = findViewById(R.id.et_nuevadescripcion);
        btn_actualizar = findViewById(R.id.btn_cambiardatos);

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Evento para actualizar los datos del perfil
        btn_actualizar.setOnClickListener(v -> {
            String nuevoNombre = et_nuevoNombre.getText().toString().trim();
            String nuevaDescripcion = et_nuevaDescripcion.getText().toString().trim();

            if (!nuevoNombre.isEmpty() && !nuevaDescripcion.isEmpty()) {
                actualizarPerfil(nuevoNombre, nuevaDescripcion);
            } else {
                Toast.makeText(EditarPerfil.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPerfil(String nuevoNombre, String nuevaDescripcion) {
        if (userId == null) return;

        // Actualizar los datos en Firestore
        db.collection("usuarios").document(userId)
                .update("nombre", nuevoNombre, "descripcion", nuevaDescripcion)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarPerfil.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    finish();  // Cierra la actividad después de la actualización
                })
                .addOnFailureListener(e -> Toast.makeText(EditarPerfil.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show());
    }

}