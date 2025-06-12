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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Aquí es donde lo vas a poner cuacua 🦆👇
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(context, "Activa las notificaciones en configuración", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Log.e("NotificacionHelper", "❌ Permiso POST_NOTIFICATIONS no concedido.");
            return;
        }

        try {
            NotificationManagerCompat.from(context).notify(1001, builder.build());
        } catch (SecurityException e) {
            Log.e("NotificacionHelper", "❌ Error al mostrar notificación: permiso denegado", e);
        }
    }

}
