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
    private long tiempoAcumuladoHoy = 0;
    private long lastActivityTime = 0;
    private String fechaHoy;

    private boolean enDescanso = false;
    private long tiempoDescanso = 60 * 60;

    private long tiempoRestanteParaDescanso = 0;
    private boolean descansoHechoHoy = false;

    public tiempoUsuario(String userId, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        cargarTiempoDeHoy();
        cargarTiempoDeDescanso();
    }

    public void iniciarConteo() {
        Log.d("TiempoUsuario", "🚀 iniciarConteo() llamado");
        lastActivityTime = System.currentTimeMillis();

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (enDescanso) {
                    Log.d("TiempoUsuario", "🛑 En descanso, no acumulando tiempo");
                    handler.postDelayed(this, 60000);
                    return;
                }

                String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                if (!fechaActual.equals(fechaHoy)) {
                    Log.d("TiempoUsuario", "🔥 Nuevo día detectado, reiniciando contador");
                    tiempoAcumuladoHoy = 0;
                    fechaHoy = fechaActual;
                    descansoHechoHoy = false;
                }

                long currentTime = System.currentTimeMillis();
                long elapsedSeconds = (currentTime - lastActivityTime) / 1000;
                lastActivityTime = currentTime;

                Log.d("TiempoUsuario", "⏳ elapsedSeconds: " + elapsedSeconds);

                tiempoAcumuladoHoy += elapsedSeconds;

                Log.d("TiempoUsuario", "🔥 tiempoAcumuladoHoy: " + tiempoAcumuladoHoy + " segundos");

                if (tiempoAcumuladoHoy >= tiempoDescanso && !descansoHechoHoy) {
                    lanzarPantallaDescanso();
                    descansoHechoHoy = true;
                    tiempoRestanteParaDescanso = tiempoDescanso;
                }

                subirTiempoAFirebase();

                handler.postDelayed(this, 60000);
            }
        });
    }

    public void pausarConteo() {
        handler.removeCallbacksAndMessages(null);
        Log.d("TiempoUsuario", "🚧 Contador pausado");
    }

    public void reanudarConteo() {
        Log.d("TiempoUsuario", "🚀 Reiniciando el contador");
        iniciarConteo();
    }

    public void detenerYGuardar() {
        handler.removeCallbacksAndMessages(null);
        subirTiempoAFirebase();
    }

    public void forzarGuardarAhora() {
        subirTiempoAFirebase();
    }

    public void iniciarDescanso() {
        enDescanso = true;
        Log.d("TiempoUsuario", "🔥 Iniciando descanso...");
    }

    public void terminarDescanso() {
        enDescanso = false;
        Log.d("TiempoUsuario", "🔥 Terminando descanso...");
    }

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

        long minutosHoy = tiempoAcumuladoHoy / 60;

        Log.d("TiempoUsuario", "🔥 Guardando minutosHoy = " + minutosHoy);

        Map<String, Object> data = new HashMap<>();
        data.put("tiempo_" + fechaHoy, minutosHoy);
        data.put("tiempoTotal", minutosHoy);

        userDoc.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("TiempoUsuario", "✅ Guardado exitoso en Firestore"))
                .addOnFailureListener(e -> Log.e("TiempoUsuario", "❌ Error al guardar: " + e.getMessage()));
    }

    private void cargarTiempoDeHoy() {
        SharedPreferences prefs = context.getSharedPreferences("TiempoAcumulado", Context.MODE_PRIVATE);
        tiempoAcumuladoHoy = prefs.getLong("tiempoHoy", 0);
    }

    public void cargarTiempoDeDescanso() {
        DocumentReference userDoc = FirebaseFirestore.getInstance().collection("usuarios").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long tiempoDescansoFirestore = documentSnapshot.getLong("tiempo_descanso");
                if (tiempoDescansoFirestore != null) {
                    tiempoDescanso = tiempoDescansoFirestore * 60;
                    Log.d("TiempoUsuario", "🔥 Tiempo de descanso recuperado: " + tiempoDescanso);
                } else {
                    tiempoDescanso = 60 * 60;
                    Log.d("TiempoUsuario", "🔥 No se encontró el tiempo de descanso, usando valor por defecto: " + tiempoDescanso);
                }
            } else {
                tiempoDescanso = 60 * 60;
                Log.d("TiempoUsuario", "🔥 Documento no encontrado, usando valor por defecto: " + tiempoDescanso);
            }
        }).addOnFailureListener(e -> {
            Log.e("TiempoUsuario", "❌ Error al cargar el tiempo de descanso: " + e.getMessage());
            tiempoDescanso = 60 * 60;
        });
    }

    private void lanzarPantallaDescanso() {
        Intent intent = new Intent(context, Descanso.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    public void calcularPromediosEntradaPorSemana() {
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long totalMinutos = documentSnapshot.getLong("totalMinutosAcumuladosEntrada");
                Long vecesEntrada = documentSnapshot.getLong("vecesEntrada");

                if (totalMinutos != null && vecesEntrada != null && vecesEntrada > 0) {
                    long promedio = totalMinutos / vecesEntrada;
                    int hora = (int) (promedio / 60);
                    int minuto = (int) (promedio % 60);

                    Log.d("PromedioEntrada", "📊 Promedio general de entrada: " + hora + "h " + minuto + "m en " + vecesEntrada + " días");
                } else {
                    Log.d("PromedioEntrada", "⛔ No hay suficientes datos para calcular el promedio.");
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("PromedioEntrada", "❌ Error al obtener promedio de entrada", e);
        });
    }
}