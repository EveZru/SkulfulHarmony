package com.example.skulfulharmony.javaobjects.users;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class tiempoUsuario {

    private final FirebaseFirestore db;
    private final String userId;
    private final Handler handler;
    private Runnable updateRunnable;

    private long tiempoAcumuladoHoy = 0;
    private long lastActivityTime = 0;
    private String fechaHoy;

    public tiempoUsuario(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.handler = new Handler();
        this.fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Inicia el conteo continuo de tiempo
    public void iniciarConteo() {
        lastActivityTime = System.currentTimeMillis();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedMinutes = (currentTime - lastActivityTime) / 1000 / 60;
                lastActivityTime = currentTime;

                String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                if (!fechaActual.equals(fechaHoy)) {
                    subirTiempoAFirebase();
                    tiempoAcumuladoHoy = 0;
                    fechaHoy = fechaActual;
                }

                tiempoAcumuladoHoy += elapsedMinutes;

                handler.postDelayed(this, 60000); // cada minuto
            }
        };

        handler.post(updateRunnable);
    }

    // Llamar este método para guardar el tiempo antes de salir de la app
    public void detenerYGuardar() {
        handler.removeCallbacks(updateRunnable);
        subirTiempoAFirebase();
    }

    // Guarda el tiempo acumulado en Firestore usando la fecha como clave y actualiza el total
    private void subirTiempoAFirebase() {
        DocumentReference userDoc = db.collection("usuarios").document(userId);

        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            long totalAnterior = 0;
            if (documentSnapshot.exists()) {
                Long totalGuardado = documentSnapshot.getLong("tiempoTotal");
                totalAnterior = (totalGuardado != null) ? totalGuardado : 0;
            }

            long nuevoTotal = totalAnterior + tiempoAcumuladoHoy;

            Map<String, Object> data = new HashMap<>();
            data.put("tiempo_" + fechaHoy, tiempoAcumuladoHoy);
            data.put("tiempoTotal", nuevoTotal);

            userDoc.set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TiempoUsuario", "Tiempo diario y total guardado correctamente"))
                    .addOnFailureListener(e -> Log.e("TiempoUsuario", "Error al guardar tiempo: " + e.getMessage()));
        }).addOnFailureListener(e -> Log.e("TiempoUsuario", "Error al obtener documento para guardar tiempo total: " + e.getMessage()));
    }
}