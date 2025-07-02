package com.example.skulfulharmony;

import android.content.Intent;
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
import com.example.skulfulharmony.javaobjects.clustering.DataClusterList;
import com.example.skulfulharmony.javaobjects.clustering.RespuestasCuestionario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaRecomendacion;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PreguntasRecomendacion extends AppCompatActivity {
    private TextView txt_preguntas;
    private RadioGroup rg_opcionespregunta;
    private Button btn_siguiente_pregunta, btn_anterior_pregunta;
    private int currentPreguntaIndex = 0;
    private final List<PreguntaRecomendacion> listaDePreguntas = new ArrayList<>();
    private final DbUser dbUser = new DbUser(this);

    private static final ArrayList<String> respuestas1 = new ArrayList<>() {{
        add("Sí, es la primera vez");
        add("No, he tomado clases regularmente");
        add("No, hace mucho tomé clases");
    }};

    private static final ArrayList<String> respuestas2 = new ArrayList<>() {{
        addAll(DataClusterList.listaInstrumentos.stream()
                .map(map -> new ArrayList<>(map.keySet()).get(0))
                .collect(Collectors.toList()));
    }};

    private static final ArrayList<String> respuestas3 = new ArrayList<>() {{
        add("Cada día");
        add("Cada tres días");
        add("Cada semana");
        add("Cada dos semanas");
    }};

    private static final ArrayList<String> respuestas4 = new ArrayList<>() {{
        addAll(DataClusterList.listaGenero.stream()
                .map(map -> new ArrayList<>(map.keySet()).get(0))
                .collect(Collectors.toList()));
    }};

    @Override
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

        listaDePreguntas.add(new PreguntaRecomendacion("¿Es la primera vez que tomarás clases de música?", respuestas1));
        listaDePreguntas.add(new PreguntaRecomendacion("¿Para comenzar qué instrumento te gustaría aprender a tocar?", respuestas2));
        listaDePreguntas.add(new PreguntaRecomendacion("¿Con qué frecuencia desea tomar clases?", respuestas3));
        listaDePreguntas.add(new PreguntaRecomendacion("¿Qué géneros musicales deseas aprender a tocar?", respuestas4));

        mostrarPregunta(currentPreguntaIndex);

        btn_siguiente_pregunta.setOnClickListener(v -> {
            if (listaDePreguntas.get(currentPreguntaIndex).getRespuestaElegida() != null) {
                guardarRespuestaElegida(currentPreguntaIndex);
                if (currentPreguntaIndex < listaDePreguntas.size() - 1) {
                    currentPreguntaIndex++;
                    mostrarPregunta(currentPreguntaIndex);
                } else {
                    guardarRespuestasFirebase();
                }
            } else {
                Toast.makeText(this, "Contesta la pregunta", Toast.LENGTH_SHORT).show();
            }
        });

        btn_anterior_pregunta.setOnClickListener(v -> {
            if (currentPreguntaIndex > 0) {
                guardarRespuestaElegida(currentPreguntaIndex);
                currentPreguntaIndex--;
                mostrarPregunta(currentPreguntaIndex);
            }
        });

        rg_opcionespregunta.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedRadioIndex = group.indexOfChild(findViewById(checkedId));
            if (selectedRadioIndex != -1) {
                listaDePreguntas.get(currentPreguntaIndex).setRespuestaElegida(selectedRadioIndex);
            }
        });
    }

    private void mostrarPregunta(int index) {
        PreguntaRecomendacion pregunta = listaDePreguntas.get(index);
        txt_preguntas.setText(pregunta.getPregunta());
        rg_opcionespregunta.clearCheck();
        rg_opcionespregunta.removeAllViews();
        List<String> respuestas = pregunta.getRespuestas();
        if (respuestas == null) return;

        for (int i = 0; i < respuestas.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(respuestas.get(i));
            radioButton.setId(View.generateViewId());
            radioButton.setTextSize(15f);
            radioButton.setTextColor(getResources().getColor(R.color.white));
            radioButton.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            if (pregunta.getRespuestaElegida() != null && pregunta.getRespuestaElegida() == i) {
                radioButton.setChecked(true);
            }
            rg_opcionespregunta.addView(radioButton);
        }
    }

    private void guardarRespuestaElegida(int index) {
        int selectedRadioIndex = rg_opcionespregunta.indexOfChild(findViewById(rg_opcionespregunta.getCheckedRadioButtonId()));
        if (selectedRadioIndex != -1) {
            listaDePreguntas.get(index).setRespuestaElegida(selectedRadioIndex);
        }
    }

    private void guardarRespuestasFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String correo = user.getEmail();

        db.collection("usuarios")
                .whereEqualTo("correo", correo)
                .limit(1)
                .get()
                .addOnSuccessListener(result -> {
                    if (!result.isEmpty()) {
                        DocumentReference ref = result.getDocuments().get(0).getReference();
                        RespuestasCuestionario respuestas = new RespuestasCuestionario();

                        for (int i = 0; i < listaDePreguntas.size(); i++) {
                            Integer respuestaIdx = listaDePreguntas.get(i).getRespuestaElegida();
                            if (respuestaIdx == null) continue;
                            switch (i) {
                                case 0:
                                    respuestas.setDificultad(new ArrayList<>(DataClusterList.listaDificultad.get(respuestaIdx).keySet()).get(0));
                                    break;
                                case 1:
                                    respuestas.setInstrumento(new ArrayList<>(DataClusterList.listaInstrumentos.get(respuestaIdx).keySet()).get(0));
                                    break;
                                case 2:
                                    respuestas.setFecha(respuestas3.get(respuestaIdx));
                                    break;
                                case 3:
                                    respuestas.setGenero(new ArrayList<>(DataClusterList.listaGenero.get(respuestaIdx).keySet()).get(0));
                                    break;
                            }
                        }

                        ref.update("respuestasCuestionario", respuestas)
                                .addOnSuccessListener(unused -> {
                                    startActivity(new Intent(this, Home.class).putExtra("primeraVez", true));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                });
    }
}
