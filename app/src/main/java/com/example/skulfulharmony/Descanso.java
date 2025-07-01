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

public class  Descanso extends AppCompatActivity {

    private TextView tv_mensaje;
    private TextView tvCuentaRegresiva;
    private Button btnSaltar;
    private String mensajes[] = {"¡Vas Muy Bien!", "¡No Te Rindas!", "¡Un Descanso Es Bueno!"};

    private Handler handler = new Handler();
    private long tiempoRestante = 5 * 60;
    private long tiempoSaltar = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_descanso);

        tv_mensaje = findViewById(R.id.tv_mensajebonito);
        tvCuentaRegresiva = findViewById(R.id.tv_cuenta_regresiva);
        btnSaltar = findViewById(R.id.btn_saltardescanso);

        Random random = new Random();
        int ifrase = random.nextInt(mensajes.length);
        tv_mensaje.setText(mensajes[ifrase]);

        btnSaltar.setEnabled(false);
        btnSaltar.setVisibility(View.INVISIBLE);

        handler.postDelayed(cuentaRegresivaRunnable, 1000);


        handler.postDelayed(cuentaRegresivaSaltarRunnable, 1000);


        btnSaltar.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private final Runnable cuentaRegresivaRunnable = new Runnable() {
        @Override
        public void run() {
            if (tiempoRestante > 0) {
                tiempoRestante--;
                long minutos = tiempoRestante / 60;
                long segundos = tiempoRestante % 60;
                tvCuentaRegresiva.setText("Cuenta regresiva: " + minutos + ":" + String.format("%02d", segundos));
                handler.postDelayed(this, 1000);
            } else {

            }
        }
    };

    // Runnable para la cuenta regresiva de 3 segundos para habilitar el botón de "Saltar"
    private final Runnable cuentaRegresivaSaltarRunnable = new Runnable() {
        @Override
        public void run() {
            if (tiempoSaltar > 0) {
                tiempoSaltar--;
                tvCuentaRegresiva.setText("Cuenta regresiva: " + tiempoSaltar + "s");
                handler.postDelayed(this, 1000);
            } else {
                btnSaltar.setEnabled(true);
                btnSaltar.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onBackPressed() {

        if (tiempoRestante > 0) {

        } else {
            super.onBackPressed();
        }
    }
}