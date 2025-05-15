package com.example.skulfulharmony;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.v2.clouddocs.UserInfo;
import com.example.skulfulharmony.adapters.AdapterPreguntasEnVerClase;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Ver_clases extends AppCompatActivity {
    private TextView tvPuntuacion, tvInfo, tvTitulo;
    private Button btnCalificar;
    private EditText etcomentario;
    private PlayerView playerViewPortrait;
    private ImageView[] estrellas;
    //private  ImageView btnPlayPause,btnRewind,btnForward;
    private ExoPlayer player;
    private Button crear_comentario;
    private int puntuacionActual = 0;
    private UserInfo user;
    private int idClase;
    private int idCurso;
    private Clase clase;
    private RecyclerView verPreguntas;
    private AdapterPreguntasEnVerClase adapterPreguntasEnVerClase;
    private int cantidadRespuestasCorrectas;

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
        tvPuntuacion = findViewById(R.id.tv_puntuacion);
        tvInfo = findViewById(R.id.tv_info_verclase);
        tvTitulo = findViewById(R.id.verclase_vertitulo);
        etcomentario = findViewById(R.id.et_comentario_verclase);
        LinearLayout layoutEstrellas = findViewById(R.id.ll_estrellas);
        verPreguntas = findViewById(R.id.rv_preguntasporclase_verclase);
        btnCalificar = findViewById(R.id.btt_revisar_respuestas_verclase);
        //-----------------------------------------
        crear_comentario=findViewById(R.id.btn_subir_comentario);

        Intent intent = getIntent();
        idClase = intent.getIntExtra("idClase", 1);
        idCurso = intent.getIntExtra("idCurso", 1);

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

                            //Aqui la logica para el historial de clase

                            tvTitulo.setText(clase.getTitulo());
                            tvInfo.setText(clase.getTextos());

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                            adapterPreguntasEnVerClase = new AdapterPreguntasEnVerClase(clase.getPreguntas(),Ver_clases.this);
                            verPreguntas.setLayoutManager(layoutManager);
                            verPreguntas.setAdapter(adapterPreguntasEnVerClase);

                            btnCalificar.setOnClickListener(v -> {
                                cantidadRespuestasCorrectas = 0;
                               for(PreguntaCuestionario pregunta : clase.getPreguntas()){
                                   if(
                                           pregunta
                                                   .getRespuestaCorrecta()
                                                    .equals(
                                                            adapterPreguntasEnVerClase.
                                                                getRespuestas()
                                                                .get(
                                                                       clase.getPreguntas()
                                                                            .indexOf(pregunta)
                                                                )
                                                    )
                                   ) {
                                       cantidadRespuestasCorrectas++;
                                   }
                               }

                                Dialog dialog = new Dialog(Ver_clases.this);
                                dialog.setContentView(R.layout.dialog_calificacionpreguntas_verclase);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.getWindow().setDimAmount(0.5f);

                                TextView mensajeGrande = dialog.findViewById(R.id.txt_mensajegrande_dialogcalificacionpreguntas);
                                TextView mensajePequeno = dialog.findViewById(R.id.txt_mensajepequeno_dialogcalificacionpreguntas);
                                TextView puntuacion = dialog.findViewById(R.id.txt_puntuacion_dialogcalificacionpreguntas);
                                Button aceptar = dialog.findViewById(R.id.btn_aceptar_dialogcalificacionpreguntas);

                                int total = clase.getPreguntas().size();
                                int porcentaje = (int) (((double) cantidadRespuestasCorrectas / total) * 100);
                                String mensaje = MensajeCalificacion.obtenerMensaje(porcentaje);

                                mensajeGrande.setText(mensaje);
                                mensajePequeno.setText("Has completado la tarea con éxito.");
                                puntuacion.setText(cantidadRespuestasCorrectas + "/" + total);

                                //Aquí añadir la logica para guardar la calificacion del usuario en el algoritmo de mejora

                                aceptar.setOnClickListener(v1 -> {
                                    dialog.dismiss();
                                });

                                dialog.show();
                            });

                        }
                    }

                }).addOnFailureListener(onFailureListener -> {
                    Toast.makeText(this, "Error al obtener la clase", Toast.LENGTH_SHORT).show();
                    finish();
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

        crear_comentario.setOnClickListener(v->
        {

              String coment = etcomentario.getText().toString();
              Date fecha = new Date(System.currentTimeMillis());
              Comentario comentario = new Comentario();
              comentario.setUsuario(user.getEmail());
              comentario.setTexto(coment);
              comentario.setFecha(fecha);

              Toast.makeText(this, "subiendo comentario", Toast.LENGTH_SHORT).show();


              // subir el comentario

        });



// coso de estrellas _________________________________________
        estrellas = new ImageView[5];
        estrellas[0] = findViewById(R.id.iv_1_estrella);
        estrellas[1] = findViewById(R.id.iv_2_estrella);
        estrellas[2] = findViewById(R.id.iv_3_estrella);
        estrellas[3] = findViewById(R.id.iv_4_estrella);
        estrellas[4] = findViewById(R.id.iv_5_estrella);

        // Establecer OnClickListener para cada estrella
        for (int i = 0; i < estrellas.length; i++) {
            final int estrellaIndex = i;
            estrellas[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actualizarPuntuacion(estrellaIndex + 1);
                }
            });
        }

        // Inicializar la puntuación en 0/5
        actualizarTextoPuntuacion();
        //-coso para el video-----------------------------------------------------------------
        playerViewPortrait = findViewById(R.id.vv_videoclase);
