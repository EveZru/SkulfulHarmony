package com.example.skulfulharmony;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasEnCuestionariosparaContestar;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreguntasIncorrectas extends AppCompatActivity {
    private RecyclerView rvPreguntasIncorrectas;
    private TextView tvPreguntasIncorrectas;
    private AdapterPreguntasEnCuestionariosparaContestar adapterPreguntas;
    private Button btnAccionPrincipal;
    private static final String TAG = "PreguntasIncorrectas";
    private final FirebaseFirestore bd = FirebaseFirestore.getInstance();
    private List<PreguntaCuestionario> preguntasIncorrectasCargadas;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_incorrectas);

        tvPreguntasIncorrectas = findViewById(R.id.tv_preguntas_incorrectas);
        rvPreguntasIncorrectas = findViewById(R.id.rv_preguntas_incorrectas);
        btnAccionPrincipal = findViewById(R.id.btn_reintentar); // Este botón ahora funcionará para ambas acciones

        rvPreguntasIncorrectas.setLayoutManager(new LinearLayoutManager(this));

        preguntasIncorrectasCargadas = new ArrayList<>();
        prefs = getSharedPreferences("cuestionario", MODE_PRIVATE);

        cargarPreguntasDesdeSharedPreferences();
        setupUIAndListeners();
    }

    private void cargarPreguntasDesdeSharedPreferences() {
        String json = prefs.getString("preguntas_incorrectas", null);

        if (json != null && !json.trim().isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PreguntaCuestionario>>() {}.getType();
            try {
                List<PreguntaCuestionario> listaTemporal = gson.fromJson(json, listType);
                if (listaTemporal != null) {
                    preguntasIncorrectasCargadas.addAll(listaTemporal);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar preguntas incorrectas desde SharedPreferences: " + e.getMessage(), e);
                Toast.makeText(this, "Error al cargar preguntas incorrectas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupUIAndListeners() {
        if (preguntasIncorrectasCargadas.isEmpty()) {
            tvPreguntasIncorrectas.setText("No hay preguntas incorrectas registradas aún. ¡Sigue practicando!");
            tvPreguntasIncorrectas.setVisibility(View.VISIBLE);
            rvPreguntasIncorrectas.setVisibility(View.GONE);
            btnAccionPrincipal.setVisibility(View.GONE);
        } else {
            tvPreguntasIncorrectas.setVisibility(View.GONE);
            rvPreguntasIncorrectas.setVisibility(View.VISIBLE);


            adapterPreguntas = new AdapterPreguntasEnCuestionariosparaContestar(this, preguntasIncorrectasCargadas);
            // Mostrar las preguntas en modo para contestar al principio
            adapterPreguntas.setMostrarResultados(false); // Importante: Inicialmente no se muestran resultados
            rvPreguntasIncorrectas.setAdapter(adapterPreguntas);

            // Configuramos el botón para que diga "Comprobar" inicialmente
            btnAccionPrincipal.setText("Comprobar");
            btnAccionPrincipal.setVisibility(View.VISIBLE);

            btnAccionPrincipal.setOnClickListener(v -> {
                if (btnAccionPrincipal.getText().toString().equals("Comprobar")) {
                    procesarRespuestas();
                } else { // Si el texto es "Salir"
                    Intent intent = new Intent(PreguntasIncorrectas.this, Home.class); // Asegúrate que 'Home.class' sea la actividad correcta
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void procesarRespuestas() {
        adapterPreguntas.comprobarRespuestas();
        Toast.makeText(this, "Respuestas comprobadas", Toast.LENGTH_SHORT).show();

        //  Obtener la lista de preguntas que el adaptador identificó como INCORRECTAS

        List<PreguntaCuestionario> preguntasQueQuedaronIncorrectas = adapterPreguntas.getPreguntasIncorrectas();

        preguntasIncorrectasCargadas.clear();
        preguntasIncorrectasCargadas.addAll(preguntasQueQuedaronIncorrectas);

        // 4. Notificar al adaptador que los datos subyacentes han cambiado (aunque su lista interna ya refleja esto).
        //    Esto es más una buena práctica si el adaptador estuviera observando la lista original.
        //    En tu caso, `adapterPreguntas.comprobarRespuestas()` ya llama a `notifyDataSetChanged()`,
        //    así que esto podría ser redundante, pero no hace daño.
        adapterPreguntas.notifyDataSetChanged();


        // 5. Guardar la lista actualizada (solo las preguntas incorrectas restantes) en SharedPreferences.
        Gson gson = new Gson();
        String jsonActualizado = gson.toJson(preguntasIncorrectasCargadas);
        prefs.edit().putString("preguntas_incorrectas", jsonActualizado).apply();
        Log.d(TAG, "Preguntas restantes guardadas en SharedPreferences. Cantidad: " + preguntasIncorrectasCargadas.size());

        //  Cambiar el texto del botón a "Salir".
        btnAccionPrincipal.setText("Salir");

        // Actualizar la interfaz de usuario si no quedan preguntas incorrectas.
        if (preguntasIncorrectasCargadas.isEmpty()) {
            tvPreguntasIncorrectas.setText("¡Felicidades! Has respondido correctamente todas las preguntas incorrectas. ¡Sigue practicando!");
            tvPreguntasIncorrectas.setVisibility(View.VISIBLE);
            rvPreguntasIncorrectas.setVisibility(View.GONE);
        }
    }



}