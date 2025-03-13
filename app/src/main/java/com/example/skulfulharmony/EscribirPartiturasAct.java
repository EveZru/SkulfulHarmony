package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Rect;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class EscribirPartiturasAct extends AppCompatActivity {
    private TextView tv_nota;
    private String[]notas={"Do","Re","Mi","Sol","La","Si"};
    private ImageView noteImageView = findViewById(R.id.iv_notaclase);
    private String inicialpx,inicialpy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribirpartituras_act);
        tv_nota=findViewById(R.id.tv_notaporbuscar);
        // Inicializa los componentes
         // El ImageView para la nota

        final View lin0 = findViewById(R.id.line0);
        final View line1 = findViewById(R.id.line1);
        final View line2 = findViewById(R.id.line2);
        final View line3 = findViewById(R.id.line3);
        final View line4 = findViewById(R.id.line4);
        final View line5 = findViewById(R.id.line5);

        /*noteImageView.post(()->{
            inicialpx = noteImageView.getX();
            inicialpy = noteImageView.getY();
        });*/

        

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

    private void checkLineCollision(View note ,View line1, View line2, View line3, View line4, View line5) {
        if (isColliding(note, line1)) {
            line1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color de línea
            correcto();
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


    public void correcto(){
        Random random=new Random();
        int inota=random.nextInt(notas.length);
        String nuevaNota=notas[inota];
        tv_nota.setText(nuevaNota);

       // noteImageView.setX(inicialpx);
        //noteImageView.setY(inicialpy);
    }
}
