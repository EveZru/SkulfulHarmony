package com.example.skulfulharmony.javaobjects.users;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
    private final Handler handler;
    private final ExecutorService executor;
    private Runnable updateRunnable;

    private long tiempoAcumuladoHoy = 0;
    private long lastActivityTime = 0;
    private String fechaHoy;

    public tiempoUsuario(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
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
                long currentTime = System.currentTimeMillis();
                long elapsedSeconds = (currentTime - lastActivityTime) / 1000;
                lastActivityTime = currentTime;

                Log.d("TiempoUsuario", "⏳ elapsedSeconds: " + elapsedSeconds);

                tiempoAcumuladoHoy += elapsedSeconds;

                Log.d("TiempoUsuario", "🔥 tiempoAcumuladoHoy: " + tiempoAcumuladoHoy + " segundos");

                subirTiempoAFirebase();

                handler.postDelayed(this, 60000);
            }
        };

        handler.post(updateRunnable);
    }

    public void detenerYGuardar() {
        handler.removeCallbacks(updateRunnable);
        subirTiempoAFirebase();
    }

    public void forzarGuardarAhora() {
        subirTiempoAFirebase();
    }

    private void subirTiempoAFirebase() {
        executor.execute(() -> {
            Log.d("TiempoUsuario", "🚀 Ejecutando subirTiempoAFirebase()");

            DocumentReference userDoc = db.collection("usuarios").document(userId);

            long minutosHoy = tiempoAcumuladoHoy / 60;

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
}