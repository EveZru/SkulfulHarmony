package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Configuracion extends AppCompatActivity {
    private Button btnInfoPerfil, btnNotificaciones, btnEliminarCuenta, btnContPersonalizado,
            btnTerminosYCondiciones, btnRecuperarCont, btnTiempoTrabajo, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion); // Asegúrate de usar el layout adecuado

        // Inicializar botones
        btnInfoPerfil = findViewById(R.id.btn_info_perfil);
        btnNotificaciones = findViewById(R.id.btn_notificaciones);
        btnEliminarCuenta = findViewById(R.id.btn_eliminar_cuenta);
        btnContPersonalizado = findViewById(R.id.btn_cont_personalizado);
        btnTerminosYCondiciones = findViewById(R.id.btn_terminosycondiciones);
        btnRecuperarCont = findViewById(R.id.btn_recuperar_cont);
        btnTiempoTrabajo = findViewById(R.id.btn_tiempotrabajo);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);

        // Configuración del botón de Información del perfil
        btnInfoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Información de perfil
                Intent intent = new Intent(Configuracion.this, Perfil.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Notificaciones
        btnNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Notificaciones
                Intent intent = new Intent(Configuracion.this, PreguntasRecomendacion.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Eliminar Cuenta
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Eliminar Cuenta
                Intent intent = new Intent(Configuracion.this, EliminarCuenta.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Contenido Personalizado
        btnContPersonalizado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Contenido Personalizado
                Intent intent = new Intent(Configuracion.this, CrearCurso.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Términos y Condiciones
        btnTerminosYCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Términos y Condiciones
                Intent intent = new Intent(Configuracion.this, Home.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Recuperar Contraseña
        btnRecuperarCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Recuperar Contraseña
                Intent intent = new Intent(Configuracion.this, RecuperarContrasena.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Tiempo de Trabajo
        btnTiempoTrabajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de Tiempo de Trabajo
                Intent intent = new Intent(Configuracion.this, CrearClase.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra sesión (esto dependerá de la implementación de tu sistema de autenticación)
                // Aquí deberías agregar código para cerrar la sesión en Firebase o el sistema que uses
                Intent intent = new Intent(Configuracion.this, IniciarSesion.class); // Redirige a la pantalla de inicio de sesión
                startActivity(intent);
                finish(); // Finaliza esta actividad para que el usuario no pueda regresar a ella
            }
        });
    }
}