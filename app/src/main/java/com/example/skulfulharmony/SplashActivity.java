package com.example.skulfulharmony;

import static com.example.skulfulharmony.MyApp.hayInternet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.example.skulfulharmony.modooffline.ClaseFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                            tiempo.calcularPromediosEntradaPorSemana();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    Log.d("SplashActivity", " Solicitando permiso para notificaciones");
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                                }
                            }

                            tiempo.registrarHoraEntrada();
                            tiempo.registrarFechaUltimaEntrada();
                            MyApp.setContadorTiempo(tiempo);

                            if (hayInternet(this)) {
                                DbHelper dbHelper = new DbHelper(this);
                                sincronizarProgresoOffline(dbHelper, user.getUid());
                            }

                            if ("admin".equals(rol)) {
                                startActivity(new Intent(this, admi_populares.class));
                            } else {
                                startActivity(new Intent(this, Home.class));
                            }

                        } else {
                            Log.e("SplashActivity", " Documento del usuario no existe");
                            Toast.makeText(this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, IniciarSesion.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SplashActivity", " Error al obtener datos: " + e.getMessage());
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, IniciarSesion.class));
                        finish();
                    });

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (task.isSuccessful() && currentUser != null) {
                    String token = task.getResult();
                    FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(currentUser.getUid())
                            .update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d("SplashActivity", " Token actualizado en Splash"))
                            .addOnFailureListener(e -> Log.e("SplashActivity", " Error al guardar token en Splash", e));
                } else {
                    Log.e("SplashActivity", " Token fallido o usuario null al intentar guardar token");
                }
            });

        } else {
            Log.e("SplashActivity", "No hay usuario logueado");
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        }
    }

    public static boolean hayInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void sincronizarProgresoOffline(DbHelper dbHelper, String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firestore.collection("usuarios").document(userId);

        List<Curso> cursos = dbHelper.obtenerCursosDescargados();
        Map<String, Object> progresoPorCurso = new HashMap<>();
        Map<String, Object> tiempoPorClase = new HashMap<>();

        for (Curso curso : cursos) {
            List<ClaseFirebase> clases = dbHelper.obtenerClasesPorCurso(curso.getId());
            List<String> clasesCompletadas = new ArrayList<>();

            for (ClaseFirebase clase : clases) {
                Map<String, Object> progreso = dbHelper.obtenerProgresoOffline(clase.getTitulo());

                long tiempoVisto = (long) progreso.getOrDefault("tiempoVisto", 0L);
                boolean cuestionarioOk = (boolean) progreso.getOrDefault("cuestionarioCompletado", false);

                String tituloClase = clase.getTitulo().trim().toLowerCase().replaceAll("[^a-z0-9]", "_");

                if (tiempoVisto >= 90 || cuestionarioOk) {
                    clasesCompletadas.add(tituloClase);

                    tiempoPorClase.put("clase_" + tituloClase, new HashMap<String, Object>() {{
                        put("idCurso", curso.getIdCurso());
                        put("tiempo", tiempoVisto);
                    }});
                }
            }

            if (!clasesCompletadas.isEmpty()) {
                progresoPorCurso.put("curso_" + curso.getIdCurso(), clasesCompletadas);
            }
        }

        Map<String, Object> updates = new HashMap<>();
        if (!progresoPorCurso.isEmpty()) updates.put("progresoCursoOffline", progresoPorCurso);
        if (!tiempoPorClase.isEmpty()) updates.put("tiempoVisualizadoOffline", tiempoPorClase);

        if (!updates.isEmpty()) {
            userRef.set(updates, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("SYNC_OFFLINE", " Sincronización completada"))
                    .addOnFailureListener(e -> Log.e("SYNC_OFFLINE", "Error al sincronizar progreso offline", e));
        }
    }

}