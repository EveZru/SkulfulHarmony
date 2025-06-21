package com.example.skulfulharmony;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d("SplashActivity", "👤 Usuario detectado: " + user.getUid());

            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            Log.d("SplashActivity", "✅ Documento existe. Rol: " + rol);

                            String userId = user.getUid();
                            tiempoUsuario tiempo = new tiempoUsuario(userId, getApplicationContext());
                            tiempo.iniciarConteo();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    Log.d("SplashActivity", "🔔 Solicitando permiso para notificaciones");
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                                }
                            }

                            tiempo.registrarHoraEntrada();
                            MyApp.setContadorTiempo(tiempo);

                            if ("admin".equals(rol)) {
                                startActivity(new Intent(this, admi_populares.class));
                            } else {
                                startActivity(new Intent(this, Home.class));
                            }

                        } else {
                            Log.e("SplashActivity", "⛔ Documento del usuario no existe");
                            Toast.makeText(this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, IniciarSesion.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SplashActivity", "❌ Error al obtener datos: " + e.getMessage());
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, IniciarSesion.class));
                        finish();
                    });
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // ✅
                if (task.isSuccessful() && currentUser != null) {
                    String token = task.getResult();
                    FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(currentUser.getUid()) // ✅ Ya no truena
                            .update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d("SplashActivity", " Token actualizado en Splash"))
                            .addOnFailureListener(e -> Log.e("SplashActivity", " Error al guardar token en Splash", e));
                } else {
                    Log.e("SplashActivity", " Token fallido o usuario null al intentar guardar token");
                }
            });

        } else {
            Log.e("SplashActivity", "⚠️ No hay usuario logueado");
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        }



    }
}
