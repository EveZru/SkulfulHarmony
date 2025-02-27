package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Config extends AppCompatActivity {
    private Button btnInfoPerfil, btnNotificaciones, btnEliminarCuenta, btnContPersonalizado,
            btnTerminosYCondiciones, btnRecuperarCont, btnTiempoTrabajo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Inicializar botones
        btnInfoPerfil = findViewById(R.id.btn_info_perfil);
        btnNotificaciones = findViewById(R.id.btn_notificaciones);
        btnEliminarCuenta = findViewById(R.id.btn_eliminar_cuenta);
        btnContPersonalizado = findViewById(R.id.btn_cont_personalizado);
        btnTerminosYCondiciones = findViewById(R.id.btn_terminosycondiciones);
        btnRecuperarCont = findViewById(R.id.btn_recuperar_cont);
        btnTiempoTrabajo = findViewById(R.id.btn_tiempotrabajo);


        btnInfoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para Información de perfil
            }
        });

        btnNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para Notificaciones
            }
        });

        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para Eliminar Cuenta
            }
        });

        btnContPersonalizado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para Contenido Personalizado
            }
        });

        btnTerminosYCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para Términos y Condiciones
            }
        });

        btnRecuperarCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Recuperar Contraseña
                Intent intent = new Intent(Config.this, RecuperarContr.class);
                startActivity(intent);
            }
        });

        btnTiempoTrabajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para Tiempo de Trabajo
            }
        });

    }
}