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

        Button botonAceptar = findViewById(R.id.botonAceptar);

        botonAceptar.setOnClickListener(v -> {

            Intent intent = new Intent(creditos.this, Perfil.class);
            startActivity(intent);
            finish();
        });
    }
}