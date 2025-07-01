package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class EscribirPartiturasDo extends AppCompatActivity {

    private ImageView ivNota;
    private TextView tvNota;

    private float posInicialY;
    private float initialY;
    private String[] notas = {  "Re2", "Mi2","Fa2", "Sol2", "La2", "Si2", "Do3", "Re3", "Mi3", "Fa3", "Sol3", };
    private String notaActual;
    private View[] lineas = new View[6];
    private View[] espacios = new View[5];
    private float limiteSuperior, limiteInferior;
    private Map<String, Float> coordenadasEsperadas = new HashMap<>();
    private TextView tvInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_partituras_do);

        ivNota = findViewById(R.id.iv_notaclase);
        tvNota = findViewById(R.id.tv_notaporbuscar);
        tvInfo = findViewById(R.id.tvinfo);


        // Inicializar las líneas
        lineas[0] = findViewById(R.id.line0); // Do
        lineas[1] = findViewById(R.id.line1); // Mi
        lineas[2] = findViewById(R.id.line2); // Sol
        lineas[3] = findViewById(R.id.line3); // Si
        lineas[4] = findViewById(R.id.line4); // Re4
        lineas[5] = findViewById(R.id.line5); // Fa4

        // Inicializar los espacios
        espacios[0] = findViewById(R.id.space0); // Re
        espacios[1] = findViewById(R.id.space1); // Fa
        espacios[2] = findViewById(R.id.space2); // La
        espacios[3] = findViewById(R.id.space3); // Do4
        espacios[4] = findViewById(R.id.space4); // Mi4

        ivNota.post(new Runnable() {
            @Override
            public void run() {
                initialY = ivNota.getY();
                limiteSuperior = 0;
                limiteInferior = ((View) ivNota.getParent()).getHeight() - ivNota.getHeight();

                coordenadasEsperadas.put("La2", lineas[0].getY() +151);
                coordenadasEsperadas.put("Si2", espacios[0].getY()+151);
                coordenadasEsperadas.put("Do3", lineas[1].getY()+151);
                coordenadasEsperadas.put("Re3", espacios[1].getY()+151);
                coordenadasEsperadas.put("Mi3", lineas[2].getY()+151);
                coordenadasEsperadas.put("Fa3", espacios[2].getY() +151);
                coordenadasEsperadas.put("Sol3", lineas[3].getY()+151);
                coordenadasEsperadas.put("La3", espacios[3].getY()+151);
                coordenadasEsperadas.put("Si3", lineas[4].getY() +151);
                coordenadasEsperadas.put("Do4", espacios[4].getY() +151);
                coordenadasEsperadas.put("Re4", lineas[5].getY()+151);

                cambiarNota();
            }
        });

        ivNota.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        posInicialY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float nuevaPosY = event.getRawY() + posInicialY;
                        if (nuevaPosY >= limiteSuperior && nuevaPosY <= limiteInferior) {
                            v.setY(nuevaPosY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        compararCoordenada();
                        break;
                }
                return true;
            }
        });
    }

    private void cambiarNota() {
        int indiceAleatorio = (int) (Math.random() * notas.length);
        notaActual = notas[indiceAleatorio];
        tvNota.setText(notaActual);
    }


    private void compararCoordenada() {
        float notaYCentroImagen = ivNota.getY() + ivNota.getHeight() / 2;
        Float coordenadaEsperada = coordenadasEsperadas.get(notaActual);
        float tolerancia = 40;

        float toleranciaLineas = 17f;
        float toleranciaEspacios = 40f;

        if (notaActual.equals("Do") || notaActual.equals("Mi") || notaActual.equals("Sol") || notaActual.equals("Si") || notaActual.equals("Re4") || notaActual.equals("Fa4")) {
            tolerancia = toleranciaLineas;
        } else if (notaActual.equals("Re") || notaActual.equals("Fa") || notaActual.equals("La") || notaActual.equals("Do4") || notaActual.equals("Mi4")) {
            tolerancia = toleranciaEspacios;
        }

        String textoCoordenadas = "Buscando: " + notaActual + "\n";
        if (coordenadaEsperada != null) {
            textoCoordenadas += "Coordenada Esperada (Y): " + String.format("%.2f", coordenadaEsperada) + "\n";
            textoCoordenadas += "Coordenada Imagen (Y): " + String.format("%.2f", notaYCentroImagen) + "\n";
            textoCoordenadas += "Tolerancia (Y): " + String.format("%.2f", tolerancia) + "\n";

            if (notaYCentroImagen >= coordenadaEsperada - tolerancia && notaYCentroImagen <= coordenadaEsperada + tolerancia) {
               mostrarMensaje("Correcto");
                ivNota.animate().y(initialY).setDuration(200).start();
                cambiarNota();
            }
        } else {
            textoCoordenadas += "Coordenada esperada para " + notaActual + " no encontrada.";
        }
        tvInfo.setText("Buscando: " + notaActual);
        tvInfo.setText(textoCoordenadas);

    }
    private void mostrarMensaje(String mensaje) {
        View layout = getLayoutInflater().inflate(R.layout.holder_boton_extra, null);
        Button boton = layout.findViewById(R.id.btn_ver_mas);
        boton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grayverde));
        boton.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}