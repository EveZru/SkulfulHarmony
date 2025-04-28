package com.example.skulfulharmony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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
import java.util.List;
import java.util.Random;

public class Act_pianoAcordes extends AppCompatActivity {
    //teclas blancas
    private AppCompatButton btn_do1, btn_re1, btn_mi1, btn_fa1, btn_sol1, btn_la1, btn_si1, btn_do2, btn_re2, btn_mi2, btn_fa2, btn_sol2, btn_la2, btn_si2;
    //teclas negras
    private AppCompatButton btn_doso1, btn_reso1, btn_faso1, btn_solso1, btn_laso1, btn_doso2, btn_reso2, btn_faso2, btn_solso2, btn_laso2;

    private TextView tv_acorde;
    private LinearLayout ll_verapuntes, ll_volveralcursop , actividadContainer,imagenContainer;

    private ImageView perfil_logo,atras; // Asegúrate de tener el ID correcto si lo vas a usar

    private String[] notas = {"Do", "Re", "Mi", "Fa", "Sol", "La", "Si"};
    private Random random = new Random();


    // Variables para el control del acorde
    private String acordeActual;
    private int teclasPresionadasCorrectas = 0;
    private List<AppCompatButton> teclasPiano; // Lista para guardar todas las teclas
    private List<String> notasAcordeActual;   // Lista para las notas del acorde actual
    private final String[] notasMusicales = {"Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si"};
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

        //  teclas blancas
        btn_do1 = findViewById(R.id.btn_do1);
        btn_re1 = findViewById(R.id.btn_re1);
        btn_mi1 = findViewById(R.id.btn_mi1);
        btn_fa1 = findViewById(R.id.btn_fa1);
        btn_sol1 = findViewById(R.id.btn_sol1);
        btn_la1 = findViewById(R.id.btn_la1);
        btn_si1 = findViewById(R.id.btn_si1);
        btn_do2 = findViewById(R.id.btn_do2);
        btn_re2 = findViewById(R.id.btn_re2);
        btn_mi2 = findViewById(R.id.btn_mi2);
        btn_fa2 = findViewById(R.id.btn_fa2);
        btn_sol2 = findViewById(R.id.btn_sol2);
        btn_la2 = findViewById(R.id.btn_la2);
        btn_si2 = findViewById(R.id.btn_si2);

        //  teclas negras
        btn_doso1 = findViewById(R.id.btn_doso1);
        btn_reso1 = findViewById(R.id.btn_reso1);
        btn_faso1 = findViewById(R.id.btn_faso1);
        btn_solso1 = findViewById(R.id.btn_solso1);
        btn_laso1 = findViewById(R.id.btn_laso1);
        btn_doso2 = findViewById(R.id.btn_doso2);
        btn_reso2 = findViewById(R.id.btn_reso2);
        btn_faso2 = findViewById(R.id.btn_faso2);
        btn_solso2 = findViewById(R.id.btn_solso2);
        btn_laso2 = findViewById(R.id.btn_laso2);

// Inicializar la lista de teclas del piano
        teclasPiano = new ArrayList<>();
        teclasPiano.add(btn_do1);
       // teclasPiano.add(btn_doso1);
        teclasPiano.add(btn_re1);
       // teclasPiano.add(btn_reso1);
        teclasPiano.add(btn_mi1);
        teclasPiano.add(btn_fa1);
      //  teclasPiano.add(btn_faso1);
        teclasPiano.add(btn_sol1);
       // teclasPiano.add(btn_solso1);
        teclasPiano.add(btn_la1);
       // teclasPiano.add(btn_laso1);
        teclasPiano.add(btn_si1);
        teclasPiano.add(btn_do2);
       // teclasPiano.add(btn_doso2);
        teclasPiano.add(btn_re2);
       // teclasPiano.add(btn_reso2);
        teclasPiano.add(btn_mi2);
        teclasPiano.add(btn_fa2);
        //teclasPiano.add(btn_faso2);
        teclasPiano.add(btn_sol2);
        //teclasPiano.add(btn_solso2);
        teclasPiano.add(btn_la2);
       // teclasPiano.add(btn_laso2);
        teclasPiano.add(btn_si2);

