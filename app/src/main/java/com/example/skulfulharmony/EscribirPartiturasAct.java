package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Rect;
import android.widget.TextView;
import android.widget.EditText;

public class EscribirPartiturasAct extends AppCompatActivity {

    private ImageView ivNota;  // El ImageView para la nota
    private TextView tvNota;   // El TextView donde se muestra la nota aleatoria
    private EditText tvActual; // El EditText donde se muestra la posición actual
    private float posInicialX, posInicialY;  // Posiciones iniciales de la nota
    private String[] notas = {"Do", "Re", "Mi", "Fa", "Sol", "La", "Si"}; // Notas posibles
    private String notaActual;  // Nota que se debe mover

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribirpartituras_act); // Tu XML

        // Inicializar componentes
        ivNota = findViewById(R.id.iv_notaclase); // El ImageView para la nota
        tvNota = findViewById(R.id.tv_notaporbuscar); // El TextView para la nota aleatoria
        tvActual = findViewById(R.id.tvactual); // El EditText donde se muestra la posición

        // Generar una nota aleatoria para mostrar
        mostrarNotaAleatoria();

        // Configurar el Listener para mover la nota solo en el eje Y
        ivNota.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Guardar las posiciones iniciales cuando se toca la nota
                        posInicialX = v.getX() - event.getRawX();
                        posInicialY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Mover la nota solo en el eje Y (no en el eje X)
                        v.setY(event.getRawY() + posInicialY);  // Cambiar solo la posición Y
                        break;
                    case MotionEvent.ACTION_UP:
                        // Verificar si la nota está sobre algún View y mostrar la posición
                        mostrarPosicionNota(v);
                        break;
                }
                return true;
            }
        });
    }

    // Mét para mostrar una nota aleatoria en el TextView
    private void mostrarNotaAleatoria() {
        // Elegir una nota aleatoria del arreglo
        int indiceAleatorio = (int) (Math.random() * notas.length);
        notaActual = notas[indiceAleatorio];
        tvNota.setText(notaActual); // Mostrar la nota en el TextView
    }

    // Mét para mostrar la posición de la nota en el EditText
    private void mostrarPosicionNota(View nota) {
        // Obtener la posición de la nota (en el eje X y Y)
        float notaX = nota.getX();
        float notaY = nota.getY();

        // Actualizar el EditText con la posición de la nota
        tvActual.setText("Posición: X=" + notaX + ", Y=" + notaY);

        // Verificar si la nota está sobre algún View
        if (isViewAtPosition(nota, R.id.line0)) {
            tvActual.append("\nEstá sobre 'Do'");
        } else if (isViewAtPosition(nota, R.id.space0)) {
            tvActual.append("\nEstá sobre 'Re'");
        } else if (isViewAtPosition(nota, R.id.line1)) {
            tvActual.append("\nEstá sobre 'Mi'");
        } else if (isViewAtPosition(nota, R.id.space1)) {
            tvActual.append("\nEstá sobre 'Fa'");
        } else if (isViewAtPosition(nota, R.id.line2)) {
            tvActual.append("\nEstá sobre 'Sol'");
        } else if (isViewAtPosition(nota, R.id.space2)) {
            tvActual.append("\nEstá sobre 'La'");
        } else if (isViewAtPosition(nota, R.id.line3)) {
            tvActual.append("\nEstá sobre 'Si'");
        } else if (isViewAtPosition(nota, R.id.space3)) {
            tvActual.append("\nEstá sobre 'Do4'");
        } else if (isViewAtPosition(nota, R.id.line4)) {
            tvActual.append("\nEstá sobre 'Re4'");
        } else if (isViewAtPosition(nota, R.id.space4)) {
            tvActual.append("\nEstá sobre 'Mi4'");
        } else if (isViewAtPosition(nota, R.id.line5)) {
            tvActual.append("\nEstá sobre 'Fa4'");
        } else {
            tvActual.append("\nNo está sobre ninguna línea/espacio.");
        }
    }

    // Méta verificar si el `View` está en la posición correcta
    private boolean isViewAtPosition(View nota, int idView) {
        // Obtener las coordenadas globales de la nota
        int[] notaLocation = new int[2];
        nota.getLocationOnScreen(notaLocation);  // Obtener la ubicación de la nota en la pantalla

        // Obtener las coordenadas globales de la posición de la línea/espacio
        View positionView = findViewById(idView);
        int[] positionLocation = new int[2];
        positionView.getLocationOnScreen(positionLocation);  // Obtener la ubicación de la línea/espacio en la pantalla

        // Comparar la posición de la nota con la posición de la línea/espacio
        int notaX = notaLocation[0];  // Coordenada X de la nota
        int notaY = notaLocation[1];  // Coordenada Y de la nota
       // notaY=notaY+5;


        int posX = positionLocation[0];  // Coordenada X del View
        int posY = positionLocation[1];  // Coordenada Y del View

        // Definir un margen de tolerancia (puedes ajustarlo según sea necesario)
        int tolerance = 150;

        // Verificar si la posición de la nota está dentro de los límites de la línea o espacio
        return (notaX >= posX - tolerance && notaX <= posX + positionView.getWidth() + tolerance)
                && (notaY >= posY - tolerance && notaY <= posY + positionView.getHeight() + tolerance);
    }



}
