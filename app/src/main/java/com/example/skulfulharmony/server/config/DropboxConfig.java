package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.google.firebase.firestore.FirebaseFirestore;

public class DropboxConfig {

    private DbxClientV2 client;
    public DropboxConfig(String accessToken) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("SkillfullHarmonyApp")
                .build();
        // Usamos el Access Token para inicializar el cliente Dropbox
        this.client = new DbxClientV2(config, accessToken);
    }

    // Método para obtener el cliente de Dropbox
    public DbxClientV2 getClient() {
        return this.client;
    }

    public interface TokenCallback {
        void onTokenReceived(String token);
        void onError(Exception e);
    }

    public static void obtenerAccessTokenFirebase(TokenCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("token")
                .document("1") // accedes directamente al documento "1"
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String token = documentSnapshot.getString("token");
                        if (token != null) {
                            callback.onTokenReceived(token);
                        } else {
                            callback.onError(new Exception("Token no encontrado en el documento"));
                        }
                    } else {
                        callback.onError(new Exception("Documento '1' no existe"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

}