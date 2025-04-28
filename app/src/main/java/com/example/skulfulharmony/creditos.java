package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class creditos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos); // Cargar el layout de créditos

        // Inicializar el botón de aceptación
        Button botonAceptar = findViewById(R.id.botonAceptar);

        // Configurar el botón de aceptación
        botonAceptar.setOnClickListener(v -> {
            // Redirigir a la siguiente actividad (por ejemplo, Perfil)
            Intent intent = new Intent(creditos.this, Perfil.class); // Cambia 'Perfil' a la actividad que quieras abrir
            startActivity(intent);
            finish(); // Finaliza la actividad actual para evitar que el usuario regrese a esta pantalla con el botón de atrás
        });
    }
}