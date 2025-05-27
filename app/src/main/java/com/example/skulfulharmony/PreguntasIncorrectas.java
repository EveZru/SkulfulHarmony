package com.example.skulfulharmony;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity; // androidx.activity.EdgeToEdge, Insets, ViewCompat, WindowInsetsCompat no son estrictamente necesarios aquí a menos que los uses para EdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasEnCuestionariosparaContestar;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken; // Importación correcta para TypeToken (no com.google.common.reflect.TypeToken)

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreguntasIncorrectas extends AppCompatActivity {
    private RecyclerView rvPreguntasIncorrectas;
    private TextView tvPreguntasIncorrectas; // Este será el TextView para el mensaje de "no hay preguntas"
    private AdapterPreguntasEnCuestionariosparaContestar adapterPreguntas;
    private Button btnReintentar, btnComprobar;
    private static final String TAG = "PreguntasIncorrectas";

    // Elimina @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_incorrectas);

        // Ahora, estos findViewByIds deben coincidir con los IDs y tipos correctos en el XML
        tvPreguntasIncorrectas = findViewById(R.id.tv_preguntas_incorrectas); // Este es tu TextView de mensaje/título
        rvPreguntasIncorrectas = findViewById(R.id.rv_preguntas_incorrectas); // ¡Ahora sí es un RecyclerView en el XML!
        btnReintentar = findViewById(R.id.btn_reintentar);
        btnComprobar = findViewById(R.id.btn_comprobar);

        rvPreguntasIncorrectas.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("cuestionario", MODE_PRIVATE);
        String json = prefs.getString("preguntas_incorrectas", null);

        List<PreguntaCuestionario> preguntasGuardadas = new ArrayList<>(); // Inicializa para evitar NPE
        if (json != null && !json.trim().isEmpty()) { // Comprueba que el JSON no sea null ni vacío
            Gson gson = new Gson();
            // Utiliza la clase Type de Java.lang.reflect
            Type listType = new TypeToken<List<PreguntaCuestionario>>() {}.getType();
            try {
                preguntasGuardadas = gson.fromJson(json, listType);
                if (preguntasGuardadas == null) { // Si GSON devuelve null para JSON vacío/malformado
                    preguntasGuardadas = new ArrayList<>();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar preguntas incorrectas: " + e.getMessage(), e); // Log el stack trace completo
                preguntasGuardadas = new ArrayList<>();
                Toast.makeText(this, "Error al cargar preguntas incorrectas", Toast.LENGTH_SHORT).show();
            }
        }

        // Lógica para mostrar u ocultar RecyclerView y el TextView
        if (preguntasGuardadas.isEmpty()) {
            tvPreguntasIncorrectas.setText("No hay preguntas incorrectas registradas aún. ¡Sigue practicando!");
            tvPreguntasIncorrectas.setVisibility(View.VISIBLE);
            rvPreguntasIncorrectas.setVisibility(View.GONE);
            if (btnComprobar != null) btnComprobar.setVisibility(View.GONE);
            if (btnReintentar != null) btnReintentar.setVisibility(View.GONE);
        } else {
            // El tvPreguntasIncorrectas podría ser el título, si es así, no lo ocultes.
            // Si solo quieres mostrar el mensaje cuando está vacío, el id del TextView debería ser tv_preguntas_incorrectas_vacio
            // como en la solución anterior, y el tvPreguntasIncorrectas que usas aquí debería ser ese.
            // Para este ejemplo, lo dejaremos como si tvPreguntasIncorrectas es el que actúa como mensaje.
            tvPreguntasIncorrectas.setVisibility(View.GONE); // Oculta el mensaje si hay preguntas
            rvPreguntasIncorrectas.setVisibility(View.VISIBLE);

            adapterPreguntas = new AdapterPreguntasEnCuestionariosparaContestar(this, preguntasGuardadas);
            adapterPreguntas.setMostrarResultados(true); // Para que muestre las respuestas correctas al cargar
            rvPreguntasIncorrectas.setAdapter(adapterPreguntas);

            if (btnComprobar != null && btnReintentar != null) {
                btnComprobar.setVisibility(View.GONE); // Asumo que al cargar las incorrectas ya están "comprobadas"
                btnReintentar.setVisibility(View.VISIBLE); // Ofrece reintentar

                btnComprobar.setOnClickListener(v -> {
                    adapterPreguntas.comprobarRespuestas();
                    Toast.makeText(this, "Respuestas comprobadas.", Toast.LENGTH_SHORT).show();
                    btnComprobar.setVisibility(View.GONE);
                    btnReintentar.setVisibility(View.VISIBLE);
                });

                btnReintentar.setOnClickListener(v -> {
                    Toast.makeText(this, "Reintentando las preguntas incorrectas...", Toast.LENGTH_SHORT).show();
                    adapterPreguntas.setMostrarResultados(false);
                   // adapterPreguntas.resetSelecciones();
                    adapterPreguntas.notifyDataSetChanged();
                    btnComprobar.setVisibility(View.VISIBLE);
                    btnReintentar.setVisibility(View.GONE);
                });
            }
        }
    }
}