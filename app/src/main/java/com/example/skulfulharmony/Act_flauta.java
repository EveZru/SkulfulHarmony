
/*Explicacion
La clase Act_flauta. java se ejecuta al entrar a ella por medio de home
la clase cuenta con los elementos de interaccion de usuario  botones  btn_oatras, btn_ouno,
 btn_odos, btn_otres, btn_ocuatro, btn_ocinco, btn_oseis, btn_oocho,

En cuanto a la lógica de la actividad , notaActual es una cadena de texto que almacena la nota musical
activa, sirviendo como la referencia al objetivo del usuario.El usaurio debera presionar los botones .
 correspondientes a agujerosCorrectos, una List<Integer> Las acciones del usuario
se registran en agujerosPresionados, un Set<Integer>, que permite un seguimiento eficiente de los
agujeros ya cubiertos por el usuario sin duplicados. Para la selección aleatoria de notas, se
utiliza un objeto Random, llamado random. Aunque declarado, el objeto mediaPlayer no se utiliza
explícitamente en el código proporcionado para la reproducción de sonidos de notas; sin embargo, se incluye una
gestión de recursos para él en el ciclo de vida de la actividad.

las  variables posicionesNotas, un Map<String, List<Integer>>
que asocia cada nota musical con su correspondiente lista de agujeros a cubrir, funcionando como
un diccionario de digitaciones. El arreglo de cadenas notas ("Do", "Re", "Mi", etc.) es el conjunto
 de todas las notas posibles que la aplicación puede proponer para la práctica.
El elementos ll_verapuntes hace visible el cntenido de imagenContainer(el cual cumple con mostrar la leccion de
la clase)
El elemento ll_volveralcurso lleva a la aactividad anterior (home)
Metodos y su funcionalidad
generar nueva nota
  crea en el tv_acorne una nota aleatoria por un proceso aleatorio se llama al cerrar la actividad, presionar todos
   los elementos correctos
   onAgujeroTocado(int indiceAgujero): Este método es el núcleo de la interacción del usuario. Se encarga de procesar
cada vez que el usuario presiona uno de los botones de agujero (btn_oatras, btn_ouno, etc.). Su función principal es:

-Verificar si el agujero presionado (indiceAgujero) forma parte de los agujerosCorrectos para la notaActual.

-Si es correcto, registra el agujero en agujerosPresionados y cambia el color del botón a verde para indicar acierto.
Si el usuario ha presionado todos los agujeros correctos para la nota actual, deshabilita temporalmente los botones,
muestra un mensaje de "¡Correcto!", y después de un breve retraso, vuelve a habilitar los botones y llama
 a generarNuevaNota() para la siguiente ronda.

-Si es incorrecto, muestra un mensaje de "¡Error!", y el botón presionado
 incorrectamente cambia de color a rojo brevemente para dar una retroalimentación visual al usuario.

cambiarColorAgujero(int indiceAgujero, int colorResId): Este método auxiliar es responsable de actualizar
visualmente el color de fondo de un botón de agujero específico. Recibe el índice del agujero y el recurso
 de color deseado, aplicándolo al botón correspondiente para indicar si fue presionado correctamente (verde)
o para restablecer su color.

restablecerColoresAgujeros(): Como su nombre indica, este método restaura el color de fondo original
 (definido por R.drawable.five_rounder_button) de todos los botones de los agujeros de la flauta. Se
utiliza principalmente al inicio de una nueva nota o cuando se necesita limpiar la interfaz de cualquier
indicación de error o acierto previo.

mostrarMensaje(String mensaje): Un método simple pero efectivo para proporcionar retroalimentación textual al
usuario. Utiliza un Toast para mostrar mensajes breves en la pantalla, como "¡Correcto!" o "¡Error! Intenta de
nuevo.", informando al usuario sobre el resultado de sus acciones.

onStop(): Este es un método del ciclo de vida de Android que se llama cuando la actividad ya no
                         es visible para el usuario. Su función aquí es crucial para la gestión de recursos:
 asegura que el objeto mediaPlayer (si existe y no es nulo) sea liberado (mediaPlayer.release()). Esto previene
 fugas de memoria y libera recursos del sistema, lo cual es una buena práctica de programación en Android.

deshabilitarBotones(): Este método desactiva la interactividad de todos los botones de los agujeros, impidiendo
que el usuario pueda presionarlos. Se usa, por ejemplo, después de que el usuario ha completado correctamente una
nota, para evitar entradas adicionales mientras se prepara la siguiente nota.

habilitarBotones(): Complementario a deshabilitarBotones(), este método vuelve a activar la interactividad de todos
 los botones de los agujeros, permitiendo al usuario continuar con la práctica de digitación. Se llama después de
 que la interfaz se ha preparado para la siguiente nota o ronda.

 */

