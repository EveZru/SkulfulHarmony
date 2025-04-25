package com.example.skulfulharmony;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class videos extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerViewPortrait;
    private ImageButton btnPlayPause, btnRewind, btnForward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        // Vincular vistas
        playerViewPortrait = findViewById(R.id.playerViewPortrait);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);

        // Crear reproductor
        player = new ExoPlayer.Builder(this).build();
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

        // Adelantar 5 segundos
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
    }
}