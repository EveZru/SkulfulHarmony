package com.example.skulfulharmony;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaRecomendacion;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreguntasRecomendacion extends AppCompatActivity {
    private TextView txt_preguntas;
    private RadioGroup rg_opcionespregunta;
    private Button btn_siguiente_pregunta, btn_anterior_pregunta;
    private int currentPreguntaIndex = 0;
    private List<PreguntaRecomendacion> listaDePreguntas = new ArrayList<>(); // Inicialización con tipo genérico
    private DbUser dbUser = new DbUser(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preguntasrecomendacion);

        txt_preguntas = findViewById(R.id.txt_preguntarecomendacion);
        rg_opcionespregunta = findViewById(R.id.rg_recomendacionrespuesta);
        btn_anterior_pregunta = findViewById(R.id.btn_preguntaanterior);
        btn_siguiente_pregunta = findViewById(R.id.btn_preguntasigueinte);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        {
            // Crear las preguntas
            ArrayList<String> respuestas1 = new ArrayList<>();
            respuestas1.add("Sí, es la primera vez");
            respuestas1.add("No, he tomado clases regularmente");
            respuestas1.add("No, hace mucho tomé clases");
            listaDePreguntas.add(new PreguntaRecomendacion("¿Es la primera vez que tomarás clases de música", respuestas1));

            ArrayList<String> respuestas2 = new ArrayList<>();
            respuestas2.add("Guitarra");
            respuestas2.add("Bajo");
            respuestas2.add("Flauta");
            respuestas2.add("Trompeta");
            respuestas2.add("Batería");
            respuestas2.add("Piano");
            respuestas2.add("Ukelele");
            respuestas2.add("Violín");
            respuestas2.add("Canto");
            respuestas2.add("Otro");
            listaDePreguntas.add(new PreguntaRecomendacion("¿Para comenzar qué instrumento te gustaría aprender a tocar", respuestas2));

            ArrayList<String> respuestas3 = new ArrayList<>();
            respuestas3.add("Cada día");
            respuestas3.add("Cada tres días");
            respuestas3.add("Cada semana");
            respuestas3.add("Cada dos semanas");
            listaDePreguntas.add(new PreguntaRecomendacion("¿Con qué frecuencia desea tomar clases", respuestas3));

            ArrayList<String> respuestas4 = new ArrayList<>();
            respuestas4.add("Pop");
            respuestas4.add("Rock");
            respuestas4.add("Hip hop/rap");
            respuestas4.add("Electrónica");
            respuestas4.add("Jazz");
            respuestas4.add("Blues");
            respuestas4.add("Reggaetón");
            respuestas4.add("Reggae");
            respuestas4.add("Clásica");
            respuestas4.add("Country");
            respuestas4.add("Metal");
            respuestas4.add("Folk");
            respuestas4.add("Independiente");
            listaDePreguntas.add(new PreguntaRecomendacion("¿Qué géneros musicales deseas aprender a tocar", respuestas4));
        }

        mostrarPregunta(currentPreguntaIndex);

        // Botón para siguiente pregunta
        btn_siguiente_pregunta.setOnClickListener(v -> {
            if (listaDePreguntas.get(currentPreguntaIndex).getRespuestaElegida() != null) {
                guardarRespuestaElegida(currentPreguntaIndex); // Guarda antes de avanzar
                if (currentPreguntaIndex < listaDePreguntas.size() - 1) {
                    currentPreguntaIndex++;
                    mostrarPregunta(currentPreguntaIndex);
                } else {
                    cargarRespuestas();
                    Intent intent = new Intent(PreguntasRecomendacion.this, Home.class);
                    startActivity(intent);
                    finish(); // Opcional, para cerrar esta actividad
                }
            } else {
                Toast.makeText(this, "Contesta la pregunta", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para pregunta anterior
        btn_anterior_pregunta.setOnClickListener(v -> {
            if (listaDePreguntas.get(currentPreguntaIndex).getRespuestaElegida() != null) {
                guardarRespuestaElegida(currentPreguntaIndex); // Guarda antes de retroceder
                if (currentPreguntaIndex > 0) {
                    currentPreguntaIndex--;
                    mostrarPregunta(currentPreguntaIndex);
                }
            } else {
                Toast.makeText(this, "Contesta la pregunta", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para guardar automáticamente la respuesta cuando el usuario seleccione una opción
        rg_opcionespregunta.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedRadioIndex = group.indexOfChild(findViewById(checkedId));
            if (selectedRadioIndex != -1) {
                listaDePreguntas.get(currentPreguntaIndex).setRespuestaElegida(selectedRadioIndex);
                guardarRespuestaElegida(currentPreguntaIndex);
            }
        });
    }

    private void mostrarPregunta(int index) {
        PreguntaRecomendacion pregunta = listaDePreguntas.get(index);
        txt_preguntas.setText(pregunta.getPregunta());
        // Limpiar opciones anteriores
        rg_opcionespregunta.clearCheck();
        rg_opcionespregunta.removeAllViews();
        // Configurar dinámicamente los RadioButtons para las opciones
        List<String> respuestas = pregunta.getRespuestas();
        if (respuestas == null) return;  // Evita crasheos si las respuestas son nulas

        for (int i = 0; i < respuestas.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(respuestas.get(i));
            radioButton.setId(View.generateViewId());

            if (pregunta.getRespuestaElegida() != null && pregunta.getRespuestaElegida() == i) {
                radioButton.setChecked(true);  // Si la respuesta elegida coincide, marcar el radioButton
            }
            rg_opcionespregunta.addView(radioButton); // Agregar el RadioButton al grupo
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarRespuestaElegida(currentPreguntaIndex);
    }

    private void guardarRespuestaElegida(int index1) {
        int selectedRadioIndex = rg_opcionespregunta.indexOfChild(findViewById(rg_opcionespregunta.getCheckedRadioButtonId()));
        if (selectedRadioIndex != -1) {  // Solo guarda si hay una selección válida
            listaDePreguntas.get(index1).setRespuestaElegida(selectedRadioIndex);
            // Guardar en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("preguntas_respuestas", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("respuesta_" + index1, selectedRadioIndex);
            editor.apply();
        }
    }

    private void cargarRespuestas() {
        String correo = dbUser.getCorreoUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference recomendacionRef = db.collection("usuarios")
                .document(correo)
                .collection("recomendaciones")
                .document("respuestas");

        recomendacionRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<PreguntaRecomendacion> preguntasRecuperadas = new ArrayList<>();
                List<Map<String, Object>> preguntasMap = (List<Map<String, Object>>) documentSnapshot.get("preguntasRecomendacion");

                if (preguntasMap != null) {
                    for (Map<String, Object> preguntaMap : preguntasMap) {
                        String pregunta = (String) preguntaMap.get("pregunta");
                        List<String> respuestas = (List<String>) preguntaMap.get("respuestas");
                        Long respuestaElegida = (Long) preguntaMap.get("respuestaElegida");

                        preguntasRecuperadas.add(new PreguntaRecomendacion(pregunta, new ArrayList<>(respuestas), respuestaElegida != null ? respuestaElegida.intValue() : null));
                    }
                }

                listaDePreguntas.clear();
                listaDePreguntas.addAll(preguntasRecuperadas);
                mostrarPregunta(currentPreguntaIndex);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar respuestas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
