package com.example.skulfulharmony;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class Act_guitarra extends AppCompatActivity {

    private TextView tv_acorde;
    private LinearLayout actividadContainer, imagenContainer;
    private Map<String, List<String>> posicionesAcordes = new HashMap<>();
    private String[] acordes = {"Do", "Re", "Mi", "Fa", "Sol", "La", "Si"};
    private String acordeActual;
    private Set<String> botonesPresionados = new HashSet<>();
    private Random random = new Random();
    private Map<String, AppCompatButton> mapaBotones = new HashMap<>();
    private boolean acordeAcertado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_piano_acordes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_acorde = findViewById(R.id.tv_acorde);
        actividadContainer = findViewById(R.id.actividadcontainer);
        imagenContainer = findViewById(R.id.imagencontainer);
        imagenContainer.setVisibility(View.VISIBLE);
        actividadContainer.setVisibility(View.GONE);

        // Obtener referencias a todos los botones de las cuerdas y trastes
        for (int cuerda = 1; cuerda <= 6; cuerda++) {
            for (int traste = 1; traste <= 5; traste++) {
                String idBoton = "l" + cuerda + "_t" + traste;
                int resId = getResources().getIdentifier(idBoton, "id", getPackageName());
                AppCompatButton boton = findViewById(resId);
                if (boton != null) {
                    mapaBotones.put(idBoton, boton);
                    final String botonId = idBoton;
                    boton.setOnClickListener(v -> onBotonTocado(botonId));
                }
            }
        }

            // Mapeo de acordes a los IDs de los botones que deben estar presionados (basado en la imagen)
        posicionesAcordes.put("Do", Arrays.asList("l5_t3", "l4_t2", "l3_t0", "l2_t1", "l1_t0")); // Asumiendo 't0' es cuerda al aire y 'x' no se presiona
        posicionesAcordes.put("Re", Arrays.asList("l4_t0", "l3_t2", "l2_t3", "l1_t2"));
        posicionesAcordes.put("Mi", Arrays.asList("l6_t0", "l5_t2", "l4_t2", "l3_t1", "l2_t0", "l1_t0"));
        posicionesAcordes.put("Fa", Arrays.asList("l6_t1", "l5_t3", "l4_t3", "l3_t2", "l2_t1", "l1_t1"));
        posicionesAcordes.put("Sol", Arrays.asList("l6_t3", "l5_t2", "l3_t0", "l2_t0", "l1_t3"));
        posicionesAcordes.put("La", Arrays.asList("l5_t0", "l4_t2", "l3_t2", "l2_t2", "l1_t0"));
        posicionesAcordes.put("Si", Arrays.asList("l5_t2", "l4_t4", "l3_t4", "l2_t3", "l1_t2"));
         imagenContainer.setOnClickListener(v -> {
             imagenContainer.setVisibility(View.GONE);
             actividadContainer.setVisibility(View.VISIBLE);
             generarNuevoAcorde();
         });

         LinearLayout ll_verapuntes = findViewById(R.id.ll_verapuntes);
         ll_verapuntes.setOnClickListener(v -> {
             imagenContainer.setVisibility(View.VISIBLE);
             actividadContainer.setVisibility(View.GONE);
         });

         LinearLayout ll_volveralcursop = findViewById(R.id.ll_volveralcursop);
         ll_volveralcursop.setOnClickListener(v -> finish());
    }

    private void generarNuevoAcorde() {
        acordeAcertado = false;
        botonesPresionados.clear();
        restablecerColoresBotones();
        int indiceAleatorio = random.nextInt(acordes.length);
        acordeActual = acordes[indiceAleatorio];
        tv_acorde.setText("Toca el acorde de: " + acordeActual);
    }

    private void onBotonTocado(String botonId) {
        if (!acordeAcertado) {
            if (posicionesAcordes.containsKey(acordeActual) && posicionesAcordes.get(acordeActual).contains(botonId)) {
                // Botón correcto presionado
                if (botonesPresionados.add(botonId)) {
                    cambiarColorBoton(botonId, R.color.verde);
                    if (botonesPresionados.size() == posicionesAcordes.get(acordeActual).size()) {
                        acordeAcertado = true;
                        mostrarMensaje("¡Acorde Correcto!");
                        new Handler(Looper.getMainLooper()).postDelayed(this::generarNuevoAcorde, 1500);
                    }
                }
            } else {
                // Botón incorrecto presionado
                cambiarColorBoton(botonId, R.color.rojo);
                new Handler(Looper.getMainLooper()).postDelayed(() -> cambiarColorBoton(botonId, R.color.gray1), 500);
            }
        }
    }

    private void cambiarColorBoton(String botonId, int colorResId) {
        if (mapaBotones.containsKey(botonId)) {
            mapaBotones.get(botonId).setBackgroundColor(ContextCompat.getColor(this, colorResId));
        }
    }

    private void restablecerColoresBotones() {
        for (AppCompatButton boton : mapaBotones.values()) {
            boton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray1));
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    }