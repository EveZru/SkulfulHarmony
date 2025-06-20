package com.example.skulfulharmony.javaobjects.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.skulfulharmony.R;

public class NotificacionHelper {

    private static final String CANAL_ID = "notificaciones_skilful";
    private static boolean canalCreado = false;

    private static void crearCanal(Context context) {
        if (canalCreado) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    "Notificaciones de actividad",
                    NotificationManager.IMPORTANCE_LOW
            );
            canal.setDescription("Canal para notificaciones de descarga, subida y errores");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }

        canalCreado = true;
    }

    private static boolean permisosOK(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public static void mostrarSimple(Context context, String titulo, String mensaje) {
        if (!permisosOK(context)) {
            Log.e("NotificacionHelper", "❌ Sin permisos para mostrar notificación");
            return;
        }

        crearCanal(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.logo_confondo_notificaciones_sh)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(context).notify(1001, builder.build());
    }

    public static void mostrarProgreso(Context context, int id, String titulo, String mensaje, int progreso) {
        if (!permisosOK(context)) return;
        crearCanal(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setOnlyAlertOnce(true)
                .setProgress(100, progreso, false)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }

    public static void completarProgreso(Context context, int id, String titulo, String mensajeFinal) {
        if (!permisosOK(context)) return;
        crearCanal(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(titulo)
                .setContentText(mensajeFinal)
                .setProgress(0, 0, false)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }

    public static void mostrarError(Context context, int id, String titulo, String mensajeError) {
        if (!permisosOK(context)) return;
        crearCanal(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(titulo)
                .setContentText(mensajeError)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
