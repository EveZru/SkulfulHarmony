package com.example.skulfulharmony;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterArchivosOffline;
import com.example.skulfulharmony.adapters.AdapterPreguntasOffline;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerClaseOffline extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView tvTitulo, tvTexto, tvSinArchivo, tvPreguntasTitulo;
    private RecyclerView rvArchivos, rvPreguntas;
    private Button btnEnviarRespuestas;

    private int idCurso = 0;
    private int idClase = 0;

    private long tiempoInicioClase = 0;
    private long tiempoAcumuladoClase = 0;
    private List<PreguntaCuestionario> preguntasGuardadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clase_offline);

        playerView = findViewById(R.id.exoplayer_clase_offline);
        tvTitulo = findViewById(R.id.tv_titulo_clase_offline);
        tvTexto = findViewById(R.id.tv_texto_clase_offline);
        tvSinArchivo = findViewById(R.id.tv_no_disponible);
        tvPreguntasTitulo = findViewById(R.id.tv_preguntas_titulo);
        rvArchivos = findViewById(R.id.rv_archivos_offline);
        rvPreguntas = findViewById(R.id.rv_preguntas_offline);
        btnEnviarRespuestas = findViewById(R.id.btn_enviar_respuestas_offline);

        final String finalTitulo = getIntent().getStringExtra("titulo");
        String videoUrl = getIntent().getStringExtra("video");
        String documentoUrl = getIntent().getStringExtra("documento");
        ArrayList<String> archivos = getIntent().getStringArrayListExtra("archivos");

        idCurso = getIntent().getIntExtra("idCurso", -1);
        idClase = getIntent().getIntExtra("idClase", -1);

        String tituloMostrar = (finalTitulo != null) ? finalTitulo : "Clase sin título";
        tvTitulo.setText(tituloMostrar);

        if (videoUrl == null) videoUrl = "";
        if (documentoUrl == null) documentoUrl = "";
        if (archivos == null) archivos = new ArrayList<>();

        ImageView ivMenu = findViewById(R.id.iv_menu_curso_descargado); // Usa el mismo ID del XML
        ivMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_curso_descargado, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.it_borrar_curso) {
                    eliminarClaseOffline();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        // Video
        File videoFile = new File(videoUrl);
        if (videoFile.exists()) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        // Archivo principal
        File docFile = new File(documentoUrl);
        if (docFile.exists()) {
            tvTexto.setText("Archivo destacado: " + docFile.getName());
        } else {
            tvTexto.setText("Archivo principal no disponible.");
        }

        // Archivos adicionales
        List<String> archivosDisponibles = new ArrayList<>();
        for (String path : archivos) {
            if (path != null && !path.isEmpty() && new File(path).exists()) {
                archivosDisponibles.add(path);
            }
        }

        if (!archivosDisponibles.isEmpty()) {
            rvArchivos.setLayoutManager(new LinearLayoutManager(this));
            AdapterArchivosOffline adapter = new AdapterArchivosOffline(archivosDisponibles, this);
            rvArchivos.setAdapter(adapter);
            tvSinArchivo.setText("");
        } else {
            tvSinArchivo.setText("No hay archivos disponibles offline.");
        }

        DbHelper dbHelper = new DbHelper(this);
        List<PreguntaCuestionario> preguntas = dbHelper.obtenerPreguntasPorClase(finalTitulo);
        Log.d("VERCLASEOFFLINE", "📦 Total preguntas recuperadas: " + preguntas.size());
        if (!preguntas.isEmpty()) {
            preguntasGuardadas = preguntas;
            tvPreguntasTitulo.setText("Preguntas del cuestionario");
            rvPreguntas.setLayoutManager(new LinearLayoutManager(this));
            rvPreguntas.setAdapter(new AdapterPreguntasOffline(preguntas));
        } else {
            tvPreguntasTitulo.setText(""); // Ocultar título si no hay
        }

        btnEnviarRespuestas.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Log.w("OFFLINE_SYNC", "Usuario no autenticado");
                return;
            }

            final List<PreguntaCuestionario> preguntasLista = new ArrayList<>(preguntasGuardadas);
            final List<Integer> respuestasUsuario = dbHelper.obtenerRespuestasUsuarioPorClase(finalTitulo);
            final int finalIdCurso = idCurso;
            final int finalIdClase = idClase;

            if (respuestasUsuario.size() != preguntasLista.size()) {
                Log.e("OFFLINE_SYNC", "❌ Respuestas incompletas o mal sincronizadas");
                return;
            }

            Map<String, Object> mapaRespuestas = new HashMap<>();
            int correctas = 0;

            for (int i = 0; i < preguntasLista.size(); i++) {
                PreguntaCuestionario p = preguntasLista.get(i);
                int respUsuario = respuestasUsuario.get(i);
                int respCorrecta = p.getRespuestaCorrecta();

                if (respUsuario == respCorrecta) {
                    mapaRespuestas.put("pregunta_" + i, true);
                    correctas++;
                } else {
                    mapaRespuestas.put("pregunta_" + i, false);
                }
            }

            final int correctasFinal = correctas;
            int total = preguntasLista.size();
            int incorrectas = total - correctasFinal;
            int porcentaje = (int) (((double) correctasFinal / total) * 100);

            // ✅ Dialog original con botones
            runOnUiThread(() -> {
                new androidx.appcompat.app.AlertDialog.Builder(VerClaseOffline.this)
                        .setTitle("Resultados")
                        .setMessage("Correctas: " + correctasFinal + "\nIncorrectas: " + incorrectas)
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            enviarResultadosAFirebase(currentUser, finalTitulo, preguntasLista, respuestasUsuario, mapaRespuestas, finalIdCurso, finalIdClase, dbHelper);
                        })
                        .setNegativeButton("Reintentar", (dialog, which) -> {
                            Toast.makeText(VerClaseOffline.this, "Puedes modificar tus respuestas", Toast.LENGTH_SHORT).show();
                        })
                        .show();
            });

            // 🟦 Nuevo dialogo personalizado como Ver_clases
            runOnUiThread(() -> {
                Dialog dialog = new Dialog(VerClaseOffline.this);
                dialog.setContentView(R.layout.dialog_calificacionpreguntas_verclase);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setDimAmount(0.5f);

                TextView mensajeGrande = dialog.findViewById(R.id.txt_mensajegrande_dialogcalificacionpreguntas);
                TextView mensajePequeno = dialog.findViewById(R.id.txt_mensajepequeno_dialogcalificacionpreguntas);
                TextView puntuacion = dialog.findViewById(R.id.txt_puntuacion_dialogcalificacionpreguntas);
                Button aceptar = dialog.findViewById(R.id.btn_aceptar_dialogcalificacionpreguntas);
                Button reintentar = dialog.findViewById(R.id.btn_reintentar_dialogcalificacionpreguntas);

                mensajeGrande.setText(MensajeCalificacion.obtenerMensaje(porcentaje));
                mensajePequeno.setText("Has completado la tarea con éxito.");
                puntuacion.setText(correctasFinal + "/" + total);

                aceptar.setOnClickListener(v1 -> dialog.dismiss());
                reintentar.setOnClickListener(v1 -> {
                    Toast.makeText(VerClaseOffline.this, "Puedes modificar tus respuestas", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                dialog.show();
            });

            // 🔁 Guardado Firebase
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference userRef = firestore.collection("usuarios").document(currentUser.getUid());

            userRef.get().addOnSuccessListener(snapshot -> {
                List<Map<String, Object>> listaRespuestas = new ArrayList<>();
                Object raw = snapshot.get("respuestasIncorrectas");
                if (raw instanceof List<?>) {
                    for (Object r : (List<?>) raw) {
                        if (r instanceof Map) listaRespuestas.add((Map<String, Object>) r);
                    }
                }

                boolean actualizado = false;
                for (Map<String, Object> intento : listaRespuestas) {
                    Long cId = ((Number) intento.get("idCurso")).longValue();
                    Long clId = ((Number) intento.get("idClase")).longValue();

                    if (cId == finalIdCurso && clId == finalIdClase) {
                        List<Map<String, Object>> previos = new ArrayList<>();
                        Object rawPrevios = intento.get("intentos");
                        if (rawPrevios instanceof List<?>) {
                            for (Object obj : (List<?>) rawPrevios) {
                                if (obj instanceof Map) {
                                    previos.add((Map<String, Object>) obj);
                                }
                            }
                        }
                        previos.add(new HashMap<>(mapaRespuestas));
                        intento.put("intentos", previos);
                        actualizado = true;
                        break;
                    }
                }

                if (!actualizado) {
                    Map<String, Object> nuevoIntento = new HashMap<>();
                    nuevoIntento.put("idCurso", finalIdCurso);
                    nuevoIntento.put("idClase", finalIdClase);
                    List<Map<String, Object>> intentos = new ArrayList<>();
                    intentos.add(new HashMap<>(mapaRespuestas));
                    nuevoIntento.put("intentos", intentos);
                    listaRespuestas.add(nuevoIntento);
                }

                userRef.set(new HashMap<String, Object>() {{
                    put("respuestasIncorrectas", listaRespuestas);
                }}, SetOptions.merge());

                if (correctasFinal == preguntasLista.size()) {
                    guardarClaseComoCompletadaOffline(userRef);
                    dbHelper.marcarClaseComoCompletadaLocal(finalTitulo);
                    dbHelper.guardarProgresoOffline(finalTitulo, tiempoAcumuladoClase, true);
                }

                Log.d("OFFLINE_SYNC", "📤 Respuestas enviadas correctamente");

                runOnUiThread(() -> {
                    Toast.makeText(this, "Respuestas enviadas", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });

            }).addOnFailureListener(e -> Log.e("OFFLINE_SYNC", "🔥 Error al obtener documento de usuario", e));
        });
    }

    private void guardarClaseComoCompletadaOffline(DocumentReference userRef) {
        userRef.get().addOnSuccessListener(snapshot -> {
            Map<String, Object> progresoTemp = new HashMap<>();
            Object raw = snapshot.get("progresoCursoOffline");
            if (raw instanceof Map) {
                progresoTemp = (Map<String, Object>) raw;
            }

            String claveCurso = "curso_" + idCurso;
            List<Long> clasesVistas = new ArrayList<>();
            if (progresoTemp.containsKey(claveCurso)) {
                Object rawLista = progresoTemp.get(claveCurso);
                if (rawLista instanceof List<?>) {
                    for (Object id : (List<?>) rawLista) {
                        if (id instanceof Number) {
                            clasesVistas.add(((Number) id).longValue());
                        }
                    }
                }
            }

            if (!clasesVistas.contains((long) idClase)) {
                clasesVistas.add((long) idClase);
                progresoTemp.put(claveCurso, clasesVistas);

                Map<String, Object> datos = new HashMap<>();
                datos.put("progresoCursoOffline", progresoTemp);

                userRef.set(datos, SetOptions.merge())
                        .addOnSuccessListener(a -> Log.d("PROGRESO_OFFLINE", "✅ Clase marcada como vista offline"))
                        .addOnFailureListener(e -> Log.e("PROGRESO_OFFLINE", "❌ Error al guardar progreso offline", e));
            }
        });
    }

    private void eliminarClaseOffline() {
        String tituloClase = getIntent().getStringExtra("titulo");
        String imagen = getIntent().getStringExtra("imagen");
        String video = getIntent().getStringExtra("video");
        ArrayList<String> archivos = getIntent().getStringArrayListExtra("archivos");

        DbHelper db = new DbHelper(this);
        db.eliminarClasePorTitulo(tituloClase); // 🧹 Debes implementar este método si no lo tienes

        eliminarArchivo(imagen);
        eliminarArchivo(video);
        if (archivos != null) {
            for (String archivo : archivos) {
                eliminarArchivo(archivo);
            }
        }

        Toast.makeText(this, "Clase eliminada correctamente", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void eliminarArchivo(String ruta) {
        if (ruta != null) {
            File archivo = new File(ruta);
            if (archivo.exists()) archivo.delete();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tiempoInicioClase = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tiempoInicioClase > 0) {
            long tiempoVisto = (System.currentTimeMillis() - tiempoInicioClase) / 1000; // en segundos
            tiempoAcumuladoClase += tiempoVisto;
            tiempoInicioClase = 0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }

        guardarTiempoOffline();
    }

    private void guardarTiempoOffline() {
        if (tiempoAcumuladoClase >= 90) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String userId = user.getUid();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("usuarios").document(userId);

            String claveClase = "clase_" + idClase;
            Map<String, Object> tiempoPorClase = new HashMap<>();
            Map<String, Object> claseInfo = new HashMap<>();
            claseInfo.put("idCurso", idCurso);
            claseInfo.put("tiempo", tiempoAcumuladoClase);
            tiempoPorClase.put(claveClase, claseInfo);

            userRef.set(new HashMap<String, Object>() {{
                        put("tiempoVisualizadoOffline", tiempoPorClase);
                    }}, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TiempoOffline", "⏱️ Tiempo offline guardado: " + tiempoAcumuladoClase + "s"))
                    .addOnFailureListener(e -> Log.e("TiempoOffline", "❌ Error al guardar tiempo offline", e));

            DbHelper db = new DbHelper(this);
            db.guardarProgresoOffline(tvTitulo.getText().toString(), tiempoAcumuladoClase, false);
        }
    }

    private void enviarResultadosAFirebase(FirebaseUser currentUser, String finalTitulo,
                                           List<PreguntaCuestionario> preguntasLista,
                                           List<Integer> respuestasUsuario,
                                           Map<String, Object> mapaRespuestas,
                                           int finalIdCurso, int finalIdClase, DbHelper dbHelper) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firestore.collection("usuarios").document(currentUser.getUid());

        userRef.get().addOnSuccessListener(snapshot -> {
            List<Map<String, Object>> listaRespuestas = new ArrayList<>();
            Object raw = snapshot.get("respuestasIncorrectas");
            if (raw instanceof List<?>) {
                for (Object r : (List<?>) raw) {
                    if (r instanceof Map) listaRespuestas.add((Map<String, Object>) r);
                }
            }

            boolean actualizado = false;
            for (Map<String, Object> intento : listaRespuestas) {
                Long cId = ((Number) intento.get("idCurso")).longValue();
                Long clId = ((Number) intento.get("idClase")).longValue();

                if (cId == finalIdCurso && clId == finalIdClase) {
                    List<Map<String, Object>> previos = new ArrayList<>();
                    Object rawPrevios = intento.get("intentos");
                    if (rawPrevios instanceof List<?>) {
                        for (Object obj : (List<?>) rawPrevios) {
                            if (obj instanceof Map) {
                                previos.add((Map<String, Object>) obj);
                            }
                        }
                    }
                    previos.add(new HashMap<>(mapaRespuestas));
                    intento.put("intentos", previos);
                    actualizado = true;
                    break;
                }
            }

            if (!actualizado) {
                Map<String, Object> nuevoIntento = new HashMap<>();
                nuevoIntento.put("idCurso", finalIdCurso);
                nuevoIntento.put("idClase", finalIdClase);
                List<Map<String, Object>> intentos = new ArrayList<>();
                intentos.add(new HashMap<>(mapaRespuestas));
                nuevoIntento.put("intentos", intentos);
                listaRespuestas.add(nuevoIntento);
            }

            userRef.set(new HashMap<String, Object>() {{
                put("respuestasIncorrectas", listaRespuestas);
            }}, SetOptions.merge());

            // 🔁 Reemplazo de .stream().map().toList() por código manual compatible
            List<Integer> respuestasCorrectas = new ArrayList<>();
            for (PreguntaCuestionario p : preguntasLista) {
                respuestasCorrectas.add(p.getRespuestaCorrecta());
            }

            if (preguntasLista.size() == respuestasUsuario.size() &&
                    respuestasUsuario.equals(respuestasCorrectas)) {
                guardarClaseComoCompletadaOffline(userRef);
                dbHelper.marcarClaseComoCompletadaLocal(finalTitulo);
                dbHelper.guardarProgresoOffline(finalTitulo, tiempoAcumuladoClase, true);
            }

            Log.d("OFFLINE_SYNC", "📤 Respuestas enviadas correctamente");

            runOnUiThread(() -> {
                Toast.makeText(this, "Respuestas enviadas", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });

        }).addOnFailureListener(e -> Log.e("OFFLINE_SYNC", "🔥 Error al obtener documento de usuario", e));
    }

    private enum MensajeCalificacion {
        CIEN_CORRECTO(100, "¡Perfecto!"),
        OCHENTA_CORRECTO(80, "¡Felicidades!"),
        SESENTA_CORRECTO(60, "¡Buen trabajo!"),
        CUARENTA_CORRECTO(40, "¡Tú puedes!"),
        VEINTE_CORRECTO(20, "¡No te rindas!"),
        CERO_CORRECTO(0, "¡Sigue intentándolo!");

        private final int minimoPorcentaje;
        private final String mensaje;

        MensajeCalificacion(int minimoPorcentaje, String mensaje) {
            this.minimoPorcentaje = minimoPorcentaje;
            this.mensaje = mensaje;
        }

        public static String obtenerMensaje(int porcentaje) {
            for (MensajeCalificacion mensaje : MensajeCalificacion.values()) {
                if (porcentaje >= mensaje.minimoPorcentaje) {
                    return mensaje.mensaje;
                }
            }
            return "¡Ups!";
        }
    }

}