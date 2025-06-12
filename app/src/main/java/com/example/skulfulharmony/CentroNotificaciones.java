package com.example.skulfulharmony;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class CentroNotificaciones extends AppCompatActivity {

    private Switch switch_horaentrada, switch_megustacomentario, switch_comentariodenunciatuclase, switch_progresodescarga, switch_subidamaterial, switch_errorsubidadatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_notificaciones);

        switch_horaentrada = findViewById(R.id.switch_horaentrada);
        switch_megustacomentario = findViewById(R.id.switch_megustacomentario);
        switch_comentariodenunciatuclase = findViewById(R.id.switch_comentariodenunciatuclase);
        switch_progresodescarga = findViewById(R.id.switch_progresodescarga);
        switch_subidamaterial = findViewById(R.id.switch_subidamaterial);
        switch_errorsubidadatos = findViewById(R.id.switch_errorsubidadatos);

        Button btnConfig = findViewById(R.id.btn_config_notificaciones);
        //btnConfig.setOnClickListener(v -> abrirConfiguracionNotificaciones());

        // Aquí podrías cargar o guardar los valores de cada switch con SharedPreferences o Firestore si quieres
    }

//    private void abrirConfiguracionNotificaciones() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//            try {
//                startActivity(intent);
//            } catch (Exception e) {
//                Toast.makeText(this, "No se pudo abrir configuración", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Tu Android no soporta esta función", Toast.LENGTH_SHORT).show();
//        }
//    }
}
