package com.example.skulfulharmony;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Descanso extends AppCompatActivity {

    private TextView tv_mensaje;
    private TextView tvCuentaRegresiva; // Para mostrar la cuenta regresiva de 5 minutos
    private Button btnSaltar;
    private String mensajes[] = {"¡Chido crack!", "¡Puto el que se rinda!", "¡5 comentarios positivos!"};

    private Handler handler = new Handler();
    private long tiempoRestante = 5 * 60; // 5 minutos en segundos (300 segundos)
    private long tiempoSaltar = 3; // 3 segundos para habilitar el botón de "Saltar"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_descanso);

        tv_mensaje = findViewById(R.id.tv_mensajebonito);
        tvCuentaRegresiva = findViewById(R.id.tv_cuenta_regresiva); // Para la cuenta regresiva de 5 minutos
        btnSaltar = findViewById(R.id.btn_saltardescanso);

        // Mensajes aleatorios
        Random random = new Random();
        int ifrase = random.nextInt(mensajes.length);
        tv_mensaje.setText(mensajes[ifrase]);

        btnSaltar.setEnabled(false); // Desactivar el botón al principio
        btnSaltar.setVisibility(View.INVISIBLE); // Ocultar el botón al principio

        // Iniciamos la cuenta regresiva de 5 minutos
        handler.postDelayed(cuentaRegresivaRunnable, 1000);

        // Iniciamos la cuenta regresiva para habilitar el botón de "Saltar"
        handler.postDelayed(cuentaRegresivaSaltarRunnable, 1000);

        // Verificamos que el botón se pueda presionar después de la cuenta regresiva
        btnSaltar.setOnClickListener(v -> finish()); // Termina la actividad cuando se presiona el botón

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Runnable para la cuenta regresiva de 5 minutos (300 segundos)
    private final Runnable cuentaRegresivaRunnable = new Runnable() {
        @Override
        public void run() {
            if (tiempoRestante > 0) {
                tiempoRestante--; // Decrementamos el tiempo
                long minutos = tiempoRestante / 60;
                long segundos = tiempoRestante % 60;
                tvCuentaRegresiva.setText("Cuenta regresiva: " + minutos + ":" + String.format("%02d", segundos)); // Actualizamos el texto con minutos y segundos
                handler.postDelayed(this, 1000); // Llamamos de nuevo después de 1 segundo
            } else {
                // Aquí podrías hacer algo al finalizar el tiempo de descanso, si es necesario
            }
        }
    };

    // Runnable para la cuenta regresiva de 3 segundos para habilitar el botón de "Saltar"
    private final Runnable cuentaRegresivaSaltarRunnable = new Runnable() {
        @Override
        public void run() {
            if (tiempoSaltar > 0) {
                tiempoSaltar--; // Decrementamos el tiempo
                tvCuentaRegresiva.setText("Cuenta regresiva: " + tiempoSaltar + "s"); // Actualizamos el texto
                handler.postDelayed(this, 1000); // Llamamos de nuevo después de 1 segundo
            } else {
                btnSaltar.setEnabled(true); // Habilitamos el botón después de 3 segundos
                btnSaltar.setVisibility(View.VISIBLE); // Mostramos el botón
            }
        }
    };

    @Override
    public void onBackPressed() {
        // Evitamos que el usuario regrese antes de que termine la cuenta regresiva
        if (tiempoRestante > 0) {
            // No dejamos que regrese
        } else {
            super.onBackPressed(); // Si el tiempo terminó, se permite la acción normal de "Atrás"
        }
    }
}