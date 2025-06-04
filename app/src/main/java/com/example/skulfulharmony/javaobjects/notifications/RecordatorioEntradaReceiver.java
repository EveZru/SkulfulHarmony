package com.example.skulfulharmony.javaobjects.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RecordatorioEntradaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RecordatorioReceiver", "📢 Alarma recibida, revisando entrada...");

        NotificacionHelper.mostrarNotificacion(context,
                "🎵 ¡Hora de practicar!",
                "Parece que no has entrado hoy. ¡Vamos a tocar algo!");
    }
}