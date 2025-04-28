package com.example.skulfulharmony.javaobjects.users;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.skulfulharmony.Descanso;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tiempoUsuario {

    private final FirebaseFirestore db;
    private final String userId;
    private final Context context;
    private final Handler handler;
    private final ExecutorService executor;
    private Runnable updateRunnable;

    private long tiempoAcumuladoHoy = 0; // 🔥 Acumulamos tiempo en segundos
    private long lastActivityTime = 0;
    private String fechaHoy;

    private boolean enDescanso = false; // 🔥 Si estamos en descanso o no

    public tiempoUsuario(String userId, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.executor = Executors.newSingleThreadExecutor();
        this.fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public void iniciarConteo() {
        Log.d("TiempoUsuario", "🚀 iniciarConteo() llamado");
        lastActivityTime = System.currentTimeMillis();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Verificamos si estamos en descanso
                if (enDescanso) {
                    // Si estamos en descanso, no sumamos el tiempo
                    Log.d("TiempoUsuario", "🛑 En descanso, no acumulando tiempo");
                    handler.postDelayed(this, 60000); // Checamos cada minuto
                    return;
                }

                long currentTime = System.currentTimeMillis();
                long elapsedSeconds = (currentTime - lastActivityTime) / 1000;
                lastActivityTime = currentTime;

                Log.d("TiempoUsuario", "⏳ elapsedSeconds: " + elapsedSeconds);

                tiempoAcumuladoHoy += elapsedSeconds; // Acumulamos en segundos

                Log.d("TiempoUsuario", "🔥 tiempoAcumuladoHoy: " + tiempoAcumuladoHoy + " segundos");

                // Mandamos al descanso automáticamente después de 1 minuto
                if (tiempoAcumuladoHoy >= 90 * 60) {
                    lanzarPantallaDescanso(); // Mandamos a descanso
                    tiempoAcumuladoHoy = 0; // Reiniciamos el contador de tiempo
                }

                subirTiempoAFirebase(); // Subimos el tiempo a Firebase

                handler.postDelayed(this, 60000); // Cada minuto
            }
        };

        handler.post(updateRunnable);
    }

    public void detenerYGuardar() {
        handler.removeCallbacks(updateRunnable); // Detenemos el contador
        subirTiempoAFirebase();
    }

    public void forzarGuardarAhora() {
        subirTiempoAFirebase();
    }

    // Método para iniciar descanso
    public void iniciarDescanso() {
        enDescanso = true; // Activamos el estado de descanso
        Log.d("TiempoUsuario", "🔥 Iniciando descanso...");
    }

    // Método para terminar descanso
    public void terminarDescanso() {
        enDescanso = false; // Desactivamos el descanso
        Log.d("TiempoUsuario", "🔥 Terminando descanso...");
    }

    private void subirTiempoAFirebase() {
        executor.execute(() -> {
            Log.d("TiempoUsuario", "🚀 Ejecutando subirTiempoAFirebase()");

            DocumentReference userDoc = db.collection("usuarios").document(userId);

            long minutosHoy = tiempoAcumuladoHoy / 60; // Convertimos a minutos

            Log.d("TiempoUsuario", "🔥 Guardando minutosHoy = " + minutosHoy);

            Map<String, Object> data = new HashMap<>();
            data.put("tiempo_" + fechaHoy, minutosHoy);
            data.put("tiempoTotal", minutosHoy);

            userDoc.set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TiempoUsuario", "✅ Guardado exitoso en Firestore"))
                    .addOnFailureListener(e -> Log.e("TiempoUsuario", "❌ Error al guardar: " + e.getMessage()));
        });
    }

    public static String formatearMinutos(long minutos) {
        long horas = minutos / 60;
        long mins = minutos % 60;
        if (horas > 0) {
            return horas + "h " + mins + "m";
        } else {
            return mins + "m";
        }
    }

    private void lanzarPantallaDescanso() {
        Intent intent = new Intent(context, Descanso.class);  // Se crea la Intent para ir a DescansoActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Esto es necesario cuando no estás en un Activity ya
        context.startActivity(intent);  // Inicia la actividad de descanso
        Log.d("TiempoUsuario", "🔥 Mandando al descanso...");
    }

}