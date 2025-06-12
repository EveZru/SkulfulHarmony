package com.example.skulfulharmony;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.io.File;

public class VerClaseOffline extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private ImageView imgClase;
    private TextView tvTitulo, tvTexto, tvSinArchivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clase_offline);

        playerView = findViewById(R.id.exoplayer_clase_offline);
        imgClase = findViewById(R.id.img_clase_offline);
        tvTitulo = findViewById(R.id.tv_titulo_clase_offline);
        tvTexto = findViewById(R.id.tv_texto_clase_offline);
        tvSinArchivo = findViewById(R.id.tv_no_disponible);

        // Obtener datos desde Intent
        String titulo = getIntent().getStringExtra("titulo");
        String imagenUrl = getIntent().getStringExtra("imagen");
        String videoUrl = getIntent().getStringExtra("video");
        String documentoUrl = getIntent().getStringExtra("documento");

        tvTitulo.setText(titulo != null ? titulo : "Clase sin título");

        // Cargar imagen
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            File imgFile = new File(imagenUrl);
            if (imgFile.exists()) {
                Glide.with(this).load(imgFile).into(imgClase);
            }
        }

        // Reproducir video
        if (videoUrl != null && !videoUrl.isEmpty()) {
            File videoFile = new File(videoUrl);
            if (videoFile.exists()) {
                player = new ExoPlayer.Builder(this).build();
                playerView.setPlayer(player);
                MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile));
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
            }
        }

        // Mostrar archivo
        if (documentoUrl != null && !documentoUrl.isEmpty()) {
            File doc = new File(documentoUrl);
            if (doc.exists()) {
                tvTexto.setText("Archivo disponible: " + doc.getName());
            } else {
                tvSinArchivo.setText("Documento no disponible offline.");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }
}