package com.example.skulfulharmony;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Act_flauta extends AppCompatActivity {

    private AppCompatButton btn_oatras, btn_ouno, btn_odos, btn_otres, btn_ocuatro, btn_ocinco, btn_oseis, btn_oocho;
    private TextView tv_acorde;
    private LinearLayout ll_verapuntes, ll_volveralcursop, actividadContainer, imagenContainer;
    private ImageView perfil_logo, atras_img;
    private FrameLayout fl_flauta;

    private String notaActual;
    private List<Integer> agujerosCorrectos;
    private Set<Integer> agujerosPresionados = new HashSet<>();
    private Random random = new Random();
    private MediaPlayer mediaPlayer;


    private Map<String, List<Integer>> posicionesNotas = new HashMap<>();
    private String[] notas = {"Do ", "Re", "Mi", "Fa", "Sol", "La", "Si"}; // Cambiamos acordes a notas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_flauta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_oatras = findViewById(R.id.btn_oatras);
        btn_ouno = findViewById(R.id.btn_ouno);
        btn_odos = findViewById(R.id.btn_odos);
        btn_otres = findViewById(R.id.btn_otres);
        btn_ocuatro = findViewById(R.id.btn_ocuatro);
        btn_ocinco = findViewById(R.id.btn_ocinco);
        btn_oseis = findViewById(R.id.btn_oseis);
        btn_oocho = findViewById(R.id.btn_oocho);
        tv_acorde = findViewById(R.id.tv_acorde);
        ll_verapuntes = findViewById(R.id.ll_verapuntes);
        ll_volveralcursop = findViewById(R.id.ll_volveralcursop);
        imagenContainer = findViewById(R.id.imagencontainer);
        actividadContainer = findViewById(R.id.actividadcontainer);
        fl_flauta = findViewById(R.id.fl_flauta);

        imagenContainer.setVisibility(View.VISIBLE);
        actividadContainer.setVisibility(View.GONE);

        imagenContainer.setOnClickListener(v -> {
            imagenContainer.setVisibility(View.GONE);
            actividadContainer.setVisibility(View.VISIBLE);
            generarNuevaNota();
        });

        ll_verapuntes.setOnClickListener(v -> {
            imagenContainer.setVisibility(View.VISIBLE);
            actividadContainer.setVisibility(View.GONE);
        });

        ll_volveralcursop.setOnClickListener(v -> {
            finish();
        });

        btn_oatras.setOnClickListener(v -> onAgujeroTocado(0));


        posicionesNotas.put("Do ", Arrays.asList(0, 1, 2, 3, 4, 5, 6,7));
        posicionesNotas.put("Re", Arrays.asList(0,1, 2, 3, 4, 5, 6));
        posicionesNotas.put("Mi", Arrays.asList(0, 1, 2, 3, 4,5));
        posicionesNotas.put("Fa", Arrays.asList(0,1, 2, 3, 4));
        posicionesNotas.put("Sol", Arrays.asList(0,1,2,3));
        posicionesNotas.put("La", Arrays.asList(0,1, 2));
        posicionesNotas.put("Si", Arrays.asList(0,1));


        btn_ouno.setOnClickListener(v -> onAgujeroTocado(1));
        btn_odos.setOnClickListener(v -> onAgujeroTocado(2));
        btn_otres.setOnClickListener(v -> onAgujeroTocado(3));
        btn_ocuatro.setOnClickListener(v -> onAgujeroTocado(4));
        btn_ocinco.setOnClickListener(v -> onAgujeroTocado(5));
        btn_oseis.setOnClickListener(v -> onAgujeroTocado(6));
        btn_oocho.setOnClickListener(v -> onAgujeroTocado(7));
    }

    private void generarNuevaNota() {
        agujerosPresionados.clear();
        restablecerColoresAgujeros();

        int indiceAleatorio = random.nextInt(notas.length);
        notaActual = notas[indiceAleatorio]; // Cambiamos acordeActual a notaActual
        tv_acorde.setText(notaActual);
        agujerosCorrectos = posicionesNotas.get(notaActual); // Cambiamos posicionesAcordes a posicionesNotas
        if (agujerosCorrectos == null) {
            agujerosCorrectos = new ArrayList<>();
        }
    }

    private void onAgujeroTocado(int indiceAgujero) {
        boolean esCorrecto = false;
        if (agujerosCorrectos.contains(indiceAgujero)) {
            esCorrecto = true;
        }

        AppCompatButton buttonPresionado = null;
        switch (indiceAgujero) {
            case 0: buttonPresionado = btn_oatras; break;
            case 1: buttonPresionado = btn_ouno; break;
            case 2: buttonPresionado = btn_odos; break;
            case 3: buttonPresionado = btn_otres; break;
            case 4: buttonPresionado = btn_ocuatro; break;
            case 5: buttonPresionado = btn_ocinco; break;
            case 6: buttonPresionado = btn_oseis; break;
            case 7: buttonPresionado = btn_oocho; break;
        }

        if (esCorrecto) {
            if (agujerosPresionados.add(indiceAgujero)) {
                cambiarColorAgujero(indiceAgujero, R.color.verde);
                if (agujerosPresionados.size() == agujerosCorrectos.size()) {
                    deshabilitarBotones();
                    mostrarMensaje("¡Correcto!");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        habilitarBotones(); // Volver a habilitar después de generar nueva nota
                        generarNuevaNota();
                    }, 1500);
                }

            }
        } else {
            // Agujero incorrecto presionado
            mostrarMensaje("¡Error! Intenta de nuevo.");
            if (buttonPresionado != null) {
                buttonPresionado.setBackgroundResource(R.drawable.error_button);
                AppCompatButton finalButtonPresionado = buttonPresionado;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (finalButtonPresionado != null) {
                        finalButtonPresionado.setBackgroundResource(R.drawable.five_rounder_button);
                    }
                }, 500);
            }
        }
    }

    private void cambiarColorAgujero(int indiceAgujero, int colorResId) {
        AppCompatButton button = null;
        switch (indiceAgujero) {
            case 0: button = btn_oatras; break;
            case 1: button = btn_ouno; break;
            case 2: button = btn_odos; break;
            case 3: button = btn_otres; break;
            case 4: button = btn_ocuatro; break;
            case 5: button = btn_ocinco; break;
            case 6: button = btn_oseis; break;
            case 7: button = btn_oocho; break;
        }
        if (button != null) {
            button.setBackgroundColor(ContextCompat.getColor(this, colorResId));
        }
    }

    private void restablecerColoresAgujeros() {
        btn_oatras.setBackgroundResource(R.drawable.five_rounder_button);
        btn_ouno.setBackgroundResource(R.drawable.five_rounder_button);
        btn_odos.setBackgroundResource(R.drawable.five_rounder_button);
        btn_otres.setBackgroundResource(R.drawable.five_rounder_button);
        btn_ocuatro.setBackgroundResource(R.drawable.five_rounder_button);
        btn_ocinco.setBackgroundResource(R.drawable.five_rounder_button);
        btn_oseis.setBackgroundResource(R.drawable.five_rounder_button);
        btn_oocho.setBackgroundResource(R.drawable.five_rounder_button);
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void deshabilitarBotones() {
        btn_oatras.setEnabled(false);
        btn_ouno.setEnabled(false);
        btn_odos.setEnabled(false);
        btn_otres.setEnabled(false);
        btn_ocuatro.setEnabled(false);
        btn_ocinco.setEnabled(false);
        btn_oseis.setEnabled(false);
        btn_oocho.setEnabled(false);
    }

    private void habilitarBotones() {
        btn_oatras.setEnabled(true);
        btn_ouno.setEnabled(true);
        btn_odos.setEnabled(true);
        btn_otres.setEnabled(true);
        btn_ocuatro.setEnabled(true);
        btn_ocinco.setEnabled(true);
        btn_oseis.setEnabled(true);
        btn_oocho.setEnabled(true);
    }
}