//        btnPlayPause = findViewById(R.id.btn_play);
//        btnRewind = findViewById(R.id.btn_adelantar);
//        btnForward = findViewById(R.id.btn_retroceder);

        player = new ExoPlayer.Builder(this).build();

        // obtener de firebase orita esta igual que el ejemplo

        Uri videoUri = Uri.parse("https://dl.dropboxusercontent.com/scl/fi/cl4w3odb9jyiysutjlcps/videoprueba_optimizado.mp4?rlkey=vtwcayh9cktynbu16sjlzw0hc&st=n8c0m2wk");
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();

        playerViewPortrait.setPlayer(player);

        // Reproducir o pausar
//        btnPlayPause.setOnClickListener(v -> {
//            if (player.isPlaying()) {
//                player.pause();
//                btnPlayPause.setImageResource(R.drawable.jugar); // Cambiar imagen a "Play"
//            } else {
//                player.play();
//                btnPlayPause.setImageResource(R.drawable.pausa); // Cambiar imagen a "Pause"
//            }
//        });
//        btnForward.setOnClickListener(v -> {
//            long currentPosition = player.getCurrentPosition();
//            long duration = player.getDuration();
//            if (currentPosition + 5000 < duration) {
//                player.seekTo(currentPosition + 5000);
//            } else {
//                player.seekTo(duration);
//            }
//        });
//
//        // Retroceder 5 segundos
//        btnRewind.setOnClickListener(v -> {
//                    long currentPosition = player.getCurrentPosition();
//                    if (currentPosition - 5000 > 0) {
//                        player.seekTo(currentPosition - 5000);
//                    } else {
//                        player.seekTo(0);
//                    }
//                }
//        );


    }



// calificacion met odos extra

    private void actualizarPuntuacion(int nuevaPuntuacion) {
        if (nuevaPuntuacion >= 0 && nuevaPuntuacion <= 5) {
            puntuacionActual = nuevaPuntuacion;
            actualizarTextoPuntuacion();
            actualizarImagenesEstrellas();
        }
    }

    private void actualizarTextoPuntuacion() {
        tvPuntuacion.setText(puntuacionActual + " / 5");
    }

    private void actualizarImagenesEstrellas() {
        for (int i = 0; i < estrellas.length; i++) {
            if (i < puntuacionActual) {
                estrellas[i].setImageResource(R.drawable.estella_llena);
            } else {
                estrellas[i].setImageResource(R.drawable.estrella);
            }
        }
    }
    private void obtenerFirebase (){

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

}