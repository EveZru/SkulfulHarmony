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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreguntasIncorrectas extends AppCompatActivity {
    private RecyclerView rvPreguntasIncorrectas;
    private TextView tvPreguntasIncorrectas;
    private AdapterPreguntasEnCuestionariosparaContestar adapterPreguntas;
    private Button btnAccionPrincipal;
    private static final String TAG = "PreguntasIncorrectas";
    private final FirebaseFirestore bd = FirebaseFirestore.getInstance();
    private List<PreguntaCuestionario> preguntasIncorrectasCargadas;
    private SharedPreferences prefs;
    private TextView tvPreguntasCorregidas;

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

        tvPreguntasCorregidas = findViewById(R.id.tv_preguntas_corregidas);

        cargarPreguntasDesdeSharedPreferences();
        cargarYContarCorregidasDesdeFirebase();
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
        int totalGuardadas = prefs.getInt("total_preguntas_guardadas", 0);
        int corregidas = totalGuardadas - preguntasIncorrectasCargadas.size();

        if (totalGuardadas > 0) {
            tvPreguntasCorregidas.setText("Has corregido " + corregidas + " de " + totalGuardadas + " preguntas incorrectas.");
        } else {
            tvPreguntasCorregidas.setText("");
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

            List<PreguntaCuestionario> preguntasQueQuedaronIncorrectas = adapterPreguntas.getPreguntasIncorrectas();

              preguntasIncorrectasCargadas.clear();
            preguntasIncorrectasCargadas.addAll(preguntasQueQuedaronIncorrectas);

            adapterPreguntas.notifyDataSetChanged();

        Gson gson = new Gson();
        String jsonActualizado = gson.toJson(preguntasIncorrectasCargadas);
        prefs.edit().putString("preguntas_incorrectas", jsonActualizado).apply();
        Log.d(TAG, "Preguntas restantes guardadas en SharedPreferences. Cantidad: " + preguntasIncorrectasCargadas.size());

         btnAccionPrincipal.setText("Salir");

         if (preguntasIncorrectasCargadas.isEmpty()) {
            tvPreguntasIncorrectas.setText("¡Felicidades! Has respondido correctamente todas las preguntas incorrectas. ¡Sigue practicando!");
            tvPreguntasIncorrectas.setVisibility(View.VISIBLE);
            rvPreguntasIncorrectas.setVisibility(View.GONE);
        }
    }

    public static int contarPreguntasCorregidas(List<Map<String, Object>> intentos) {
        if (intentos.size() < 2) return 0;

        Map<String, Boolean> primerIntento = new HashMap<>();
        Map<String, Boolean> ultimoIntento = new HashMap<>();

        for (Map.Entry<String, Object> entry : intentos.get(0).entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                primerIntento.put(entry.getKey(), (Boolean) entry.getValue());
            }
        }
        for (Map.Entry<String, Object> entry : intentos.get(intentos.size() - 1).entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                ultimoIntento.put(entry.getKey(), (Boolean) entry.getValue());
            }
        }
        int corregidas = 0;
        for (String key : primerIntento.keySet()) {
            boolean falloAntes = !primerIntento.get(key);
            boolean acertoDespues = ultimoIntento.getOrDefault(key, false);
            if (falloAntes && acertoDespues) {
                corregidas++;
            }
        }

        return corregidas;
    }

    private void cargarYContarCorregidasDesdeFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        bd.collection("usuarios")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Object raw = documentSnapshot.get("respuestasIncorrectas");
                    if (raw instanceof List<?>) {
                        List<Map<String, Object>> lista = (List<Map<String, Object>>) raw;
                        Map<Long, Integer> correccionesPorCursoClase = new HashMap<>();
                        Map<Long, String> cacheTitulosCurso = new HashMap<>();
                        Map<Long, String> cacheTitulosClase = new HashMap<>();
                        final int[] totalCorregidas = {0};

                        for (Object entrada : lista) {
                            if (!(entrada instanceof Map)) continue;

                            Map<String, Object> intentoMap = (Map<String, Object>) entrada;
                            Object intentosRaw = intentoMap.get("intentos");
                            if (!(intentosRaw instanceof List<?>)) continue;

                            List<Map<String, Object>> intentos = new ArrayList<>();
                            for (Object o : (List<?>) intentosRaw) {
                                if (o instanceof Map) {
                                    intentos.add((Map<String, Object>) o);
                                }
                            }

                            int corregidas = contarPreguntasCorregidas(intentos);
                            if (corregidas > 0) {
                                long idCurso = ((Number) intentoMap.get("idCurso")).longValue();
                                long idClase = ((Number) intentoMap.get("idClase")).longValue();

                                long keyUnica = idCurso * 1000 + idClase;
                                correccionesPorCursoClase.put(keyUnica, corregidas);
                                totalCorregidas[0] += corregidas;

                                // Cargar título del curso si no está
                                if (!cacheTitulosCurso.containsKey(idCurso)) {
                                    bd.collection("cursos")
                                            .whereEqualTo("idCurso", idCurso)
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener(snapshot -> {
                                                String nombreCurso = "Curso " + idCurso;
                                                if (!snapshot.isEmpty()) {
                                                    nombreCurso = snapshot.getDocuments().get(0).getString("titulo");
                                                }
                                                cacheTitulosCurso.put(idCurso, nombreCurso);
                                                mostrarMensajeFinal(correccionesPorCursoClase, cacheTitulosCurso, cacheTitulosClase, totalCorregidas[0]);
                                            });
                                }

                                if (!cacheTitulosClase.containsKey(idClase)) {
                                    bd.collection("clases")
                                            .whereEqualTo("idClase", idClase)
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener(snapshot -> {
                                                String nombreClase = "Clase " + idClase;
                                                if (!snapshot.isEmpty()) {
                                                    nombreClase = snapshot.getDocuments().get(0).getString("titulo");
                                                }
                                                cacheTitulosClase.put(idClase, nombreClase);
                                                mostrarMensajeFinal(correccionesPorCursoClase, cacheTitulosCurso, cacheTitulosClase, totalCorregidas[0]);
                                            });
                                }
                            }
                        }

                        mostrarMensajeFinal(correccionesPorCursoClase, cacheTitulosCurso, cacheTitulosClase, totalCorregidas[0]);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener respuestasIncorrectas", e));
    }

    private void mostrarMensajeFinal(Map<Long, Integer> correccionesPorCursoClase, Map<Long, String> cacheTitulosCurso, Map<Long, String> cacheTitulosClase, int totalCorregidas) {
        runOnUiThread(() -> {
            StringBuilder mensaje = new StringBuilder();
            for (Map.Entry<Long, Integer> entry : correccionesPorCursoClase.entrySet()) {
                long key = entry.getKey();
                int corregidas = entry.getValue();
                long idCurso = key / 1000;
                long idClase = key % 1000;

                String nombreCurso = cacheTitulosCurso.getOrDefault(idCurso, "Curso " + idCurso);
                String nombreClase = cacheTitulosClase.getOrDefault(idClase, "Clase " + idClase);

                mensaje.append("📘 ").append(nombreCurso)
                        .append(" / ").append(nombreClase)
                        .append(": ").append(corregidas).append(" corregidas\n");
            }

            if (totalCorregidas > 0) {
                tvPreguntasCorregidas.setText("Has corregido un total de " + totalCorregidas + " preguntas:\n\n" + mensaje.toString());
            } else {
                tvPreguntasCorregidas.setText(" Aún no has corregido ninguna pregunta fallada.");
            }
        });
    }



}