        // Establecer OnClickListener para cada tecla del piano
        for (AppCompatButton tecla : teclasPiano) {
            tecla.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatButton botonPresionado = (AppCompatButton) v;
                    verificarTeclaPresionada(botonPresionado);
                }
            });
        }




        tv_acorde = findViewById(R.id.tv_acorde);
        ll_verapuntes = findViewById(R.id.ll_verapuntes);
        ll_volveralcursop = findViewById(R.id.ll_volveralcursop);

        imagenContainer = findViewById(R.id.imagencontainer);
        actividadContainer=findViewById(R.id.actividadcontainer);

        imagenContainer.setVisibility(View.VISIBLE);
        actividadContainer.setVisibility(View.GONE);
        // Establecer el OnClickListener para el contenedor de la imagen
        imagenContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al tocar la imagen, la ocultamos y mostramos la actividad
                imagenContainer.setVisibility(View.GONE);
                actividadContainer.setVisibility(View.VISIBLE);
            }
        });

        // Establecer el OnClickListener para ll_verapuntes
        ll_verapuntes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al tocar "Ver apuntes", mostramos la imagen y ocultamos la actividad
                imagenContainer.setVisibility(View.VISIBLE);
                actividadContainer.setVisibility(View.GONE);
            }
        });

        AcordeRandom();

    }


    private void AcordeRandom() {

        int indiceAleatorio = random.nextInt(notas.length);
        acordeActual = notas[indiceAleatorio];
        tv_acorde.setText(acordeActual);
      //  tv_acorde.setText(acordeActual + " Mayor"); // Indicamos que es acorde mayor
        teclasPresionadasCorrectas = 0; // Reiniciar el contador al cambiar de acorde
        notasAcordeActual = obtenerNotasAcordeMayor(acordeActual);
        // Opcional: Puedes reiniciar el color de las teclas aquí si lo deseas
        for (AppCompatButton tecla : teclasPiano) {
            tecla.setBackgroundResource(R.drawable.third_rounder_button); // Reemplaza con el background original
        }
    }

    // obtener las notas de un acorde mayor
    private List<String> obtenerNotasAcordeMayor(String notaBase) {
        List<String> acorde = new ArrayList<>();
        int indiceNotaBase = -1;

        // Encontrar el índice de la nota base en el array de notas musicales
        for (int i = 0; i < notas.length; i++) {
            if (notas[i].equals(notaBase)) {
                indiceNotaBase = i;
                break;
            }
        }

        if (indiceNotaBase != -1) {
            // Calcular los índices de las otras dos notas del acorde mayor (tónica, tercera mayor, quinta justa)
            acorde.add(notas[indiceNotaBase % notas.length]); // Tónica
            acorde.add(notas[(indiceNotaBase + 2) % notas.length]); // Tercera mayor (esto es simplificado, necesitará ajuste para sostenidos)
            acorde.add(notas[(indiceNotaBase + 4) % notas.length]); // Quinta justa (esto es simplificado, necesitará ajuste para sostenidos)
        }
        return acorde;
    }
    private void verificarTeclaPresionada(AppCompatButton botonPresionado) {
        String nombreTecla = "";

        // Obtener el "nombre" de la tecla presionada. Aquí asumimos que el texto
        // del botón (o alguna forma de identificar la nota) está disponible.
        // Necesitarás adaptar esto según cómo hayas nombrado o identificado tus botones.
        // Por ejemplo, si el ID del botón contiene la nota:
        String idBoton = getResources().getResourceEntryName(botonPresionado.getId());
        if (idBoton.contains("do")) nombreTecla = "Do";
        else if (idBoton.contains("re")) nombreTecla = "Re";
        else if (idBoton.contains("mi")) nombreTecla = "Mi";
        else if (idBoton.contains("fa")) nombreTecla = "Fa";
        else if (idBoton.contains("sol")) nombreTecla = "Sol";
        else if (idBoton.contains("la")) nombreTecla = "La";
        else if (idBoton.contains("si")) nombreTecla = "Si";
        else if (idBoton.contains("doso")) nombreTecla = "Do#";
        else if (idBoton.contains("reso")) nombreTecla = "Re#";
        else if (idBoton.contains("faso")) nombreTecla = "Fa#";
        else if (idBoton.contains("solso")) nombreTecla = "Sol#";
        else if (idBoton.contains("laso")) nombreTecla = "La#";

        // Verificar si la tecla presionada es parte del acorde actual
        if (notasAcordeActual.contains(nombreTecla)) {
            // La tecla es correcta, cambiar su color
            botonPresionado.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
            teclasPresionadasCorrectas++;

            // Verificar si se han presionado todas las teclas correctas (para un acorde mayor son 3)
            if (teclasPresionadasCorrectas == 3) {
                // ¡Acorde completado
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AcordeRandom(); // Cargar un nuevo acorde después de 1 segundo
                    }
                }, 1000);
                Toast.makeText(this, "¡Acorde correcto!", Toast.LENGTH_SHORT).show();
                 // Cargar un nuevo acorde
            }
        }
    }
}