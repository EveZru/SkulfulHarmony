package com.example.skulfulharmony;

import android.app.Application;
import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.google.firebase.auth.FirebaseAuth;

public class MyApp extends Application {

    private static tiempoUsuario contadorTiempo;

    @Override
    public void onCreate() {
        super.onCreate();

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId != null) {
            contadorTiempo = new tiempoUsuario(userId);
            contadorTiempo.iniciarConteo();
        }
    }

    public static tiempoUsuario getContadorTiempo() {
        return contadorTiempo;
    }
}
