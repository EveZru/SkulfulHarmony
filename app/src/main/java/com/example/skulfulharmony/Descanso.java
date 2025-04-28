package com.example.skulfulharmony;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class Descanso extends AppCompatActivity {

    private Chronometer cronometro;
    private TextView tv_mensaje;
    private Button btnSaltar;
    private String mensajes[] = {"¡Chido crack!", "¡Puto el que se rinda!", "¡5 comentarios positivos!"};

    private Handler handler = new Handler();
    private long tiempoInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_descanso);

        tv_mensaje = findViewById(R.id.tv_mensajebonito);
        cronometro = findViewById(R.id.cronometro_descanso);
        btnSaltar = findViewById(R.id.btn_saltardescanso);

        Random random = new Random();
        int ifrase = random.nextInt(mensajes.length);
        tv_mensaje.setText(mensajes[ifrase]);

        btnSaltar.setEnabled(false); // 🔥 DESACTIVAR botón al inicio

        tiempoInicio = System.currentTimeMillis();
        cronometro.start();

        // 🔥 Cada segundo checamos si ya pasó 1 minuto
        handler.postDelayed(checkTiempoRunnable, 1000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSaltar.setOnClickListener(v -> finish());
    }

    private final Runnable checkTiempoRunnable = new Runnable() {
        @Override
        public void run() {
            long tiempoActual = System.currentTimeMillis();
            long tiempoPasado = (tiempoActual - tiempoInicio) / 1000; // segundos

            if (tiempoPasado >= 60) { // 🔥 1 minuto
                btnSaltar.setEnabled(true); // 🔥 Habilitar botón
            } else {
                handler.postDelayed(this, 1000); // 🔥 Si no, seguir checando cada segundo
            }
        }
    };
}