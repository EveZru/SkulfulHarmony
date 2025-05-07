package com.example.skulfulharmony;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Act_pianoAcordes extends AppCompatActivity {

    // Teclas blancas
    private AppCompatButton btn_do1, btn_re1, btn_mi1, btn_fa1, btn_sol1, btn_la1, btn_si1, btn_do2, btn_re2, btn_mi2, btn_fa2, btn_sol2, btn_la2, btn_si2;
    // Teclas negras
    private AppCompatButton btn_doso1, btn_reso1, btn_faso1, btn_solso1, btn_laso1, btn_doso2, btn_reso2, btn_faso2, btn_solso2, btn_laso2;

    private TextView tv_acorde, tv_tipoAcorde;
    private LinearLayout ll_verapuntes, ll_volveralcursop, actividadContainer, imagenContainer;

    private Random random = new Random();

    // Variables para el control del acorde
    private String acordeActual;
    private int teclasPresionadasCorrectas = 0;
    private List<AppCompatButton> teclasPiano; // Lista para guardar todas las teclas
    private List<String> notasAcordeActual;   // Lista para las notas del acorde actual

    // Map para definir los acordes con sus notas y tipo
    private final Map<String, AcordeInfo> acordesMap = new HashMap<>();

    // Clase interna para almacenar información del acorde
    private static class AcordeInfo {

        String nombre;
        String tipo;
        List<String> notas;

        public AcordeInfo(String nombre, String tipo, List<String> notas) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.notas = notas;
        }
    }

    // Map para almacenar los colores originales de las teclas
    private final Map<AppCompatButton, Integer> coloresOriginales = new HashMap<>();
    private final Map<String, Integer> drawableMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_piano_acordes);


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
        agregarTecla(btn_do1);
        agregarTecla(btn_doso1);
        agregarTecla(btn_re1);
        agregarTecla(btn_reso1);
        agregarTecla(btn_mi1);
        agregarTecla(btn_fa1);
        agregarTecla(btn_faso1);
        agregarTecla(btn_sol1);
        agregarTecla(btn_solso1);
        agregarTecla(btn_la1);
        agregarTecla(btn_laso1);
        agregarTecla(btn_si1);
        agregarTecla(btn_do2);
        agregarTecla(btn_doso2);
        agregarTecla(btn_re2);
        agregarTecla(btn_reso2);
        agregarTecla(btn_mi2);
        agregarTecla(btn_fa2);
        agregarTecla(btn_faso2);
        agregarTecla(btn_sol2);
        agregarTecla(btn_solso2);
        agregarTecla(btn_la2);
        agregarTecla(btn_laso2);
        agregarTecla(btn_si2);

        // Inicializar el mapa de drawables
        drawableMap.put("btn_do1", R.drawable.third_rounder_button);
        drawableMap.put("btn_re1", R.drawable.third_rounder_button);
        drawableMap.put("btn_mi1", R.drawable.third_rounder_button);
        drawableMap.put("btn_fa1", R.drawable.third_rounder_button);
        drawableMap.put("btn_sol1", R.drawable.third_rounder_button);
        drawableMap.put("btn_la1", R.drawable.third_rounder_button);
        drawableMap.put("btn_si1", R.drawable.third_rounder_button);
        drawableMap.put("btn_do2", R.drawable.third_rounder_button);
        drawableMap.put("btn_re2", R.drawable.third_rounder_button);
        drawableMap.put("btn_mi2", R.drawable.third_rounder_button);
        drawableMap.put("btn_fa2", R.drawable.third_rounder_button);
        drawableMap.put("btn_sol2", R.drawable.third_rounder_button);
        drawableMap.put("btn_la2", R.drawable.third_rounder_button);
        drawableMap.put("btn_si2", R.drawable.third_rounder_button);

        drawableMap.put("btn_doso1", R.drawable.five_rounder_button);
        drawableMap.put("btn_reso1", R.drawable.five_rounder_button);
        drawableMap.put("btn_faso1", R.drawable.five_rounder_button);
        drawableMap.put("btn_solso1", R.drawable.five_rounder_button);
        drawableMap.put("btn_laso1", R.drawable.five_rounder_button);
        drawableMap.put("btn_doso2", R.drawable.five_rounder_button);
        drawableMap.put("btn_reso2", R.drawable.five_rounder_button);
        drawableMap.put("btn_faso2", R.drawable.five_rounder_button);
        drawableMap.put("btn_solso2", R.drawable.five_rounder_button);
        drawableMap.put("btn_laso2", R.drawable.five_rounder_button);

        // Guardar los colores originales de las teclas y establecer OnClickListener
        for (AppCompatButton tecla : teclasPiano) {
            if (tecla != null) { // Check if the button is not null
                coloresOriginales.put(tecla, drawableMap.get(getResources().getResourceEntryName(tecla.getId())));
                tecla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatButton botonPresionado = (AppCompatButton) v;
                        verificarTeclaPresionada(botonPresionado);
                    }
                });
            }
        }

        tv_acorde = findViewById(R.id.tv_acorde);
        tv_tipoAcorde = findViewById(R.id.tv_tipoacorde);
        ll_verapuntes = findViewById(R.id.ll_verapuntes);
        ll_volveralcursop = findViewById(R.id.ll_volveralcursop);

        imagenContainer = findViewById(R.id.imagencontainer);
        actividadContainer = findViewById(R.id.actividadcontainer);

        imagenContainer.setVisibility(View.VISIBLE);
        actividadContainer.setVisibility(View.GONE);
        imagenContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagenContainer.setVisibility(View.GONE);
                actividadContainer.setVisibility(View.VISIBLE);
            }
        });

        ll_verapuntes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagenContainer.setVisibility(View.VISIBLE);
                actividadContainer.setVisibility(View.GONE);
            }
        });

        inicializarAcordes();
        AcordeRandom();


        ll_volveralcursop = findViewById(R.id.ll_volveralcursop);
        ll_volveralcursop.setOnClickListener(v -> {
            finish();
        });

    }

    private void inicializarAcordes() {
        acordesMap.put("Do", new AcordeInfo("Do", "Mayor", List.of("Do", "Mi", "Sol")));
        acordesMap.put("Do#", new AcordeInfo("Do#", "Mayor", List.of("Do#", "Fa", "Sol#")));
        acordesMap.put("Re", new AcordeInfo("Re", "Mayor", List.of("Re", "Fa#", "La")));
        acordesMap.put("Re#", new AcordeInfo("Re#", "Mayor", List.of("Re#", "Sol", "La#")));
        acordesMap.put("Mi", new AcordeInfo("Mi", "Mayor", List.of("Mi", "Sol#", "Si")));
        acordesMap.put("Fa", new AcordeInfo("Fa", "Mayor", List.of("Fa", "La", "Do")));
        acordesMap.put("Fa#", new AcordeInfo("Fa#", "Mayor", List.of("Fa#", "La#", "Do#")));
        acordesMap.put("Sol", new AcordeInfo("Sol", "Mayor", List.of("Sol", "Si", "Re")));
        acordesMap.put("Sol#", new AcordeInfo("Sol#", "Mayor", List.of("Sol#", "Do", "Re#")));
        acordesMap.put("La", new AcordeInfo("La", "Mayor", List.of("La", "Do#", "Mi")));
        acordesMap.put("La#", new AcordeInfo("La#", "Mayor", List.of("La#", "Re", "Fa")));
        acordesMap.put("Si", new AcordeInfo("Si", "Mayor", List.of("Si", "Re#", "Fa#")));

        // Ejemplo de acorde menor
        acordesMap.put("Do Menor", new AcordeInfo("Do Menor", "Menor", List.of("Do", "Mib", "Sol"))); // Corrección: "Do Menor"
    }

    private void AcordeRandom() {
        List<String> nombresAcordes = new ArrayList<>(acordesMap.keySet());
        int indiceAleatorio = random.nextInt(nombresAcordes.size());
        acordeActual = nombresAcordes.get(indiceAleatorio);
        AcordeInfo acordeInfo = acordesMap.get(acordeActual);

        tv_acorde.setText("Toca el acorde: " + acordeInfo.nombre);
        tv_tipoAcorde.setText("Tipo: " + acordeInfo.tipo); // Corrección: Muestra el tipo aquí
        teclasPresionadasCorrectas = 0;

        notasAcordeActual = acordeInfo.notas;

        // Restablecer el color de todas las teclas del piano usando el Map
        for (AppCompatButton tecla : teclasPiano) {
            if (tecla != null) {
                tecla.setBackgroundResource(coloresOriginales.get(tecla));
            }
        }
    }

    private void verificarTeclaPresionada(AppCompatButton botonPresionado) {
        String nombreTecla = "";

        String idBoton = getResources().getResourceEntryName(botonPresionado.getId());
        if (idBoton.equals("btn_do1")) {
            nombreTecla = "Do";
        } else if (idBoton.equals("btn_re1")) {
            nombreTecla = "Re";
        } else if (idBoton.equals("btn_mi1")) {
            nombreTecla = "Mi";
        } else if (idBoton.equals("btn_fa1")) {
            nombreTecla = "Fa";
        } else if (idBoton.equals("btn_sol1")) {
            nombreTecla = "Sol";
        } else if (idBoton.equals("btn_la1")) {
            nombreTecla = "La";
        } else if (idBoton.equals("btn_si1")) {
            nombreTecla = "Si";
        } else if (idBoton.equals("btn_do2")) {
            nombreTecla = "Do";
        } else if (idBoton.equals("btn_re2")) {
            nombreTecla = "Re";
        } else if (idBoton.equals("btn_mi2")) {
            nombreTecla = "Mi";
        } else if (idBoton.equals("btn_fa2")) {
            nombreTecla = "Fa";
        } else if (idBoton.equals("btn_sol2")) {
            nombreTecla = "Sol";
        } else if (idBoton.equals("btn_la2")) {
            nombreTecla = "La";
        } else if (idBoton.equals("btn_si2")) {
            nombreTecla = "Si";
        }
        else if (idBoton.equals("btn_doso1")) {
            nombreTecla = "Do#";
        } else if (idBoton.equals("btn_reso1")) {
            nombreTecla = "Re#";
        } else if (idBoton.equals("btn_faso1")) {
            nombreTecla = "Fa#";
        } else if (idBoton.equals("btn_solso1")) {
            nombreTecla = "Sol#";
        } else if (idBoton.equals("btn_laso1")) {
            nombreTecla = "La#";
        } else if (idBoton.equals("btn_doso2")) {
            nombreTecla = "Do#";
        } else if (idBoton.equals("btn_reso2")) {
            nombreTecla = "Re#";
        } else if (idBoton.equals("btn_faso2")) {
            nombreTecla = "Fa#";
        } else if (idBoton.equals("btn_solso2")) {
            nombreTecla = "Sol#";
        } else if (idBoton.equals("btn_laso2")) {
            nombreTecla = "La#";
        }

        // Mostrar un Toast con el nombre de la tecla presionada
        Toast.makeText(this, "Presionaste: " + nombreTecla, Toast.LENGTH_SHORT).show();

        // Verificar si la tecla presionada es parte del acorde actual
        boolean teclaCorrecta = notasAcordeActual.contains(nombreTecla);
        if (teclaCorrecta) {
            // La tecla es correcta, cambiar su color
            botonPresionado.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
            teclasPresionadasCorrectas++;

            // Verificar si se han presionado todas las teclas correctas
            if (teclasPresionadasCorrectas == notasAcordeActual.size()) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AcordeRandom();
                    }
                }, 1000);
                Toast.makeText(this, "¡Acorde correcto!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // La tecla es incorrecta, cambiar su color a rojo y mostrar mensaje
            botonPresionado.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
            Toast.makeText(this, "¡Inténtalo otra vez!", Toast.LENGTH_SHORT).show();

            // Restablecer el color de la tecla después de un breve retraso
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    botonPresionado.setBackgroundResource(coloresOriginales.get(botonPresionado)); // Restablece al drawable original
                }
            }, 500);
        }
    }

      private int getButtonBackgroundColor(AppCompatButton button) {
        String resourceName = getResources().getResourceEntryName(button.getId());
        if (resourceName.contains("doso") || resourceName.contains("reso") || resourceName.contains("faso")
                || resourceName.contains("solso") || resourceName.contains("laso")) {
            return R.drawable.five_rounder_button;
        } else {
            return R.drawable.third_rounder_button;
        }
    }

    private void agregarTecla(AppCompatButton tecla) {
        if (tecla != null) {
            teclasPiano.add(tecla);
        }
    }
}

