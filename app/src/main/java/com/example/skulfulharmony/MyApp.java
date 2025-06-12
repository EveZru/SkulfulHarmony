package com.example.skulfulharmony;

import android.app.Application;
import android.util.Log;

import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;

public class MyApp extends Application {

    private static tiempoUsuario contadorTiempo;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApp", "🔥 MyApp.onCreate ejecutado");
        // Ya no inicializamos aquí, ahora lo maneja SplashActivity completamente
    }

    public static void setContadorTiempo(tiempoUsuario tiempo) {
        contadorTiempo = tiempo;
    }

    public static tiempoUsuario getContadorTiempo() {
        return contadorTiempo;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (contadorTiempo != null) {
            contadorTiempo.forzarGuardarAhora(); // Backup cuando se cierra
        }
    }
}