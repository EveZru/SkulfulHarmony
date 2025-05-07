package com.example.skulfulharmony;

import android.app.Application;
import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.google.firebase.auth.FirebaseAuth;

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
}