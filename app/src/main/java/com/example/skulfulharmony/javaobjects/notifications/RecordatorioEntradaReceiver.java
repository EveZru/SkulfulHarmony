package com.example.skulfulharmony.javaobjects.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.skulfulharmony.javaobjects.notifications.NotificacionHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecordatorioEntradaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RecordatorioReceiver", "📢 Alarma recibida, revisando entrada desde Firestore...");

        // Obtener preferencias
        SharedPreferences prefs = context.getSharedPreferences("notificaciones_prefs", Context.MODE_PRIVATE);
        boolean notiEntradaActiva = prefs.getBoolean("horaentrada", true);
        boolean notiMeGustaActiva = prefs.getBoolean("megustacomentario", true);

        // 🔥 NOTIFICACIÓN DE PRUEBA para "me gusta en comentario"
        if (notiMeGustaActiva) {
            NotificacionHelper.mostrarNotificacion(
                    context,
                    "👍 ¡Esto es una preuba manito!",
                    "Wasaaaaaaaaaa!"
            );
        } else {
            Log.d("RecordatorioReceiver", "🔕 Notificación de me gusta en comentario desactivada.");
        }

        // Si no está activa la de hora promedio, cancelamos el resto
        if (!notiEntradaActiva) {
            Log.d("RecordatorioReceiver", "🔕 Notificación de hora de entrada desactivada.");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Log.e("RecordatorioReceiver", "❌ Usuario no autenticado.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Log.e("RecordatorioReceiver", "❌ Documento de usuario no encontrado.");
                return;
            }

            List<Map<String, Object>> entradas = (List<Map<String, Object>>) snapshot.get("entradasUsuario");
            if (entradas == null) {
                Log.d("RecordatorioReceiver", "📭 No hay entradas registradas. Mostrando notificación.");
                mostrarNoti(context);
                return;
            }

            int semanaActual = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            boolean entroHoy = false;

            for (Map<String, Object> semana : entradas) {
                Long id = ((Number) semana.get("idSemana")).longValue();
                if (id == semanaActual) {
                    List<Timestamp> veces = (List<Timestamp>) semana.get("vecesEntrada");
                    for (Timestamp ts : veces) {
                        String fechaEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(ts.toDate());
                        if (fechaEntrada.equals(hoy)) {
                            entroHoy = true;
                            break;
                        }
                    }
                    break;
                }
            }

            if (!entroHoy) {
                Log.d("RecordatorioReceiver", "📭 Usuario NO ha entrado hoy. Enviando notificación...");
                mostrarNoti(context);
            } else {
                Log.d("RecordatorioReceiver", "✅ Usuario YA ha entrado hoy. No se envía notificación.");
            }

        }).addOnFailureListener(e -> {
            Log.e("RecordatorioReceiver", "❌ Error al verificar entrada: " + e.getMessage());
        });
    }

    private void mostrarNoti(Context context) {
        NotificacionHelper.mostrarNotificacion(
                context,
                "🎵 ¡Hora de practicar!",
                "Parece que no has entrado hoy. ¡Vamos a tocar algo!"
        );
    }
}