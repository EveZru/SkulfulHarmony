package com.example.skulfulharmony;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApp extends Application {

    private static tiempoUsuario contadorTiempo;

    @Override
    public void onCreate() {
        super.onCreate();

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        dbHelper.getWritableDatabase(); // Forzar creación


        Log.d("MyApp", "🔥 MyApp.onCreate ejecutado");
        if (hayInternet(this)) {
            Log.d("MyApp", "🌐 Conexión detectada, sincronizando progreso offline...");
            sincronizarProgresoOffline(this);
        }
    }

    public static void setContadorTiempo(tiempoUsuario tiempo) {
        contadorTiempo = tiempo;
    }

    public static tiempoUsuario getContadorTiempo() {
        return contadorTiempo;
    }
    public static boolean hayInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    public void sincronizarProgresoOffline(Context context) {
        DbHelper db = new DbHelper(context);
        List<Map<String, Object>> progresos = db.obtenerTodosLosProgresosOffline();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("usuarios").document(user.getUid());

        userRef.get().addOnSuccessListener(snapshot -> {
            Map<String, Object> progresoTemp = new HashMap<>();
            Object raw = snapshot.get("progresoCursoOffline");
            if (raw instanceof Map) {
                progresoTemp = (Map<String, Object>) raw;
            }

            for (Map<String, Object> p : progresos) {
                String claveCurso = "curso_" + p.get("idCurso");
                List<Long> clases = progresoTemp.containsKey(claveCurso)
                        ? new ArrayList<>((List<Long>) progresoTemp.get(claveCurso))
                        : new ArrayList<>();

                Long idClase = ((Number) p.get("idClase")).longValue();
                if (!clases.contains(idClase) && (boolean) p.get("cuestionarioCompletado")) {
                    clases.add(idClase);
                }

                progresoTemp.put(claveCurso, clases);
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("progresoCursoOffline", progresoTemp);

            userRef.set(datos, SetOptions.merge())
                    .addOnSuccessListener(a -> Log.d("SYNC", "✔️ Sincronización de progreso completa"))
                    .addOnFailureListener(e -> Log.e("SYNC", "❌ Error sincronizando progreso", e));
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (contadorTiempo != null) {
            contadorTiempo.forzarGuardarAhora(); // Backup cuando se cierra
        }
    }
}