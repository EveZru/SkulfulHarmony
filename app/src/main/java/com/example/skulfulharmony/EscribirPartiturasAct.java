package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Rect;
import android.widget.TextView;
import android.widget.EditText;

public class EscribirPartiturasAct extends AppCompatActivity {

    private ImageView ivNota;  // El ImageView para la nota
    private TextView tvNota;   // El TextView donde se muestra la nota aleatoria
    private EditText tvActual; // El EditText donde se muestra la posición actual
    private float posInicialX, posInicialY;  // Posiciones iniciales de la nota
    private String[] notas = {"Do", "Re", "Mi", "Fa", "Sol", "La", "Si", "Do4", "Re4", "Mi4", "Fa4"}; // Notas posibles
    private String notaActual, Notapocicionada;  // Variables para la nota actual y la nota en la posición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribirpartituras_act); // Tu XML

        // Inicializar componentes
        ivNota = findViewById(R.id.iv_notaclase); // El ImageView para la nota
        tvNota = findViewById(R.id.tv_notaporbuscar); // El TextView para la nota aleatoria
        tvActual = findViewById(R.id.tvactual); // El EditText donde se muestra la posición

        // Comprobar si las vistas fueron inicializadas correctamente
        if (ivNota == null || tvNota == null || tvActual == null) {
            throw new NullPointerException("Una o más vistas no fueron inicializadas correctamente.");
        }

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
                        verificarNotaYPosicion();
                        break;
                }
                return true;
            }
        });
    }

    //  mostrar una nota aleatoria en el TextView
    private void mostrarNotaAleatoria() {
        // Elegir una nota aleatoria del arreglo
        int indiceAleatorio = (int) (Math.random() * notas.length);
        notaActual = notas[indiceAleatorio];
        tvNota.setText(notaActual); // Mostrar la nota en el TextView
    }

    // mostrar la posición de la nota en el EditText
    private void mostrarPosicionNota(View nota) {
        // Obtener la posición de la nota (en el eje X y Y)
        float notaX = nota.getX();
        float notaY = nota.getY();

        // Actualizar el EditText con la posición de la nota
        tvActual.setText("Posición: X=" + notaX + ", Y=" + notaY);

        // Verificar si la vista no es nula antes de hacer la comprobación
        if (nota != null && isViewAtPosition(nota, R.id.line0)) {
            tvActual.append("\nEstá sobre 'Do'");
            Notapocicionada = "Do";
        } else if (nota != null && isViewAtPosition(nota, R.id.space0)) {
            tvActual.append("\nEstá sobre 'Re'");
            Notapocicionada = "Re";
        } else if (nota != null && isViewAtPosition(nota, R.id.line1)) {
            tvActual.append("\nEstá sobre 'Mi'");
            Notapocicionada = "Mi";
        } else if (nota != null && isViewAtPosition(nota, R.id.space1)) {
            tvActual.append("\nEstá sobre 'Fa'");
            Notapocicionada = "Fa";
        } else if (nota != null && isViewAtPosition(nota, R.id.line2)) {
            tvActual.append("\nEstá sobre 'Sol'");
            Notapocicionada = "Sol";
        } else if (nota != null && isViewAtPosition(nota, R.id.space2)) {
            tvActual.append("\nEstá sobre 'La'");
            Notapocicionada = "La";
        } else if (nota != null && isViewAtPosition(nota, R.id.line3)) {
            tvActual.append("\nEstá sobre 'Si'");
            Notapocicionada = "Si";
        } else if (nota != null && isViewAtPosition(nota, R.id.space3)) {
            tvActual.append("\nEstá sobre 'Do4'");
            Notapocicionada = "Do4";
        } else if (nota != null && isViewAtPosition(nota, R.id.line4)) {
            tvActual.append("\nEstá sobre 'Re4'");
            Notapocicionada = "Re4";
        } else if (nota != null && isViewAtPosition(nota, R.id.space4)) {
            tvActual.append("\nEstá sobre 'Mi4'");
            Notapocicionada = "Mi4";
        } else if (nota != null && isViewAtPosition(nota, R.id.line5)) {
            tvActual.append("\nEstá sobre 'Fa4'");
            Notapocicionada = "Fa4";
        } else {
            tvActual.append("\nNo está sobre ninguna línea/espacio.");
        }
    }

    // comparar si la posición de la nota corresponde con la nota mostrada en el TextView
    private void verificarNotaYPosicion() {

            // Obtener la nota actual desde el TextView
            String notaBuscada = tvNota.getText().toString();

            // Comprobar si las variables son nulas antes de llamar a contains
            if (notaBuscada != null && Notapocicionada != null && Notapocicionada.equals(notaBuscada)) {
                // Si la nota está en la posición correcta, hacer lo siguiente
                findViewById(getResources().getIdentifier(Notapocicionada, "id", getPackageName()))
                        .setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Cambiar color a verde

                // Mover la nota a su posición inicial
                ivNota.setX(posInicialX);
                ivNota.setY(posInicialY);

                // Generar una nueva nota aleatoria
                mostrarNotaAleatoria();
            }


    }


    // si un View está en una posición específica
    private boolean isViewAtPosition(View nota, int idView) {
        // Verificar si la vista es nula
        if (nota == null) return false;

        // Obtener las coordenadas globales de la nota
        int[] notaLocation = new int[2];
        nota.getLocationOnScreen(notaLocation);  // Obtener la ubicación de la nota en la pantalla

        // Obtener las coordenadas globales de la posición de la línea/espacio
        View positionView = findViewById(idView);
        if (positionView == null) return false; // Verificar que la vista de la posición no sea nula

        int[] positionLocation = new int[2];
        positionView.getLocationOnScreen(positionLocation);  // Obtener la ubicación de la línea/espacio en la pantalla

        // Comparar la posición de la nota con la posición de la línea/espacio
        int notaX = notaLocation[0];  // Coordenada X de la nota
        int notaY = notaLocation[1];  // Coordenada Y de la nota

        int posX = positionLocation[0];  // Coordenada X del View
        int posY = positionLocation[1];  // Coordenada Y del View

        // Definir un margen de tolerancia
        int tolerance = 150;

        // Verificar si la posición de la nota está dentro de los límites de la línea o espacio
        return (notaX >= posX - tolerance && notaX <= posX + positionView.getWidth() + tolerance)
                && (notaY >= posY - tolerance && notaY <= posY + positionView.getHeight() + tolerance);
    }
}
