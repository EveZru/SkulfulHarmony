package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class verterminos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verterminos);

        // Inicializar el botón de aceptación
        Button botonAceptar = findViewById(R.id.botonAceptar);

        // Configurar el botón de aceptación
        botonAceptar.setOnClickListener(v -> {
            // Redirigir a la siguiente actividad (por ejemplo, Home)
            Intent intent = new Intent(verterminos.this, Perfil.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });
    }
}