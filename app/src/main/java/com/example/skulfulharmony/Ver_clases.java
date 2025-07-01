package com.example.skulfulharmony;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.skulfulharmony.adapters.AdapterArchivosClase;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.clustering.DataClusterList;
import com.example.skulfulharmony.javaobjects.clustering.PreferenciasUsuario;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.notifications.NotificacionHelper;
import com.google.firebase.firestore.DocumentReference;

import com.dropbox.core.v2.clouddocs.UserInfo;
import com.example.skulfulharmony.adapters.AdapterPreguntasEnVerClase;
import com.example.skulfulharmony.adapters.AdapterVerClaseVerComentarios;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.modooffline.DescargaManager;
import com.example.skulfulharmony.modooffline.ClaseFirebase;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ver_clases extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvInfo, tvTitulo;
    private Button btnCalificar;
    private EditText etcomentario;
    private PlayerView playerViewPortrait;
    private ImageView iv_menupop,iv_like,iv_dislike;
    private ExoPlayer player;
    private Button crear_comentario;
    private int puntuacionActual = 0;
    private UserInfo user;
    private int idClase;
    private int idCurso;
    private Clase clase;
    private RecyclerView verPreguntas;
    private RecyclerView verComentarios;
    private AdapterPreguntasEnVerClase adapterPreguntasEnVerClase;
    private int cantidadRespuestasCorrectas;
    private FirebaseFirestore firestore;
    private boolean like=false, dislike=false;
    private long tiempoInicioClase = 0;
    private long tiempoAcumuladoClase = 0;

    private static final String TAG = "Ver_clases";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verclases);

        tvInfo = findViewById(R.id.tv_info_verclase);
        tvTitulo = findViewById(R.id.verclase_vertitulo);
        etcomentario = findViewById(R.id.et_comentario_verclase);

        verPreguntas = findViewById(R.id.rv_preguntasporclase_verclase);
        btnCalificar = findViewById(R.id.btt_revisar_respuestas_verclase);

        crear_comentario=findViewById(R.id.btn_subir_comentario_clase);
        verComentarios = findViewById(R.id.rv_comentarios_verclase);
        toolbar = findViewById(R.id.toolbartituloclase);
        Intent intent = getIntent();
        idClase = intent.getIntExtra("idClase", 1);
        idCurso = intent.getIntExtra("idCurso", 1);

        iv_menupop = findViewById(R.id.iv_menupop);
        iv_like = findViewById(R.id.iv_like);
        iv_dislike = findViewById(R.id.iv_dislike);

        firestore = FirebaseFirestore.getInstance();

        //----guardar preguntas incorrectas-------------------------------------
        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreferences("cuestionario", MODE_PRIVATE);
        String jsonGuardado = prefs.getString("preguntas_incorrectas", null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("clases");
        docRef.whereEqualTo("idCurso", idCurso).
                whereEqualTo("idClase", idClase).
                limit(1).
                get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            clase = doc.toObject(Clase.class);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user == null) {
                                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }else {
                                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db1.collection("usuarios").document(user.getUid());

                                userRef.get().addOnSuccessListener(userSnapshot -> {
                                    if (userSnapshot.exists()) {
                                        Usuario usuario = userSnapshot.toObject(Usuario.class);
                                        if (usuario != null) {
                                            List<Clase> historial = usuario.getHistorialClases();
                                            if (historial == null) historial = new ArrayList<>();

                                            Clase claseExistente = null;
                                            for (Clase c : historial) {
                                                if (c.getIdCurso().equals(clase.getIdCurso()) && c.getIdClase().equals(clase.getIdClase())) {
                                                    claseExistente = c;
                                                    break;
                                                }
                                            }

                                            if (claseExistente != null) {
                                                historial.remove(claseExistente);
                                            }

                                            clase.setFechaAcceso(Timestamp.now());

                                            historial.add(clase);

                                            while (historial.size() > 250) {
                                                historial.remove(0);
                                            }

                                            usuario.setId(userSnapshot.getId());
                                            userRef.update("historialClases", historial)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Historial", "Historial de clases actualizado");
                                                        usuario.calcularClusterUsuario(db1,usuario,call->{
                                                            Toast.makeText(this, "Cluster actualizado", Toast.LENGTH_SHORT).show();
                                                        });
                                                    })
                                                    .addOnFailureListener(e -> Log.e("Historial", "Error al actualizar historial", e));
                                        }
                                    }
                                });
                            }


                            tvTitulo.setText(clase.getTitulo());
                            tvInfo.setText(clase.getTextos());

                            RecyclerView rvArchivos = findViewById(R.id.rv_archivos);
                            rvArchivos.setLayoutManager(new LinearLayoutManager(this));

                            List<String> archivos = clase.getArchivos();
                            if (archivos != null && !archivos.isEmpty()) {
                                AdapterArchivosClase adapterArchivos = new AdapterArchivosClase(archivos, this);
                                rvArchivos.setAdapter(adapterArchivos);
                            }

                            playerViewPortrait = findViewById(R.id.vv_videoclase);
                            player = new ExoPlayer.Builder(this).build();

                            String videoUrl = clase.getVideoUrl();
                            if (videoUrl != null && !videoUrl.isEmpty()) {
                                Uri videoUri = Uri.parse(videoUrl);
                                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                                player.setMediaItem(mediaItem);
                                player.prepare();
                                playerViewPortrait.setPlayer(player);
                            } else {
                                Toast.makeText(this, "No hay video disponible para esta clase", Toast.LENGTH_SHORT).show();
                            }

                            if (clase != null && clase.getCalificacionPorUsuario() != null) {
                                Boolean calificacionUsuario = clase.getCalificacionPorUsuario().get(user.getUid());
                                if (calificacionUsuario != null) {
                                    like = calificacionUsuario;
                                    dislike = !calificacionUsuario;

                                    iv_like.setImageResource(like ? R.drawable.liketrue_icono : R.drawable.like_icono);
                                    iv_dislike.setImageResource(dislike ? R.drawable.disliketrue_icono : R.drawable.dislike_icono);
                                }
                            }


                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                            adapterPreguntasEnVerClase = new AdapterPreguntasEnVerClase(clase.getPreguntas(),Ver_clases.this);
                            verPreguntas.setLayoutManager(layoutManager);
                            verPreguntas.setAdapter(adapterPreguntasEnVerClase);

                            cargarComentarios(idCurso,idClase);


                            btnCalificar.setOnClickListener(v -> {
                                cantidadRespuestasCorrectas = 0;

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser == null) {
                                    Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                Map<String, Object> mapaRespuestas = new HashMap<>();
                                List<Integer> respuestas = adapterPreguntasEnVerClase.getRespuestas();

                                for (int i = 0; i < clase.getPreguntas().size(); i++) {
                                    PreguntaCuestionario pregunta = clase.getPreguntas().get(i);
                                    int respuestaCorrecta = pregunta.getRespuestaCorrecta();
                                    int respuestaUsuario = respuestas.get(i);

                                    if (respuestaCorrecta == respuestaUsuario) {
                                        cantidadRespuestasCorrectas++;
                                        mapaRespuestas.put("pregunta_" + i, true);
                                    } else {
                                        mapaRespuestas.put("pregunta_" + i, false);
                                    }
                                }

                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                DocumentReference userRef = firestore.collection("usuarios").document(currentUser.getUid());

                                userRef.get().addOnSuccessListener(snapshot -> {
                                    List<Map<String, Object>> listaRespuestas = new ArrayList<>();

                                    Object rawGuardado = snapshot.get("respuestasIncorrectas");
                                    if (rawGuardado instanceof List<?>) {
                                        for (Object entrada : (List<?>) rawGuardado) {
                                            if (entrada instanceof Map) {
                                                listaRespuestas.add((Map<String, Object>) entrada);
                                            }
                                        }
                                    }

                                    boolean actualizo = false;
                                    for (Map<String, Object> intento : listaRespuestas) {
                                        Long cursoId = ((Number) intento.get("idCurso")).longValue();
                                        Long claseId = ((Number) intento.get("idClase")).longValue();

                                        if (cursoId == idCurso && claseId == idClase) {
                                            List<Map<String, Object>> intentosPrevios = new ArrayList<>();
                                            Object intentosRaw = intento.get("intentos");

                                            if (intentosRaw instanceof List<?>) {
                                                for (Object obj : (List<?>) intentosRaw) {
                                                    if (obj instanceof Map) {
                                                        intentosPrevios.add((Map<String, Object>) obj);
                                                    }
                                                }
                                            }


                                            intentosPrevios.add(new HashMap<>(mapaRespuestas));
                                            intento.put("intentos", intentosPrevios);
                                            actualizo = true;
                                            Log.d("Firebase", "Agregando intento a clase ya existente");
                                            break;
                                        }
                                    }

                                    if (!actualizo) {
                                        Map<String, Object> nueva = new HashMap<>();
                                        nueva.put("idCurso", idCurso);
                                        nueva.put("idClase", idClase);
                                        List<Map<String, Object>> intentos = new ArrayList<>();
                                        intentos.add(new HashMap<>(mapaRespuestas));
                                        nueva.put("intentos", intentos);
                                        listaRespuestas.add(nueva);
                                        Log.d("Firebase", " Nuevo set de intentos para clase");
                                    }

                                    userRef.set(new HashMap<String, Object>() {{
                                        put("respuestasIncorrectas", listaRespuestas);
                                    }}, SetOptions.merge()).addOnSuccessListener(unused -> {
                                        Toast.makeText(Ver_clases.this, " Intento guardado correctamente", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        Log.e("Firebase", " Error al guardar intento", e);
                                        Toast.makeText(Ver_clases.this, "Error al guardar intento", Toast.LENGTH_SHORT).show();
                                    });
                                });


                                int total = clase.getPreguntas().size();
                                int porcentaje = (int) (((double) cantidadRespuestasCorrectas / total) * 100);

                                if (porcentaje == 100) {
                                    DocumentReference progresoRef = FirebaseFirestore.getInstance()
                                            .collection("usuarios")
                                            .document(currentUser.getUid());

                                    progresoRef.get().addOnSuccessListener(docSnapshot -> {
                                        Map<String, Object> progreso = new HashMap<>();
                                        Map<String, Object> nuevosDatos = new HashMap<>();

                                        Object raw = docSnapshot.get("progresoCursos");
                                        if (raw instanceof Map) {
                                            progreso = (Map<String, Object>) raw;
                                        }

                                        String claveCurso = "curso_" + idCurso;
                                        List<Long> clasesCompletadas = new ArrayList<>();

                                        if (progreso.containsKey(claveCurso)) {
                                            Object listaRaw = progreso.get(claveCurso);
                                            if (listaRaw instanceof List<?>) {
                                                for (Object id : (List<?>) listaRaw) {
                                                    if (id instanceof Number) {
                                                        clasesCompletadas.add(((Number) id).longValue());
                                                    }
                                                }
                                            }
                                        }

                                        if (!clasesCompletadas.contains((long) idClase)) {
                                            clasesCompletadas.add((long) idClase);
                                            progreso.put(claveCurso, clasesCompletadas);

                                            nuevosDatos.put("progresoCursos", progreso);
                                            progresoRef.set(nuevosDatos, SetOptions.merge())
                                                    .addOnSuccessListener(aVoid -> Log.d("Progreso", "🎉 Clase completada agregada al progreso"))
                                                    .addOnFailureListener(e -> Log.e("Progreso", "❌ Error al guardar progreso", e));
                                        } else {
                                            Log.d("Progreso", "🟡 Clase ya estaba completada");
                                        }
                                    }).addOnFailureListener(e -> Log.e("Progreso", " Error al obtener progreso", e));
                                }

                                // Mostrar resultado
                                Dialog dialog = new Dialog(Ver_clases.this);
                                dialog.setContentView(R.layout.dialog_calificacionpreguntas_verclase);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.getWindow().setDimAmount(0.5f);

                                TextView mensajeGrande = dialog.findViewById(R.id.txt_mensajegrande_dialogcalificacionpreguntas);
                                TextView mensajePequeno = dialog.findViewById(R.id.txt_mensajepequeno_dialogcalificacionpreguntas);
                                TextView puntuacion = dialog.findViewById(R.id.txt_puntuacion_dialogcalificacionpreguntas);
                                Button aceptar = dialog.findViewById(R.id.btn_aceptar_dialogcalificacionpreguntas);
                                Button reintentar = dialog.findViewById(R.id.btn_reintentar_dialogcalificacionpreguntas);

                                String mensaje = MensajeCalificacion.obtenerMensaje(porcentaje);
                                mensajeGrande.setText(mensaje);
                                mensajePequeno.setText("Has completado la tarea con éxito.");
                                puntuacion.setText(cantidadRespuestasCorrectas + "/" + total);

                                aceptar.setOnClickListener(v1 -> dialog.dismiss());
                                reintentar.setOnClickListener(v1 -> verPreguntas.getAdapter().notifyDataSetChanged());

                                dialog.show();
                            });

                        }
                    }

                }).addOnFailureListener(onFailureListener -> {
                    Toast.makeText(this, "Error al obtener la clase", Toast.LENGTH_SHORT).show();
                    finish();
                });



        iv_menupop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        etcomentario.setMovementMethod(new ScrollingMovementMethod());

        etcomentario.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.et_comentario_verclase) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    v.performClick();
                }
            }
            return false;
        });

        crear_comentario.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String coment = etcomentario.getText().toString().trim();
            Timestamp fecha = Timestamp.now();

            if (user == null) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                return;
            }
            if (coment.isEmpty()) {
                Toast.makeText(this, "Comentario vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            Comentario comentario = new Comentario();
            comentario.setUsuario(user.getEmail());
            comentario.setTexto(coment);
            comentario.setFecha(fecha);
            comentario.setIdCurso(idCurso);
            comentario.setIdClase(idClase);
            comentario.setUidAutor(user.getUid());

            db.collection("comentarios")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Integer nuevaId = queryDocumentSnapshots.size() + 1;
                        comentario.setIdComentario(nuevaId);

                        db.collection("comentarios")
                                .add(comentario)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Comentario agregado", Toast.LENGTH_SHORT).show();
                                    etcomentario.setText("");
                                    cargarComentarios(idCurso, idClase);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al subir comentario", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al buscar la clase", Toast.LENGTH_SHORT).show();
                    });

            db.collection("usuarios")
                    .whereEqualTo("correo", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            String docId = doc.getId();
                            Usuario usuario = doc.toObject(Usuario.class);

                            if (usuario != null) {
                                List<Comentario> listaComentarios = usuario.getComentarios();
                                if (listaComentarios == null) {
                                    listaComentarios = new ArrayList<>();
                                    usuario.setComentarios(listaComentarios);
                                }

                                comentario.setIdComentario(listaComentarios.size() + 1);
                                listaComentarios.add(comentario);

                                PreferenciasUsuario preferenciasUsuarioTemp = usuario.getPreferenciasUsuario();
                                if (preferenciasUsuarioTemp == null) {
                                    preferenciasUsuarioTemp = new PreferenciasUsuario();
                                }
                                final PreferenciasUsuario finalPreferenciasUsuario = preferenciasUsuarioTemp;

                                List<Comentario> finalListaComentarios = listaComentarios;
                                db.collection("cursos")
                                        .whereEqualTo("idCurso", idCurso)
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(cursoQuery -> {
                                            if (!cursoQuery.isEmpty()) {
                                                Curso curso = cursoQuery.getDocuments().get(0).toObject(Curso.class);

                                                if (curso != null) {
                                                    if (curso.getInstrumento() != null && !curso.getInstrumento().isEmpty()) {
                                                        Map<String, Integer> instrumentoMap = curso.getInstrumento();
                                                        int instrumentoId = instrumentoMap.values().iterator().next(); // ✅ obtiene el único valor
                                                        String instrumentoStr = obtenerClavePorValor(DataClusterList.listaInstrumentos, instrumentoId);
                                                        if (instrumentoStr != null) {
                                                            finalPreferenciasUsuario.incrementarInstrumento(instrumentoStr);
                                                        }
                                                    }
                                                    if (curso.getDificultad() != null && !curso.getDificultad().isEmpty()) {
                                                        Map<String, Integer> dificultadMap = curso.getDificultad();
                                                        int dificultadId = dificultadMap.values().iterator().next(); // ✅ obtiene el único valor
                                                        String dificultadStr = obtenerClavePorValor(DataClusterList.listaDificultad, dificultadId);
                                                        if (dificultadStr != null) {
                                                            finalPreferenciasUsuario.incrementarDificultad(dificultadStr);
                                                        }
                                                    }
                                                    if (curso.getGenero() != null && !curso.getGenero().isEmpty()) {
                                                        Map<String, Integer> generoMap = curso.getGenero();
                                                        int generoId = generoMap.values().iterator().next(); // ✅ obtiene el único valor
                                                        String generoStr = obtenerClavePorValor(DataClusterList.listaGenero, generoId);
                                                        if (generoStr != null) {
                                                            finalPreferenciasUsuario.incrementarGenero(generoStr);
                                                        }
                                                    }

                                                    usuario.setPreferenciasUsuario(finalPreferenciasUsuario);

                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put("comentarios", finalListaComentarios);
                                                    userMap.put("preferenciasUsuario", finalPreferenciasUsuario);

                                                    db.collection("usuarios")
                                                            .document(docId)
                                                            .set(userMap, SetOptions.merge())
                                                            .addOnSuccessListener(aVoid ->
                                                                    Log.d("Usuario", "Preferencias y comentario actualizados correctamente"))
                                                            .addOnFailureListener(e ->
                                                                    Log.e("Usuario", "Error al actualizar usuario", e));
                                                }
                                            }
                                        });
                            }
                        }
                    });
        });


        iv_like.setOnClickListener(v -> {
            boolean estabaLike = like;
            boolean estabaDislike = dislike;

            if (!estabaLike) {
                like = true;
                dislike = false;
                iv_like.setImageResource(R.drawable.liketrue_icono);
                iv_dislike.setImageResource(R.drawable.dislike_icono);
                modificarmegusta(true, estabaLike, estabaDislike);  // Activar like
            } else {
                like = false;
                iv_like.setImageResource(R.drawable.like_icono);
                modificarmegusta(false, estabaLike, estabaDislike); // Quitar like
            }
        });


        iv_dislike.setOnClickListener(v -> {
            boolean estabaLike = like;
            boolean estabaDislike = dislike;

            if (!estabaDislike) {
                dislike = true;
                like = false;
                iv_dislike.setImageResource(R.drawable.disliketrue_icono);
                iv_like.setImageResource(R.drawable.like_icono);
                modificarnogusta(true, estabaLike, estabaDislike);  // Activar dislike
            } else {
                dislike = false;
                iv_dislike.setImageResource(R.drawable.dislike_icono);
                modificarnogusta(false, estabaLike, estabaDislike); // Quitar dislike
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }

        guardarTiempoClaseSiSuperaUmbral();
    }

    public void abrirArchivoEnWebView(String url) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.archivo_webview);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        WebView webView = dialog.findViewById(R.id.webview_archivo);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);


        String viewer = "https://docs.google.com/gview?embedded=true&url=" +
                url.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "");

        webView.loadUrl(viewer);

        Button btnCerrar = dialog.findViewById(R.id.btn_cerrar_archivo);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void cargarComentarios(int idCurso, int idClase) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("comentarios")
                .whereEqualTo("idCurso",idCurso)
                .whereEqualTo("idClase", idClase)
                .get()
                .addOnSuccessListener(onSuccessListener -> {
                    if (!onSuccessListener.isEmpty()) {
                        List<Comentario> comentarios = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : onSuccessListener) {
                            Comentario comentario = doc.toObject(Comentario.class);
                            comentarios.add(comentario);
                        }

                        verComentarios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        AdapterVerClaseVerComentarios adapter = new AdapterVerClaseVerComentarios(comentarios, idCurso, idClase);
                        verComentarios.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

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

    private void guardarTiempoClaseSiSuperaUmbral() {
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
                        put("tiempoPorClase", tiempoPorClase);
                    }}, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TiempoClase", " Tiempo registrado correctamente: " + tiempoAcumuladoClase + "s"))
                    .addOnFailureListener(e -> Log.e("TiempoClase", " Error al registrar tiempo", e));

            userRef.get().addOnSuccessListener(docSnapshot -> {
                Map<String, Object> progreso = new HashMap<>();
                Map<String, Object> nuevosDatos = new HashMap<>();

                Object raw = docSnapshot.get("progresoCursos");
                if (raw instanceof Map) {
                    progreso = (Map<String, Object>) raw;
                }

                String claveCurso = "curso_" + idCurso;
                List<Long> clasesCompletadas = new ArrayList<>();

                if (progreso.containsKey(claveCurso)) {
                    Object listaRaw = progreso.get(claveCurso);
                    if (listaRaw instanceof List<?>) {
                        for (Object id : (List<?>) listaRaw) {
                            if (id instanceof Number) {
                                clasesCompletadas.add(((Number) id).longValue());
                            }
                        }
                    }
                }

                if (!clasesCompletadas.contains((long) idClase)) {
                    clasesCompletadas.add((long) idClase);
                    progreso.put(claveCurso, clasesCompletadas);
                    nuevosDatos.put("progresoCursos", progreso);

                    userRef.set(nuevosDatos, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("Progreso", " Clase marcada como vista por tiempo"))
                            .addOnFailureListener(e -> Log.e("Progreso", " Error al marcar progreso", e));
                } else {
                    Log.d("Progreso", " Ya estaba marcada como vista");
                }
            });
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_cursos, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.it_denunciar) {
                Intent denuncia = new Intent(Ver_clases.this, CrearDenuncia.class);
                denuncia.putExtra("idCurso", idCurso);
                denuncia.putExtra("idClase", idClase);
                startActivity(denuncia);
                return true;
            } else if (id == R.id.it_descargar) {
                if (clase == null) {
                    Toast.makeText(this, "Clase no cargada aún", Toast.LENGTH_SHORT).show();
                    return true;
                }

                ClaseFirebase claseOffline = new ClaseFirebase(
                        clase.getTitulo(),
                        clase.getArchivos(),
                        clase.getImagen(),
                        clase.getVideoUrl()
                );
                claseOffline.setPreguntas(clase.getPreguntas());

                int idCursoActual = idCurso;

                if (!DescargaManager.cursoYaDescargado(idCursoActual, Ver_clases.this)) {
                    Toast.makeText(this, "Primero descarga el curso desde el menú del curso", Toast.LENGTH_SHORT).show();
                    return true;
                }

                DbHelper dbHelper = new DbHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                int idCursoLocal = -1;
                try (Cursor cursor = db.rawQuery("SELECT id FROM cursodescargado WHERE idCurso = ?", new String[]{String.valueOf(idCursoActual)})) {
                    if (cursor.moveToFirst()) {
                        idCursoLocal = cursor.getInt(0);
                    }
                }
                db.close();

                if (idCursoLocal == -1) {
                    Toast.makeText(this, "Error: Curso no está en base local", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (DescargaManager.claseYaDescargada(this, clase.getTitulo(), idCursoLocal)) {
                    Toast.makeText(this, "Esta clase ya fue descargada", Toast.LENGTH_SHORT).show();
                    return true;
                }

                final int finalIdCursoLocal = idCursoLocal;

                new Thread(() -> {
                    DbHelper dbHelperLocal = new DbHelper(Ver_clases.this);

                    int totalElementos = claseOffline.getArchivosUrl().size() + 2; // +2 por video e imagen
                    int descargados = 0;

                    NotificacionHelper.mostrarProgreso(
                            Ver_clases.this, 40,
                            "Descargando clase",
                            claseOffline.getTitulo(),
                            0
                    );

                    List<String> archivosDescargados = new ArrayList<>();
                    int i = 1;
                    for (String url : claseOffline.getArchivosUrl()) {
                        if (url != null && !url.isEmpty()) {
                            String nombre = "archivo_" + claseOffline.getTitulo().replaceAll("[^a-zA-Z0-9]", "_") + "_" + i + ".pdf";
                            String ruta = DescargaManager.descargarArchivo(Ver_clases.this, url, nombre);
                            if (ruta != null) archivosDescargados.add(ruta);
                            descargados++;
                            int progreso = (descargados * 100) / totalElementos;
                            NotificacionHelper.mostrarProgreso(Ver_clases.this, 40, "Descargando clase", claseOffline.getTitulo(), progreso);
                            i++;
                        }
                    }

                    String videoLocal = DescargaManager.descargarArchivo(
                            Ver_clases.this,
                            claseOffline.getVideoUrl(),
                            "video_" + claseOffline.getTitulo().replaceAll("[^a-zA-Z0-9]", "_") + ".mp4"
                    );
                    descargados++;
                    NotificacionHelper.mostrarProgreso(Ver_clases.this, 40, "Descargando clase", claseOffline.getTitulo(), (descargados * 100) / totalElementos);

                    String imgLocal = DescargaManager.descargarArchivo(
                            Ver_clases.this,
                            claseOffline.getImagenUrl(),
                            "img_" + claseOffline.getTitulo().replaceAll("[^a-zA-Z0-9]", "_") + ".jpg"
                    );
                    descargados++;
                    NotificacionHelper.mostrarProgreso(Ver_clases.this, 40, "Descargando clase", claseOffline.getTitulo(), (descargados * 100) / totalElementos);

                    claseOffline.setVideoUrl(videoLocal);
                    claseOffline.setImagenUrl(imgLocal);
                    claseOffline.setArchivosUrl(archivosDescargados);

                    dbHelperLocal.guardarClaseDescargada(claseOffline, finalIdCursoLocal);

                    NotificacionHelper.completarProgreso(
                            Ver_clases.this,
                            40,
                            "Clase descargada",
                            claseOffline.getTitulo() + " se descargó correctamente"
                    );

                    runOnUiThread(() -> Toast.makeText(Ver_clases.this, "Clase descargada correctamente", Toast.LENGTH_SHORT).show());
                }).start();

                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void modificarmegusta(boolean activado, boolean antesLike, boolean antesDislike) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión para calificar", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clases")
                .whereEqualTo("idCurso", idCurso)
                .whereEqualTo("idClase", idClase)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        String docId = doc.getId();
                        Long meGusta = doc.getLong("meGusta");
                        Long noGusta = doc.getLong("noGusta");

                        int likeCount = (meGusta != null ? meGusta.intValue() : 0);
                        int dislikeCount = (noGusta != null ? noGusta.intValue() : 0);

                        Map<String, Object> updates = new HashMap<>();

                        if (activado) {
                            likeCount += 1;
                            if (antesDislike) dislikeCount = Math.max(0, dislikeCount - 1);
                            actualizarPromedioCurso(antesDislike ? 0.20 : 0.10);
                            updates.put("calificacionPorUsuario." + user.getUid(), true);
                        } else {
                            likeCount = Math.max(0, likeCount - 1);
                            actualizarPromedioCurso(-0.10);
                            updates.put("calificacionPorUsuario." + user.getUid(), null);
                        }

                        updates.put("meGusta", likeCount);
                        updates.put("noGusta", dislikeCount);

                        db.collection("clases").document(docId).update(updates);

                        db.collection("cursos").document(String.valueOf(idCurso))
                                .get().addOnSuccessListener(cursoDoc -> {
                                    if (cursoDoc.exists()) {
                                        Map<String, Integer> instrumento = (Map<String, Integer>) cursoDoc.get("instrumento");
                                        Map<String, Integer> genero = (Map<String, Integer>) cursoDoc.get("genero");
                                        Map<String, Integer> dificultad = (Map<String, Integer>) cursoDoc.get("dificultad");

                                        modificarPreferenciasUsuario(user.getUid(), instrumento, genero, dificultad, activado);
                                    }
                                });
                    }
                });
    }


    private void modificarnogusta(boolean activado, boolean antesLike, boolean antesDislike) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión para calificar", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clases")
                .whereEqualTo("idCurso", idCurso)
                .whereEqualTo("idClase", idClase)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        String docId = doc.getId();
                        Long meGusta = doc.getLong("meGusta");
                        Long noGusta = doc.getLong("noGusta");

                        int likeCount = (meGusta != null ? meGusta.intValue() : 0);
                        int dislikeCount = (noGusta != null ? noGusta.intValue() : 0);

                        Map<String, Object> updates = new HashMap<>();

                        if (activado) {
                            dislikeCount += 1;
                            if (antesLike) likeCount = Math.max(0, likeCount - 1);
                            actualizarPromedioCurso(antesLike ? -0.20 : -0.10);
                            updates.put("calificacionPorUsuario." + user.getUid(), false);
                        } else {
                            dislikeCount = Math.max(0, dislikeCount - 1);
                            actualizarPromedioCurso(0.10);
                            updates.put("calificacionPorUsuario." + user.getUid(), null);
                        }

                        updates.put("meGusta", likeCount);
                        updates.put("noGusta", dislikeCount);

                        db.collection("clases").document(docId).update(updates);

                        // Actualizar preferencias del usuario
                        db.collection("cursos").document(String.valueOf(idCurso))
                                .get().addOnSuccessListener(cursoDoc -> {
                                    if (cursoDoc.exists()) {
                                        Map<String, Integer> instrumento = (Map<String, Integer>) cursoDoc.get("instrumento");
                                        Map<String, Integer> genero = (Map<String, Integer>) cursoDoc.get("genero");
                                        Map<String, Integer> dificultad = (Map<String, Integer>) cursoDoc.get("dificultad");

                                        modificarPreferenciasUsuario(user.getUid(), instrumento, genero, dificultad, !activado);
                                    }
                                });
                    }
                });
    }


    public static String obtenerClavePorValor(ArrayList<Map<String, Integer>> lista, int valor) {
        for (Map<String, Integer> map : lista) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == valor) return entry.getKey();
            }
        }
        return null;
    }

    private void actualizarPromedioCurso(double cambio) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cursos")
                .whereEqualTo("idCurso", clase.getIdCurso())
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        Double actual = doc.getDouble("popularidad");
                        double nuevo = (actual != null ? actual : 0.0) + cambio;

                        doc.getReference().update("popularidad", nuevo)
                                .addOnSuccessListener(aVoid -> Log.d("Curso", "Popularidad actualizada: " + nuevo))
                                .addOnFailureListener(e -> Log.e("Curso", "Fallo al actualizar popularidad", e));
                    } else {
                        Log.e("Curso", "Curso no encontrado con idCurso = " + clase.getIdCurso());
                        Toast.makeText(this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("Curso", "Error en búsqueda de curso", e));
    }

    private void modificarPreferenciasUsuario(String userId, Map<String, Integer> instrumento, Map<String, Integer> genero, Map<String, Integer> dificultad, boolean incremento) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            Map<String, Object> rawPrefs = (Map<String, Object>) snapshot.get("preferenciasUsuario");
            PreferenciasUsuario preferencias = new PreferenciasUsuario();

            if (rawPrefs != null) {
                Map<String, Long> instrumentosRaw = (Map<String, Long>) rawPrefs.get("instrumentos");
                Map<String, Long> generosRaw = (Map<String, Long>) rawPrefs.get("generos");
                Map<String, Long> dificultadesRaw = (Map<String, Long>) rawPrefs.get("dificultades");

                if (instrumentosRaw != null) {
                    for (String key : instrumentosRaw.keySet())
                        preferencias.getInstrumentos().put(key, instrumentosRaw.get(key).intValue());
                }
                if (generosRaw != null) {
                    for (String key : generosRaw.keySet())
                        preferencias.getGeneros().put(key, generosRaw.get(key).intValue());
                }
                if (dificultadesRaw != null) {
                    for (String key : dificultadesRaw.keySet())
                        preferencias.getDificultades().put(key, dificultadesRaw.get(key).intValue());
                }
            }

            instrumento.keySet().forEach(instr -> {
                if (incremento) preferencias.incrementarInstrumento(instr);
                else preferencias.decrementarInstrumento(instr);
            });
            genero.keySet().forEach(gen -> {
                if (incremento) preferencias.incrementarGenero(gen);
                else preferencias.decrementarGenero(gen);
            });
            dificultad.keySet().forEach(diff -> {
                if (incremento) preferencias.incrementarDificultad(diff);
                else preferencias.decrementarDificultad(diff);
            });

            userRef.update("preferenciasUsuario", preferencias);
        });
    }
}