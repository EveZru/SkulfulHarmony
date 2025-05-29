package com.example.skulfulharmony;

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
    private Button btnReintentar, btnComprobar;
    private static final String TAG = "PreguntasIncorrectas";
    private final FirebaseFirestore bd = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_incorrectas);

        tvPreguntasIncorrectas = findViewById(R.id.tv_preguntas_incorrectas);
        rvPreguntasIncorrectas = findViewById(R.id.rv_preguntas_incorrectas);
        btnReintentar = findViewById(R.id.btn_reintentar);
        btnComprobar = findViewById(R.id.btn_comprobar);

        rvPreguntasIncorrectas.setLayoutManager(new LinearLayoutManager(this));

        final List<PreguntaCuestionario> preguntasGuardadas = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("cuestionario", MODE_PRIVATE);
        String json = prefs.getString("preguntas_incorrectas", null);

        if (json != null && !json.trim().isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PreguntaCuestionario>>() {}.getType();
            try {
                List<PreguntaCuestionario> listaTemporal = gson.fromJson(json, listType);
                if (listaTemporal != null) {
                    preguntasGuardadas.addAll(listaTemporal);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar preguntas incorrectas: " + e.getMessage(), e);
                Toast.makeText(this, "Error al cargar preguntas incorrectas", Toast.LENGTH_SHORT).show();
            }
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            bd.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .get()
                    .addOnSuccessListener(query -> {
                        if (!query.isEmpty()) {
                            DocumentSnapshot docUsuario = query.getDocuments().get(0);
                            String idUsuario = docUsuario.getId();
                            Usuario usuario = docUsuario.toObject(Usuario.class);

                            List<PreguntaCuestionario> preguntasViejas = new ArrayList<>();
                            if (usuario != null && usuario.getPreguntasRepaso() != null) {
                                preguntasViejas.addAll(usuario.getPreguntasRepaso());
                            }

                            for (PreguntaCuestionario pregunta : preguntasGuardadas) {
                                if (!preguntasViejas.contains(pregunta)) {
                                    preguntasViejas.add(pregunta);
                                }
                            }

                            bd.collection("usuarios")
                                    .document(idUsuario)
                                    .update("preguntasRepaso", preguntasViejas)
                                    .addOnSuccessListener(a -> Log.d(TAG, "Preguntas de refuerzo actualizadas"))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error actualizando preguntas de refuerzo", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error obteniendo usuario", e));
        }

        if (preguntasGuardadas.isEmpty()) {
            tvPreguntasIncorrectas.setText("No hay preguntas incorrectas registradas aún. ¡Sigue practicando!");
            tvPreguntasIncorrectas.setVisibility(View.VISIBLE);
            rvPreguntasIncorrectas.setVisibility(View.GONE);
            if (btnComprobar != null) btnComprobar.setVisibility(View.GONE);
            if (btnReintentar != null) btnReintentar.setVisibility(View.GONE);
        } else {
            tvPreguntasIncorrectas.setVisibility(View.GONE);
            rvPreguntasIncorrectas.setVisibility(View.VISIBLE);

            adapterPreguntas = new AdapterPreguntasEnCuestionariosparaContestar(this, preguntasGuardadas);
            adapterPreguntas.setMostrarResultados(true);
            rvPreguntasIncorrectas.setAdapter(adapterPreguntas);

            if (btnComprobar != null && btnReintentar != null) {
                btnComprobar.setVisibility(View.GONE);
                btnReintentar.setVisibility(View.VISIBLE);

                btnComprobar.setOnClickListener(v -> {
                    adapterPreguntas.comprobarRespuestas();
                    Toast.makeText(this, "Respuestas comprobadas.", Toast.LENGTH_SHORT).show();
                    btnComprobar.setVisibility(View.GONE);
                    btnReintentar.setVisibility(View.VISIBLE);
                });

                btnReintentar.setOnClickListener(v -> {
                    Toast.makeText(this, "Reintentando las preguntas incorrectas...", Toast.LENGTH_SHORT).show();
                    adapterPreguntas.setMostrarResultados(false);
                    adapterPreguntas.notifyDataSetChanged();
                    btnComprobar.setVisibility(View.VISIBLE);
                    btnReintentar.setVisibility(View.GONE);
                });
            }
        }
    }
}
