package com.example.skulfulharmony;

import android.content.Context;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ContadorWorker extends Worker {

    private static final String TAG = "ContadorWorker";

    // El contexto y parámetros son pasados al worker
    public ContadorWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Este es el código que se ejecutará en segundo plano
        Log.d(TAG, "Trabajando en segundo plano...");

        // Obtener el tiempo acumulado (esto lo puedes sacar de algún lugar,
        // por ejemplo de una SharedPreference o de Firestore, según tus necesidades)
        long tiempoAcumuladoHoy = 60 * 60; // Valor de ejemplo de 1 hora (60 minutos)

        // Guardar el tiempo en Firestore
        subirTiempoAFirebase(tiempoAcumuladoHoy);

        // Retornar el resultado del trabajo
        return Result.success();  // Si el trabajo fue exitoso
    }

    private void subirTiempoAFirebase(long tiempoAcumuladoHoy) {
        // Instanciamos Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Asumimos que tienes un `userId` para identificar al usuario
        String userId = "USER_ID"; // Debes recuperar el ID del usuario actual en tu app

        // Calculamos los minutos de tiempo acumulado (si es necesario)
        long minutosHoy = tiempoAcumuladoHoy / 60;  // Convertimos a minutos

        // Creando el mapa con los datos para actualizar en Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("tiempoHoy", minutosHoy);  // Guardamos el tiempo de hoy
        data.put("tiempoTotal", minutosHoy);  // Guardamos el tiempo total

        // Referencia al documento del usuario en Firestore
        DocumentReference userDoc = db.collection("usuarios").document(userId);

        // Subimos el tiempo a Firestore
        userDoc.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Tiempo guardado exitosamente en Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "❌ Error al guardar tiempo: " + e.getMessage()));
    }
}
