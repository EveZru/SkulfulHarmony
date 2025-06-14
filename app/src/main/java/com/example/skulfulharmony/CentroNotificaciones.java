package com.example.skulfulharmony;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class CentroNotificaciones extends AppCompatActivity {

    private SwitchCompat switch_horaentrada, switch_megustacomentario, switch_comentariodenunciatuclase, switch_progresodescarga, switch_subidamaterial, switch_errorsubidadatos;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_notificaciones);

        // Iniciar SharedPreferences
        prefs = getSharedPreferences("notificaciones_prefs", MODE_PRIVATE);

        // Vincular Switches
        switch_horaentrada = findViewById(R.id.switch_horaentrada);
        switch_megustacomentario = findViewById(R.id.switch_megustacomentario);
        switch_comentariodenunciatuclase = findViewById(R.id.switch_comentariodenunciatuclase);
        switch_progresodescarga = findViewById(R.id.switch_progresodescarga);
        switch_subidamaterial = findViewById(R.id.switch_subidamaterial);
        switch_errorsubidadatos = findViewById(R.id.switch_errorsubidadatos);

        // Cargar valores guardados
        cargarEstados();

        // Listeners para guardar cambios
        switch_horaentrada.setOnCheckedChangeListener((buttonView, isChecked) ->
                guardarEstado("horaentrada", isChecked));

        switch_megustacomentario.setOnCheckedChangeListener((buttonView, isChecked) ->
                guardarEstado("megustacomentario", isChecked));

        switch_comentariodenunciatuclase.setOnCheckedChangeListener((buttonView, isChecked) ->
                guardarEstado("comentariodenuncia", isChecked));

        switch_progresodescarga.setOnCheckedChangeListener((buttonView, isChecked) ->
                guardarEstado("progresodescarga", isChecked));

        switch_subidamaterial.setOnCheckedChangeListener((buttonView, isChecked) ->
                guardarEstado("subidamaterial", isChecked));

        switch_errorsubidadatos.setOnCheckedChangeListener((buttonView, isChecked) ->
                guardarEstado("errorsubida", isChecked));

        Button btnConfig = findViewById(R.id.btn_config_notificaciones);
        btnConfig.setOnClickListener(v -> abrirConfiguracionNotificaciones());
    }

    private void cargarEstados() {
        switch_horaentrada.setChecked(prefs.getBoolean("horaentrada", true));
        switch_megustacomentario.setChecked(prefs.getBoolean("megustacomentario", true));
        switch_comentariodenunciatuclase.setChecked(prefs.getBoolean("comentariodenuncia", true));
        switch_progresodescarga.setChecked(prefs.getBoolean("progresodescarga", true));
        switch_subidamaterial.setChecked(prefs.getBoolean("subidamaterial", true));
        switch_errorsubidadatos.setChecked(prefs.getBoolean("errorsubida", true));
    }

    private void guardarEstado(String key, boolean estado) {
        prefs.edit().putBoolean(key, estado).apply();
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
