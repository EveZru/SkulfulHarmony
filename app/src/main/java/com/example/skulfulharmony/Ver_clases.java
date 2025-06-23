package com.example.skulfulharmony;

import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.RequestBuilder.put;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.skulfulharmony.adapters.AdapterArchivosClase;
import com.google.firebase.firestore.DocumentReference;

import com.dropbox.core.v2.clouddocs.UserInfo;
import com.example.skulfulharmony.adapters.AdapterPreguntasEnVerClase;
import com.example.skulfulharmony.adapters.AdapterVerClaseVerComentarios;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Empty;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ver_clases extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvPuntuacion, tvInfo, tvTitulo;
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
            return "¡Ups!"; // Fallback por si algo sale mal
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verclases);
        //  tvPuntuacion = findViewById(R.id.tv_puntuacion);
        tvInfo = findViewById(R.id.tv_info_verclase);
        tvTitulo = findViewById(R.id.verclase_vertitulo);
        etcomentario = findViewById(R.id.et_comentario_verclase);
       // LinearLayout layoutEstrellas = findViewById(R.id.ll_estrellas);
        verPreguntas = findViewById(R.id.rv_preguntasporclase_verclase);
        btnCalificar = findViewById(R.id.btt_revisar_respuestas_verclase);
        //-----------------------------------------
        crear_comentario=findViewById(R.id.btn_subir_comentario_clase);
        verComentarios = findViewById(R.id.rv_comentarios_verclase);
        toolbar = findViewById(R.id.toolbartituloclase);
        Intent intent = getIntent();
        idClase = intent.getIntExtra("idClase", 1);
        idCurso = intent.getIntExtra("idCurso", 1);

        //_____________-
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

                                            // Verificamos si ya existe una clase con mismo idCurso e idClase
                                            Clase claseExistente = null;
                                            for (Clase c : historial) {
                                                if (c.getIdCurso().equals(clase.getIdCurso()) && c.getIdClase().equals(clase.getIdClase())) {
                                                    claseExistente = c;
                                                    break;
                                                }
                                            }

                                            // Si existe, la removemos para agregarla al final (actualizada)
                                            if (claseExistente != null) {
                                                historial.remove(claseExistente);
                                            }

                                            clase.setFechaAcceso(Timestamp.now());
                                            // Agregamos al final
                                            historial.add(clase);

                                            // Si hay más de 250, eliminamos los más antiguos
                                            while (historial.size() > 250) {
                                                historial.remove(0);
                                            }

                                            userRef.update("historialClases", historial)
                                                    .addOnSuccessListener(aVoid -> Log.d("Historial", "Historial de clases actualizado"))
                                                    .addOnFailureListener(e -> Log.e("Historial", "Error al actualizar historial", e));
                                        }
                                    }
                                });
                            }


                            tvTitulo.setText(clase.getTitulo());
                            tvInfo.setText(clase.getTextos());

                            // Mostrar archivos si existen
                            RecyclerView rvArchivos = findViewById(R.id.rv_archivos);
                            rvArchivos.setLayoutManager(new LinearLayoutManager(this));

                            List<String> archivos = clase.getArchivos(); // Asegúrate que este campo exista en tu modelo Clase
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


                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                            adapterPreguntasEnVerClase = new AdapterPreguntasEnVerClase(clase.getPreguntas(),Ver_clases.this);
                            verPreguntas.setLayoutManager(layoutManager);
                            verPreguntas.setAdapter(adapterPreguntasEnVerClase);

                            cargarComentarios(idCurso,idClase);

                            //Calificar preguntas

                            btnCalificar.setOnClickListener(v -> {
                                cantidadRespuestasCorrectas = 0;
                               for(PreguntaCuestionario pregunta : clase.getPreguntas()){
                                   if(pregunta.getRespuestaCorrecta().equals(adapterPreguntasEnVerClase.getRespuestas().get(clase.getPreguntas().indexOf(pregunta)))) {

                                       cantidadRespuestasCorrectas++;
                                   }
                                   //List<PreguntaCuestionario> preguntasGuardadas;
                                   List<PreguntaCuestionario> preguntasIncorrectasDeEstaClase = new ArrayList<>();
                                   if (jsonGuardado != null) {
                                       Type listType = new TypeToken<List<PreguntaCuestionario>>() {}.getType();
                                       try{
                                           preguntasIncorrectasDeEstaClase = gson.fromJson(jsonGuardado, listType);
                                       }catch (Exception e) {
                                           Log.e(TAG, "Error al cargar preguntas incorrectas: " + e.getMessage());
                                           preguntasIncorrectasDeEstaClase = new ArrayList<>();
                                       }

                                       //  preguntasGuardadas = gson.fromJson(jsonGuardado, new com.google.gson.reflect.TypeToken<List<String>>(){}.getType());
                                   } else {
                                       preguntasIncorrectasDeEstaClase=new ArrayList<>();
                                   }
                                   for (PreguntaCuestionario  preguntaIndividual : clase.getPreguntas()) {// aqui marca error en pregunta
                                       if(!preguntasIncorrectasDeEstaClase.contains(pregunta)){
                                           preguntasIncorrectasDeEstaClase.add(pregunta);
                                       }
                                   }
                                   // Guardar la lista actualizada
                                   String jsonNuevo = gson.toJson(preguntasIncorrectasDeEstaClase);
                                   prefs.edit().putString("preguntas_incorrectas", jsonNuevo).apply();
                               }

                                Dialog dialog = new Dialog(Ver_clases.this);
                                dialog.setContentView(R.layout.dialog_calificacionpreguntas_verclase);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.getWindow().setDimAmount(0.5f);

                                TextView mensajeGrande = dialog.findViewById(R.id.txt_mensajegrande_dialogcalificacionpreguntas);
                                TextView mensajePequeno = dialog.findViewById(R.id.txt_mensajepequeno_dialogcalificacionpreguntas);
                                TextView puntuacion = dialog.findViewById(R.id.txt_puntuacion_dialogcalificacionpreguntas);
                                Button aceptar = dialog.findViewById(R.id.btn_aceptar_dialogcalificacionpreguntas);
                                Button reintentar = dialog.findViewById(R.id.btn_reintentar_dialogcalificacionpreguntas);

                                int total = clase.getPreguntas().size();
                                int porcentaje = (int) (((double) cantidadRespuestasCorrectas / total) * 100);
                                String mensaje = MensajeCalificacion.obtenerMensaje(porcentaje);

                                mensajeGrande.setText(mensaje);
                                mensajePequeno.setText("Has completado la tarea con éxito.");
                                puntuacion.setText(cantidadRespuestasCorrectas + "/" + total);

                                //Aquí añadir la logica para guardar la calificacion del usuario en el algoritmo de mejora
                                // aqui se guardan las preguntas incorrectas

                                aceptar.setOnClickListener(v1 -> {
                                    dialog.dismiss();
                                });

                                reintentar.setOnClickListener(v1 -> {
                                    verPreguntas.getAdapter().notifyDataSetChanged();
                                });

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
            comentario.setIdClase(idClase); // Asegúrate que no sea null aquí si es clase
            comentario.setUidAutor(user.getUid());

            db.collection("clases")
                    .whereEqualTo("idCurso", idCurso)
                    .whereEqualTo("idClase", idClase)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            String docId = doc.getId();
                            Clase clase = doc.toObject(Clase.class);
                            List<Comentario> comentarios = clase.getComentarios();
                            if (comentarios == null) comentarios = new ArrayList<>();
                            Integer nuevaId = comentarios.size() + 1;
                            comentario.setIdComentario(nuevaId);
                            comentarios.add(comentario);

                            db.collection("clases")
                                    .document(docId)
                                    .update("comentarios", comentarios)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Comentario agregado", Toast.LENGTH_SHORT).show();
                                        etcomentario.setText("");
                                        cargarComentarios(idCurso, idClase);

                                        // ✅ También subimos el doc a la colección 'comentarios' para activar notificaciones después
                                        db.collection("comentarios")
                                                .document(String.valueOf(nuevaId))
                                                .set(new HashMap<String, Object>() {{
                                                    put("autorId", user.getUid());
                                                    put("texto", coment);
                                                    put("likes", 0); // desde el inicio
                                                    put("idClase", idClase);
                                                    put("idCurso", idCurso);
                                                    put("timestamp", fecha);
                                                }})
                                                .addOnSuccessListener(aVoid -> Log.d("CrearComentario", "Comentario raíz creado"))
                                                .addOnFailureListener(e -> Log.e("CrearComentario", "Error al crear en raíz", e));
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al subir comentario", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Toast.makeText(this, "Clase no encontrada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al buscar la clase", Toast.LENGTH_SHORT).show();
                    });
        });

        iv_like.setOnClickListener(v -> {
            if (!like) {
                like = true;
                dislike = false;
                iv_like.setImageResource(R.drawable.liketrue_icono);
                iv_dislike.setImageResource(R.drawable.dislike_icono);
                modificarcalificacionmas(); // like nuevo
            } else {
                like = false;
                iv_like.setImageResource(R.drawable.like_icono);
                modificarcalificacionmenos(); // quitando like
            }
        });


        iv_dislike.setOnClickListener(v -> {
            if (!dislike) {
                dislike = true;
                like = false;
                iv_dislike.setImageResource(R.drawable.disliketrue_icono);
                iv_like.setImageResource(R.drawable.like_icono);

                modificarcalificacionmenos(); // nuevo dislike
            } else {
                dislike = false;

                iv_dislike.setImageResource(R.drawable.dislike_icono);
                modificarcalificacionmas(); // quitando dislike
            }
        });


    }

    private void cargarComentarios(int idCurso, int idClase) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("clases")
                .whereEqualTo("idCurso",idCurso)
                .whereEqualTo("idClase", idClase)
                .limit(1)
                .get()
                .addOnSuccessListener(onSuccessListener -> {
                    if (!onSuccessListener.isEmpty()) {
                        DocumentSnapshot doc = onSuccessListener.getDocuments().get(0);
                        String docId = doc.getId();
                        Clase clase = doc.toObject(Clase.class);
                        if (clase != null) {
                            if(clase.getComentarios()==null){
                                clase.setComentarios(new ArrayList<>());
                            }
                            verComentarios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                            AdapterVerClaseVerComentarios adapter = new AdapterVerClaseVerComentarios(clase.getComentarios(), idCurso, idClase);
                            verComentarios.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

    }

    // para la reprocuccion de video
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
    }

    public void abrirArchivoEnWebView(String url) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.archivo_webview);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        WebView webView = dialog.findViewById(R.id.webview_archivo);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        // Asegúrate que el link es tipo raw directo (no dropbox.com sino dl.dropboxusercontent.com)
        String viewer = "https://docs.google.com/gview?embedded=true&url=" +
                url.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "");

        webView.loadUrl(viewer);

        Button btnCerrar = dialog.findViewById(R.id.btn_cerrar_archivo);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPopupMenu(View view) {
        // Crear el PopupMenu y asociarlo con la vista 'view' (la ImageView desplegarmenu)
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_cursos, popupMenu.getMenu()); // Usamos el mismo menú que para la ActionBar

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.it_denunciar) {
                Intent denuncia = new Intent(Ver_clases.this, CrearDenuncia.class);
                denuncia.putExtra("idClase", idClase);
                startActivity(denuncia);
                return true;
            } else if (id == R.id.it_descargar) {
                // descargar clase individual
                return true;
            } else if (id == R.id.it_compartir) {
                Toast.makeText(this, "se supone que se comparte ", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false; // Importante devolver false si no se manejó el clic
        });
        popupMenu.show();
    }
    private void modificarcalificacionmas() {


    }
    private void modificarcalificacionmenos() {

    }
}