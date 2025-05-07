package com.example.skulfulharmony.javaobjects.users;
//package com.tuapp.skullfulharmony.notificaciones;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TokenService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Nuevo token generado: " + token);

        // Guardar token en Firestore si el usuario está autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .update("fcmToken", token)
                    .addOnSuccessListener(unused -> Log.d("FCM_TOKEN", "Token guardado"))
                    .addOnFailureListener(e -> Log.e("FCM_TOKEN", "Error al guardar token", e));
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Puedes manejar aquí notificaciones si deseas personalizarlas manualmente
        Log.d("FCM_MSG", "Mensaje recibido: " + remoteMessage.getData());
    }
}