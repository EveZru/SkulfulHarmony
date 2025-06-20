package com.example.skulfulharmony;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CentroNotificaciones extends AppCompatActivity {

    private SwitchCompat switch_horaentrada, switch_megustacomentario, switch_comentariodenunciatuclase;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_notificaciones);

        prefs = getSharedPreferences("notificaciones_prefs", MODE_PRIVATE);

        switch_horaentrada = findViewById(R.id.switch_horaentrada);
        switch_megustacomentario = findViewById(R.id.switch_megustacomentario);
        switch_comentariodenunciatuclase = findViewById(R.id.switch_comentariodenunciatuclase);


        // 🔁 Mapear llaves → campos Firestore
        setupSwitch(switch_horaentrada, "horaentrada", "notificaciones.horaEntrada");
        setupSwitch(switch_megustacomentario, "megustacomentario", "notificaciones.likeComentario");
        setupSwitch(switch_comentariodenunciatuclase, "comentariodenuncia", "notificaciones.denunciaComentario");

        Button btnConfig = findViewById(R.id.btn_config_notificaciones);
        btnConfig.setOnClickListener(v -> abrirConfiguracionNotificaciones());
    }

    private void setupSwitch(SwitchCompat sw, String prefKey, String firestorePath) {
        boolean estadoInicial = prefs.getBoolean(prefKey, true);
        sw.setChecked(estadoInicial);

        // Si nunca se ha guardado este switch en prefs, sincroniza en Firestore también
        if (!prefs.contains(prefKey)) {
            guardarEstado(prefKey, true);
            actualizarFirestore(firestorePath, true);
        }

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            guardarEstado(prefKey, isChecked);
            actualizarFirestore(firestorePath, isChecked);
        });
    }

    private void guardarEstado(String key, boolean estado) {
        prefs.edit().putBoolean(key, estado).apply();
    }

    private void actualizarFirestore(String path, boolean estado) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(user.getUid())
                .update(path, estado)
                .addOnSuccessListener(aVoid -> Log.d("CENTRO_NOTIS", "✅ Firestore actualizado: " + path + " = " + estado))
                .addOnFailureListener(e -> Log.e("CENTRO_NOTIS", "❌ Error al actualizar: " + path, e));
    }

    private void abrirConfiguracionNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir configuración", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tu Android no soporta esta función", Toast.LENGTH_SHORT).show();
        }
    }
}