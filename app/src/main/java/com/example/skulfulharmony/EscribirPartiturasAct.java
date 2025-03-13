package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Rect;

// setContentView(R.layout.activity_escribirpartituras_act);


import androidx.core.content.ContextCompat;

public class EscribirPartiturasAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribirpartituras_act); // Tu XML

        // Inicializa los componentes
        final ImageView noteImageView = findViewById(R.id.iv_notaclase); // El ImageView para la nota
        final View line1 = findViewById(R.id.line1);  // Línea 1 del pentagrama
        final View line2 = findViewById(R.id.line2);  // Línea 2 del pentagrama
        final View line3 = findViewById(R.id.line3);  // Línea 3 del pentagrama
        final View line4 = findViewById(R.id.line4);  // Línea 4 del pentagrama
        final View line5 = findViewById(R.id.line5);  // Línea 5 del pentagrama

        // Set the touch listener to move the noteImageView
        noteImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Variables locales para las coordenadas
                float initialX = 0;
                float initialY = 0;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Guardar la posición inicial cuando se toca la nota
                        initialX = v.getX() - event.getRawX();
                        initialY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Actualizar la posición del ImageView mientras se mueve
                        v.setX(event.getRawX() + initialX);
                        v.setY(event.getRawY() + initialY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Llamada a performClick() para la accesibilidad
                        v.performClick();
                        checkLineCollision(v, line1, line2, line3, line4, line5);  // Verificar colisiones
                        break;
                }
                return true;
            }
        });
    }

    // Método para verificar si la nota está sobre una de las líneas
    private void checkLineCollision(View note, View line1, View line2, View line3, View line4, View line5) {
        if (isColliding(note, line1)) {
            line1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color de línea
        } else {
            line1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black)); // Restaurar color
        }

        if (isColliding(note, line2)) {
            line2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color de línea
        } else {
            line2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black)); // Restaurar color
        }

        if (isColliding(note, line3)) {
            line3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color de línea
        } else {
            line3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black)); // Restaurar color
        }

        if (isColliding(note, line4)) {
            line4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color de línea
        } else {
            line4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black)); // Restaurar color
        }

        if (isColliding(note, line5)) {
            line5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color de línea
        } else {
            line5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black)); // Restaurar color
        }
    }


    private boolean isColliding(View note, View line) {
        Rect noteRect = new Rect();
        note.getHitRect(noteRect);  // Obtiene el área de la nota

        Rect lineRect = new Rect();
        line.getHitRect(lineRect);  // Obtiene el área de la línea

        return Rect.intersects(noteRect, lineRect); // Verifica si las áreas se intersectan
    }
}
