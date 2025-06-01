package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


public class EliminarCuenta extends AppCompatActivity {

    private Button btnContinuar, btnEliminarCuenta, btnCancelarEliminar;
    private EditText editTextMotivo, editTextPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminarcuenta);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnContinuar = findViewById(R.id.btn_continuar);
        btnEliminarCuenta = findViewById(R.id.btn_eliminar_cuenta);
        btnCancelarEliminar = findViewById(R.id.btn_cancelar_eliminar);
        editTextMotivo = findViewById(R.id.editTextMotivo);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Al inicio, oculta el EditText, contraseña y botón eliminar
        editTextMotivo.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
        btnEliminarCuenta.setVisibility(View.GONE);

        btnContinuar.setOnClickListener(v -> {
            // Muestra el campo para escribir motivo, contraseña y botón eliminar
            editTextMotivo.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);
            btnEliminarCuenta.setVisibility(View.VISIBLE);
            btnContinuar.setVisibility(View.GONE);
        });

        btnEliminarCuenta.setOnClickListener(v -> {
            String motivo = editTextMotivo.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String email = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "";

            if (motivo.length() < 50) {
                Toast.makeText(this, "Debes escribir al menos 50 caracteres explicando el motivo", Toast.LENGTH_LONG).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Debes ingresar tu contraseña para confirmar", Toast.LENGTH_LONG).show();
                return;
            }
            reautenticarYEliminarCuenta(email, password, motivo);
        });

        btnCancelarEliminar.setOnClickListener(v -> finish());
    }

    private void reautenticarYEliminarCuenta(String email, String password, String motivo) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Ya autenticado, ahora guarda el motivo y elimina la cuenta
                    subirMotivoYEliminar(user, motivo);
                } else {
                    Toast.makeText(this, "Error al re-autenticar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void subirMotivoYEliminar(FirebaseUser user, String motivo) {
        String email = user.getEmail();

        Map<String, Object> razonMap = new HashMap<>();
        razonMap.put("correo", email);
        razonMap.put("razón", motivo);

        Map<String, Object> docData = new HashMap<>();
        docData.put("cuenta", Arrays.asList(razonMap));

        db.collection("cuentas_eliminadas")
                .add(docData)
                .addOnSuccessListener(documentReference -> {
                    user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            startActivity(new Intent(this, IniciarSesion.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Error al eliminar la cuenta: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar motivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}