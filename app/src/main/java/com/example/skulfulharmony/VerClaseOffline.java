package com.example.skulfulharmony;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterArchivosOffline;
import com.bumptech.glide.Glide;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VerClaseOffline extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView tvTitulo, tvTexto, tvSinArchivo;
    private RecyclerView rvArchivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clase_offline);

        playerView = findViewById(R.id.exoplayer_clase_offline);
        tvTitulo = findViewById(R.id.tv_titulo_clase_offline);
        tvTexto = findViewById(R.id.tv_texto_clase_offline);
        tvSinArchivo = findViewById(R.id.tv_no_disponible);
        rvArchivos = findViewById(R.id.rv_archivos_offline);

        String titulo = getIntent().getStringExtra("titulo");
        String videoUrl = getIntent().getStringExtra("video");
        String documentoUrl = getIntent().getStringExtra("documento");
        ArrayList<String> archivos = getIntent().getStringArrayListExtra("archivos");

        if (titulo == null) titulo = "Clase sin título";
        if (videoUrl == null) videoUrl = "";
        if (documentoUrl == null) documentoUrl = "";
        if (archivos == null) archivos = new ArrayList<>();

        tvTitulo.setText(titulo);

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }
}
