package com.example.skulfulharmony;

import android.net.Uri;
import android.os.Bundle;
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

import com.dropbox.core.v2.clouddocs.UserInfo;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.google.protobuf.Empty;

import java.sql.Timestamp;
import java.util.Date;
public class Ver_clases extends AppCompatActivity {
    private TextView tvPuntuacion;
    private EditText etcomentario;
    private PlayerView playerViewPortrait;
    private ImageView[] estrellas;
    private  ImageView btnPlayPause,btnRewind,btnForward;
    private ExoPlayer player;
    private Button crear_comentario;
    private int puntuacionActual = 0;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verclases);
        tvPuntuacion = findViewById(R.id.tv_puntuacion);
        LinearLayout layoutEstrellas = findViewById(R.id.ll_estrellas);
        //-----------------------------------------
        crear_comentario=findViewById(R.id.btn_subir_comentario);


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
        btnPlayPause = findViewById(R.id.btn_play);
        btnRewind = findViewById(R.id.btn_adelantar);
        btnForward = findViewById(R.id.btn_retroceder);

        player = new ExoPlayer.Builder(this).build();

        // obtener de firebase orita esta igual que el ejemplo

        Uri videoUri = Uri.parse("https://dl.dropboxusercontent.com/scl/fi/cl4w3odb9jyiysutjlcps/videoprueba_optimizado.mp4?rlkey=vtwcayh9cktynbu16sjlzw0hc&st=n8c0m2wk");
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();

        playerViewPortrait.setPlayer(player);

        // Reproducir o pausar
        btnPlayPause.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                btnPlayPause.setImageResource(R.drawable.jugar); // Cambiar imagen a "Play"
            } else {
                player.play();
                btnPlayPause.setImageResource(R.drawable.pausa); // Cambiar imagen a "Pause"
            }
        });
        btnForward.setOnClickListener(v -> {
            long currentPosition = player.getCurrentPosition();
            long duration = player.getDuration();
            if (currentPosition + 5000 < duration) {
                player.seekTo(currentPosition + 5000);
            } else {
                player.seekTo(duration);
            }
        });

        // Retroceder 5 segundos
        btnRewind.setOnClickListener(v -> {
                    long currentPosition = player.getCurrentPosition();
                    if (currentPosition - 5000 > 0) {
                        player.seekTo(currentPosition - 5000);
                    } else {
                        player.seekTo(0);
                    }
                }
        );
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