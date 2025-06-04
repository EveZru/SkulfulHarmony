package com.example.skulfulharmony;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.skulfulharmony.javaobjects.notifications.RecordatorioEntradaReceiver;
import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MyApp extends Application {

    private static tiempoUsuario contadorTiempo; // 🔥 Lo mantenemos global

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicia tiempoUsuario solo si el usuario está logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId != null) {
            contadorTiempo = new tiempoUsuario(userId, getApplicationContext());
            contadorTiempo.iniciarConteo(); // 🔥 Inicia el contador

            // Registrar la hora de entrada del usuario al iniciar la app
            contadorTiempo.registrarHoraEntrada(); // Registrar la hora de entrada
        }
    }

    public static tiempoUsuario getContadorTiempo() {
        return contadorTiempo; // 🔥 Accedes al contador desde cualquier lugar
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (contadorTiempo != null) {
            contadorTiempo.forzarGuardarAhora(); // Guarda al cerrar la app (solo en emulador o Android <14)
        }
    }

    private void programarAlarmaRecordatorio(int minutosEntradaPromedio) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, minutosEntradaPromedio / 60);
        cal.set(Calendar.MINUTE, (minutosEntradaPromedio % 60) + 20); // ⏰ +20 min
        cal.set(Calendar.SECOND, 0);

        // Si la hora ya pasó, programa para el día siguiente
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(getApplicationContext(), RecordatorioEntradaReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 101, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    Log.e("AlarmManager", "🚫 No tiene permiso para programar alarmas exactas");
                    // Aquí podrías lanzar la Intent para pedirle al usuario que habilite manualmente
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        pendingIntent
                );
            }
        }

    }


}