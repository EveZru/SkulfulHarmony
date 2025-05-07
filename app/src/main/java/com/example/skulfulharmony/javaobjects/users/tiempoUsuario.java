package com.example.skulfulharmony.javaobjects.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.skulfulharmony.Descanso;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class tiempoUsuario {

    private final FirebaseFirestore db;
    private final String userId;
    private final Context context;
    private final Handler handler;
    private long tiempoAcumuladoHoy = 0; // Acumulamos tiempo en segundos
    private long lastActivityTime = 0;
    private String fechaHoy;

    private boolean enDescanso = false; // Si estamos en descanso o no
    private long tiempoDescanso = 60 * 60; // Valor por defecto (1 hora) en segundos

    private long tiempoRestanteParaDescanso = 0; // Para llevar el control del descanso
    private boolean descansoHechoHoy = false; // Para controlar si ya se hizo el descanso hoy

    public tiempoUsuario(String userId, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        cargarTiempoDeHoy(); // Cargar el tiempo acumulado de hoy
        cargarTiempoDeDescanso(); // Cargar el tiempo de descanso desde Firestore
    }

    public void iniciarConteo() {
        Log.d("TiempoUsuario", "🚀 iniciarConteo() llamado");
        lastActivityTime = System.currentTimeMillis();

        handler.post(new Runnable() {
            @Override
            public void run() {
                // Verificamos si estamos en descanso
                if (enDescanso) {
                    // Si estamos en descanso, no sumamos el tiempo
                    Log.d("TiempoUsuario", "🛑 En descanso, no acumulando tiempo");
                    handler.postDelayed(this, 60000); // Checamos cada minuto
                    return;
                }

                // Obtener fecha actual
                String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Si el día cambió, reseteamos el acumulado del día
                if (!fechaActual.equals(fechaHoy)) {
                    Log.d("TiempoUsuario", "🔥 Nuevo día detectado, reiniciando contador");
                    tiempoAcumuladoHoy = 0; // Reseteamos el tiempo del día
                    fechaHoy = fechaActual; // Actualizamos la fecha
                    descansoHechoHoy = false; // Reseteamos el estado de descanso
                }

                long currentTime = System.currentTimeMillis();
                long elapsedSeconds = (currentTime - lastActivityTime) / 1000;
                lastActivityTime = currentTime;

                Log.d("TiempoUsuario", "⏳ elapsedSeconds: " + elapsedSeconds);

                tiempoAcumuladoHoy += elapsedSeconds; // Acumulamos en segundos

                Log.d("TiempoUsuario", "🔥 tiempoAcumuladoHoy: " + tiempoAcumuladoHoy + " segundos");

                // Mandamos al descanso automáticamente después del tiempo configurado
                if (tiempoAcumuladoHoy >= tiempoDescanso && !descansoHechoHoy) { // Tiempo de descanso configurado y no descansó hoy
                    lanzarPantallaDescanso(); // Mandamos a descanso
                    descansoHechoHoy = true; // Marcar que ya se hizo el descanso
                    tiempoRestanteParaDescanso = tiempoDescanso; // Reset de tiempo restante
                }

                subirTiempoAFirebase(); // Subimos el tiempo a Firebase

                // Guardamos el tiempo acumulado en SharedPreferences
                //guardarTiempoEnSharedPreferences();

                handler.postDelayed(this, 60000); // Cada minuto
            }
        });
    }

    // Método para pausar el conteo si la app está en segundo plano
    public void pausarConteo() {
        handler.removeCallbacksAndMessages(null); // Detenemos el contador
        Log.d("TiempoUsuario", "🚧 Contador pausado");
    }

    // Método para reanudar el conteo cuando la app vuelve al primer plano
    public void reanudarConteo() {
        Log.d("TiempoUsuario", "🚀 Reiniciando el contador");
        iniciarConteo(); // Reanudar el conteo
    }

    public void detenerYGuardar() {
        handler.removeCallbacksAndMessages(null); // Detenemos el contador
        subirTiempoAFirebase();
        //guardarTiempoEnSharedPreferences(); // Guardamos el tiempo cuando se detiene la app
    }

    public void forzarGuardarAhora() {
        subirTiempoAFirebase();
        //guardarTiempoEnSharedPreferences(); // Guardamos el tiempo cuando se fuerza guardar
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

    // **Registrar la hora de entrada en Firestore**
    public void registrarHoraEntrada() {
        Calendar calendar = Calendar.getInstance();
        int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
        int minutosActuales = calendar.get(Calendar.MINUTE);
        int entradaMinutos = horaActual * 60 + minutosActuales;

        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long totalMinutos = documentSnapshot.getLong("totalMinutosAcumuladosEntrada");
                Long vecesEntrada = documentSnapshot.getLong("vecesEntrada");
                String ultimaFecha = documentSnapshot.getString("ultimaFechaEntrada");

                totalMinutos = (totalMinutos != null) ? totalMinutos : 0L;
                vecesEntrada = (vecesEntrada != null) ? vecesEntrada : 0L;

                // Ya se registró hoy, no volvemos a registrar
                if (hoy.equals(ultimaFecha)) {
                    Log.d("TiempoUsuario", "⏱️ Ya se registró la entrada hoy: " + hoy);
                    return;
                }

                totalMinutos += entradaMinutos;
                vecesEntrada++;

                long promedio = totalMinutos / vecesEntrada;
                int horaPromedio = (int) (promedio / 60);
                int minutosPromedio = (int) (promedio % 60);

                Map<String, Object> data = new HashMap<>();
                data.put("horaEntrada", horaPromedio);
                data.put("minutosEntrada", minutosPromedio);
                data.put("totalMinutosAcumuladosEntrada", totalMinutos);
                data.put("vecesEntrada", vecesEntrada);
                data.put("ultimaFechaEntrada", hoy);

                userRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid ->
                                Log.d("TiempoUsuario", "✅ Hora promedio registrada: " + horaPromedio + ":" + minutosPromedio)
                        ).addOnFailureListener(e ->
                                Log.e("TiempoUsuario", "❌ Error al guardar hora promedio", e)
                        );
            }
        }).addOnFailureListener(e -> Log.e("TiempoUsuario", "❌ Error al obtener documento", e));
    }

    private void subirTiempoAFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("usuarios").document(userId);

        long minutosHoy = tiempoAcumuladoHoy / 60; // Convertimos a minutos

        Log.d("TiempoUsuario", "🔥 Guardando minutosHoy = " + minutosHoy);

        Map<String, Object> data = new HashMap<>();
        data.put("tiempo_" + fechaHoy, minutosHoy);  // Guardamos el tiempo de hoy
        data.put("tiempoTotal", minutosHoy);  // Guardamos el tiempo total (si quieres actualizarlo aquí)

        userDoc.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("TiempoUsuario", "✅ Guardado exitoso en Firestore"))
                .addOnFailureListener(e -> Log.e("TiempoUsuario", "❌ Error al guardar: " + e.getMessage()));
    }

    private void cargarTiempoDeHoy() {
        // Primero, intentamos recuperar el tiempo desde SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("TiempoAcumulado", Context.MODE_PRIVATE);
        tiempoAcumuladoHoy = prefs.getLong("tiempoHoy", 0); // Si no se encuentra, asigna 0
    }

    public void cargarTiempoDeDescanso() {
        // Recuperar el tiempo de descanso desde Firestore
        DocumentReference userDoc = FirebaseFirestore.getInstance().collection("usuarios").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long tiempoDescansoFirestore = documentSnapshot.getLong("tiempo_descanso"); // Tiempo de descanso en Firestore
                if (tiempoDescansoFirestore != null) {
                    tiempoDescanso = tiempoDescansoFirestore * 60; // Convertimos a segundos
                    Log.d("TiempoUsuario", "🔥 Tiempo de descanso recuperado: " + tiempoDescanso);
                } else {
                    // Si no hay valor en Firestore, usamos el valor por defecto (60 minutos)
                    tiempoDescanso = 60 * 60; // 1 hora
                    Log.d("TiempoUsuario", "🔥 No se encontró el tiempo de descanso, usando valor por defecto: " + tiempoDescanso);
                }
            } else {
                // Si el documento no existe, usamos el valor por defecto
                tiempoDescanso = 60 * 60; // 1 hora
                Log.d("TiempoUsuario", "🔥 Documento no encontrado, usando valor por defecto: " + tiempoDescanso);
            }
        }).addOnFailureListener(e -> {
            Log.e("TiempoUsuario", "❌ Error al cargar el tiempo de descanso: " + e.getMessage());
            // Si ocurre un error, usamos el valor por defecto
            tiempoDescanso = 60 * 60; // 1 hora
        });
    }

    private void lanzarPantallaDescanso() {
        Intent intent = new Intent(context, Descanso.class);  // Se crea la Intent para ir a DescansoActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Esto es necesario cuando no estás en un Activity ya
        context.startActivity(intent);  // Inicia la actividad de descanso
        Log.d("TiempoUsuario", "🔥 Mandando al descanso...");
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