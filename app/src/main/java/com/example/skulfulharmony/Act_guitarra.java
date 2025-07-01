package com.example.skulfulharmony;

/*
La clase usa elementos visuales como tv_acorde (muestra el acorde a tocar) y dos contenedores:
imagenContainer (para mostrar lecciones) y actividadContainer (donde se practica).

En cuanto a la lógica, acordeActual guarda el acorde que el usuario debe tocar. posicionesAcordes
es un mapa que sabe qué botones de cuerda/traste corresponden a cada acorde. botonesPresionados lleva
un registro de los botones que el usuario ha pulsado para el acorde actual. Un objeto Random elige los acordes
al azar del arreglo acordes. mapaBotones guarda todos los botones de la interfaz para poder manejarlos fácilmente.
 La variable acordeAcertado ayuda a saber si el usuario ya completó el acorde.


Al iniciar: La app muestra primero la lección (imagenContainer). Al tocarla, cambia a la pantalla
de práctica (actividadContainer) y genera un nuevo acorde.
Botones de Cuerda/Traste: La aplicación identifica automáticamente todos los botones de las cuerdas
y trastes (lX_tY) y los prepara para que el usuario los presione.
Métodos y su Función
generarNuevoAcorde(): Prepara una nueva práctica. Borra los botones pulsados antes, restablece sus colores
y elige un acorde al azar, mostrándolo en pantalla para que el usuario lo toque.
onBotonTocado(String botonId): Se activa cada vez que el usuario presiona un botón de cuerda/traste.
Verifica si el botón presionado es parte del acorde correcto.
Si es correcto, lo marca de verde. Si todos los botones correctos de un acorde fueron presionados,
felicita al usuario y genera otro acorde después de un momento.
Si es incorrecto, el botón se pone rojo brevemente para indicar el error.
cambiarColorBoton(String botonId, int colorResId): Cambia el color de un botón específico,
útil para mostrar si fue correcto o incorrecto.
restablecerColoresBotones(): Pone todos los botones de cuerda/traste en su color original.
mostrarMensaje(String mensaje): Muestra un mensaje corto en la parte inferior de la pantalla
 para informar al usuario (ej. "¡Acorde Correcto!").
*/
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
import android.widget.Button;
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
        setContentView(R.layout.activity_act_guitarra);
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
        posicionesAcordes.put("Do", Arrays.asList("l5_t3", "l4_t2", "l2_t1"));
        posicionesAcordes.put("Re", Arrays.asList("l3_t2", "l2_t3", "l1_t2"));
        posicionesAcordes.put("Mi", Arrays.asList( "l5_t2", "l4_t2", "l3_t1"));
        posicionesAcordes.put("Fa", Arrays.asList("l6_t1", "l5_t3", "l4_t3", "l3_t2", "l2_t1", "l1_t1","l5_t1","l4_t1","l3_t1"));
        posicionesAcordes.put("Sol", Arrays.asList("l6_t3", "l5_t2", "l1_t3"));
        posicionesAcordes.put("La", Arrays.asList( "l4_t2", "l3_t2", "l2_t2"));
        posicionesAcordes.put("Si", Arrays.asList("l1_t2","l2_t2","l3_t2","l4_t2","l5_t2","l6_t2","l4_t4","l3_t4","l2_t4","l1_t4"));
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
        View layout = getLayoutInflater().inflate(R.layout.holder_boton_extra, null);
        Button boton = layout.findViewById(R.id.btn_ver_mas);
        boton.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    }