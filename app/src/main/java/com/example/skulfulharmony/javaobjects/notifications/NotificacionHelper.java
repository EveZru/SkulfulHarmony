package com.example.skulfulharmony.javaobjects.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.skulfulharmony.R;

public class NotificacionHelper {

    private static final String CANAL_ID = "canal_recordatorio";

    public static void mostrarNotificacion(Context context, String titulo, String mensaje) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID, "Recordatorios de entrada",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS);
            if (permiso != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificacionHelper", "❌ No se puede mostrar: POST_NOTIFICATIONS no concedido");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.logo_confondo_sh)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(1001, builder.build());
            Log.d("NotificacionHelper", "✅ Notificación mostrada con éxito");
        } catch (Exception e) {
            Log.e("NotificacionHelper", "❌ Error inesperado al mostrar notificación", e);
        }
    }

}
