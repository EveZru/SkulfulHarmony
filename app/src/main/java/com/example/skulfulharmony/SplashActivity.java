package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Usuario autenticado, verificamos su rol
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            if ("admin".equals(rol)) {
                                startActivity(new Intent(this, admi_populares.class));
                            } else {
                                startActivity(new Intent(this, Home.class));
                            }

                            // INICIA EL CONTADOR GLOBAL AL INICIAR LA APP
                            String userId = user.getUid(); // Obtienes el userId del usuario logueado
                            MyApp.getContadorTiempo();  // Esto asegura que el tiempo siga corriendo en toda la app

                        } else {
                            Toast.makeText(this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, IniciarSesion.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, IniciarSesion.class));
                        finish();
                    });

        } else {
            // No hay sesión, redirige al login
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        }
    }
}
