package com.example.skulfulharmony.javaobjects.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordatorioEntradaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RecordatorioReceiver", "📢 Alarma recibida, revisando entrada desde usuario local...");

//       // Usuario usuario = UsuarioManager.obtenerUsuarioLocal(context);
//        if (usuario == null) {
//            Log.e("RecordatorioReceiver", "❌ No se pudo cargar el usuario local.");
//            return;
//        }

        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

//        List<Date> entradas = usuario.getHorasEntrada();
        boolean entroHoy = false;

//        for (Date entrada : entradas) {
//            String fechaEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(entrada);
//            if (fechaEntrada.equals(hoy)) {
//                entroHoy = true;
//                break;
//            }
//        }

        if (!entroHoy) {
            Log.d("RecordatorioReceiver", "📭 No ha entrado hoy. Enviando notificación...");
            NotificacionHelper.mostrarNotificacion(context,
                    "🎵 ¡Hora de practicar!",
                    "Parece que no has entrado hoy. ¡Vamos a tocar algo!");
        } else {
            Log.d("RecordatorioReceiver", "✅ Ya ha entrado hoy.");
        }
    }
}