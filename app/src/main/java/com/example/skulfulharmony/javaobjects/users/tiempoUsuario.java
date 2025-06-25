package com.example.skulfulharmony.javaobjects.users;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.skulfulharmony.Descanso;
import com.example.skulfulharmony.javaobjects.notifications.RecordatorioEntradaReceiver;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

        Log.d("TiempoUsuario", "🎬 Constructor llamado para usuario: " + userId);
        cargarTiempoDeHoy();
        cargarTiempoDeDescanso();
    }

    public void iniciarConteo() {
        Log.d("TiempoUsuario", "🚀 iniciarConteo() llamado");
        lastActivityTime = System.currentTimeMillis();
        Log.d("TiempoUsuario", "📡 Empezando conteo para UID: " + userId);

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

    public void registrarHoraEntrada() {
        Log.d("TiempoUsuario", "📥 Iniciando registrarHoraEntrada()");

        DocumentReference userRef = db.collection("usuarios").document(userId);
        userRef.get().addOnSuccessListener(doc -> {
            Object rawEntradas = doc.get("entradasUsuario");
            List<Object> entradasRaw = rawEntradas instanceof List ? (List<Object>) rawEntradas : new ArrayList<>();
            List<Map<String, Object>> entradas = new ArrayList<>();
            int semanaActual = obtenerSemanaActual();
            Timestamp ahora = Timestamp.now();
            boolean actualizo = false;

            for (Object entrada : entradasRaw) {
                if (entrada instanceof Map) {
                    entradas.add((Map<String, Object>) entrada);
                } else {
                    Log.w("TiempoUsuario", "⚠️ Entrada inválida ignorada: " + entrada);
                }
            }

            for (Map<String, Object> semana : entradas) {
                Object idRaw = semana.get("idSemana");
                if (idRaw instanceof Number && semana.get("vecesEntrada") instanceof List) {
                    Long id = ((Number) idRaw).longValue();
                    if (id == semanaActual) {
                        List<Object> vecesRaw = (List<Object>) semana.get("vecesEntrada");
                        vecesRaw.add(ahora);
                        semana.put("vecesEntrada", vecesRaw);
                        actualizo = true;
                        Log.d("TiempoUsuario", "🟢 Entrada añadida a semana existente");
                        break;
                    }
                }
            }

            if (!actualizo) {
                Map<String, Object> nuevaSemana = new HashMap<>();
                nuevaSemana.put("idSemana", semanaActual);
                nuevaSemana.put("vecesEntrada", new ArrayList<>(Arrays.asList(ahora)));
                entradas.add(nuevaSemana);
                Log.d("TiempoUsuario", "🆕 Entrada registrada en nueva semana");
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("entradasUsuario", entradas);
            userRef.set(datos, SetOptions.merge());

            for (Map<String, Object> semana : entradas) {
                Object idRaw = semana.get("idSemana");
                if (idRaw instanceof Number && semana.get("vecesEntrada") instanceof List) {
                    Long id = ((Number) idRaw).longValue();
                    if (id == semanaActual) {
                        List<Timestamp> veces = new ArrayList<>();
                        for (Object obj : (List<?>) semana.get("vecesEntrada")) {
                            if (obj instanceof Timestamp) {
                                veces.add((Timestamp) obj);
                            } else if (obj instanceof Map) {
                                try {
                                    Map<?, ?> map = (Map<?, ?>) obj;
                                    Object seconds = map.get("seconds");
                                    Object nanos = map.get("nanoseconds");
                                    if (seconds instanceof Number && nanos instanceof Number) {
                                        veces.add(new Timestamp(((Number) seconds).longValue(), ((Number) nanos).intValue()));
                                    }
                                } catch (Exception e) {
                                    Log.e("TiempoUsuario", "❌ Error casteando entrada", e);
                                }
                            }
                        }
                        Log.d("TiempoUsuario", "📊 Entradas semana actual: " + veces.size());
                        calcularYProgramarRecordatorio(veces);
                        break;
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("registrarHoraEntrada", "❌ Error al obtener documento del usuario: " + e.getMessage());
        });
    }

    public void registrarFechaUltimaEntrada() {
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("usuarios").document(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("fechaUltimaEntrada", fechaHoy);

        userRef.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("FechaEntrada", "✅ Fecha registrada: " + fechaHoy))
                .addOnFailureListener(e -> Log.e("FechaEntrada", "❌ Error al guardar fecha de entrada", e));
    }


    private void calcularYProgramarRecordatorio(List<Timestamp> entradasSemana) {
        if (entradasSemana == null || entradasSemana.isEmpty()) return;

        long totalMillis = 0;
        int count = 0;

        for (Timestamp ts : entradasSemana) {
            if (ts != null) {
                totalMillis += ts.toDate().getTime();
                count++;
            } else {
                Log.w("Recordatorio", "⚠️ Timestamp null en entradasSemana");
            }
        }

        if (count == 0) {
            Log.w("Recordatorio", "⛔ No hay timestamps válidos para calcular promedio");
            return;
        }

        long promedioMillis = totalMillis / count;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(promedioMillis);
        cal.add(Calendar.MINUTE, 20); // ⏰ +20 min

        programarAlarma(cal);
    }

    private void programarAlarma(Calendar cal) {
        Intent intent = new Intent(context, RecordatorioEntradaReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.e("Alarma", "🚫 Sin permiso para alarmas exactas");
                return;
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }

    private int obtenerSemanaActual() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private void subirTiempoAFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("usuarios").document(userId);

        long minutosHoy = tiempoAcumuladoHoy / 60; // Convertimos a minutos
        String campoDia = "tiempo_" + fechaHoy;

        Log.d("TiempoUsuario", "⏫ Subiendo tiempo: " + minutosHoy + " minutos hoy");

        // Primero obtenemos el tiempoTotal actual
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            Long tiempoTotalActual = documentSnapshot.getLong("tiempoTotal");
            if (tiempoTotalActual == null) tiempoTotalActual = 0L;

            long nuevoTotal = tiempoTotalActual + minutosHoy;

            Map<String, Object> data = new HashMap<>();
            data.put(campoDia, minutosHoy);
            data.put("tiempoTotal", nuevoTotal);

            Log.d("TiempoUsuario", "📊 tiempoTotal anterior: " + tiempoTotalActual);
            Log.d("TiempoUsuario", "📈 tiempoTotal nuevo: " + nuevoTotal);

            userDoc.set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TiempoUsuario", "✅ Guardado exitoso en Firestore"))
                    .addOnFailureListener(e -> Log.e("TiempoUsuario", "❌ Error al guardar tiempo: " + e.getMessage()));

        }).addOnFailureListener(e -> {
            Log.e("TiempoUsuario", "❌ No se pudo obtener tiempoTotal actual", e);
        });
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

    public void calcularPromediosEntradaPorSemana() {
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = context.getSharedPreferences("promedios_prefs", Context.MODE_PRIVATE);
        String ultimaFecha = prefs.getString("ultimaFechaPromedio", "");

        if (hoy.equals(ultimaFecha)) {
            Log.d("PromedioEntrada", "⏳ Promedio ya calculado hoy: " + hoy);
            return;
        }

        DocumentReference userRef = db.collection("usuarios").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.d("PromedioEntrada", "⛔ Documento de usuario no existe");
                return;
            }

            Object rawEntradas = documentSnapshot.get("entradasUsuario");
            List<Object> entradasRaw = rawEntradas instanceof List ? (List<Object>) rawEntradas : new ArrayList<>();
            List<Map<String, Object>> entradasUsuario = new ArrayList<>();

            for (Object entrada : entradasRaw) {
                if (entrada instanceof Map) {
                    entradasUsuario.add((Map<String, Object>) entrada);
                } else {
                    Log.w("PromedioEntrada", "⚠️ Entrada ignorada: no es Map");
                }
            }

            if (entradasUsuario.isEmpty()) {
                Log.d("PromedioEntrada", "⛔ Lista entradasUsuario vacía");
                return;
            }

            List<Timestamp> todosLosTiempos = new ArrayList<>();
            for (Map<String, Object> semana : entradasUsuario) {
                Object raw = semana.get("vecesEntrada");
                if (raw instanceof List) {
                    List<?> rawList = (List<?>) raw;
                    for (Object obj : rawList) {
                        if (obj instanceof Timestamp) {
                            todosLosTiempos.add((Timestamp) obj);
                        } else if (obj instanceof Map) {
                            try {
                                Map<?, ?> mapObj = (Map<?, ?>) obj;
                                Object seconds = mapObj.get("seconds");
                                Object nanos = mapObj.get("nanoseconds");
                                if (seconds instanceof Number && nanos instanceof Number) {
                                    Timestamp ts = new Timestamp(((Number) seconds).longValue(), ((Number) nanos).intValue());
                                    todosLosTiempos.add(ts);
                                }
                            } catch (Exception e) {
                                Log.e("PromedioEntrada", "❌ Error casteando timestamp desde mapa", e);
                            }
                        }
                    }
                }
            }

            if (todosLosTiempos.isEmpty()) {
                Log.d("PromedioEntrada", "⛔ No hay tiempos válidos para calcular promedio");
                return;
            }

            long totalMillis = 0;
            for (Timestamp ts : todosLosTiempos) {
                if (ts != null) {
                    totalMillis += ts.toDate().getTime();
                }
            }

            long promedioMillis = totalMillis / todosLosTiempos.size();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(promedioMillis);

            int hora = cal.get(Calendar.HOUR_OF_DAY);
            int minuto = cal.get(Calendar.MINUTE);

            try {
                if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
                    Log.e("PromedioEntrada", "❌ Hora o minuto fuera de rango: " + hora + ":" + minuto);
                    return;
                }

                LocalTime horaNoti = LocalTime.of(hora, minuto).plusMinutes(20);
                String notificacionStr = horaNoti.toString();
                String horaFormateada = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.getTime());

                Map<String, Object> update = new HashMap<>();
                update.put("horaPromedio", hora);
                update.put("tiempoDeNotificacion", notificacionStr);

                userRef.set(update, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Log.d("PromedioEntrada", "✅ horaPromedio: " + hora + ", notificación: " + notificacionStr + " (" + horaFormateada + ")");
                            prefs.edit().putString("ultimaFechaPromedio", hoy).apply();
                        })
                        .addOnFailureListener(e -> Log.e("PromedioEntrada", "❌ Error al guardar promedio", e));
            } catch (Exception e) {
                Log.e("PromedioEntrada", "❌ Error al calcular horaNoti o guardar promedio", e);
            }

        }).addOnFailureListener(e -> Log.e("PromedioEntrada", "❌ Error al obtener documento", e));
    }

}