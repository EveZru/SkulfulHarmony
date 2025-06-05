package com.example.skulfulharmony;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.io.File;

public class VerClaseOffline extends AppCompatActivity {

    private ExoPlayer exoPlayer;
    private PlayerView playerView;
    private ImageView imagenClase;
    private Button btnVerDocumento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clase_offline);

        TextView tituloClase = findViewById(R.id.verclase_vertitulo);
        TextView textoClase = findViewById(R.id.tv_info_verclase);
        imagenClase = findViewById(R.id.imagenClaseOffline);
        playerView = findViewById(R.id.vv_videoclase);
        btnVerDocumento = findViewById(R.id.btn_ver_documento); // 🆕

        ocultarComponentesOnline();

        String titulo = getIntent().getStringExtra("titulo");
        String video = getIntent().getStringExtra("video");
        String imagen = getIntent().getStringExtra("imagen");
        String documento = getIntent().getStringExtra("documento");

        tituloClase.setText(titulo);
        textoClase.setText(""); // por si luego usas algo de texto

        // 🎬 Video
        if (video != null && !video.isEmpty()) {
            File videoFile = new File(video);
            if (videoFile.exists()) {
                exoPlayer = new ExoPlayer.Builder(this).build();
                playerView.setPlayer(exoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile));
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            } else {
                playerView.setVisibility(View.GONE);
            }
        } else {
            playerView.setVisibility(View.GONE);
        }

        // 🖼 Imagen
        if (imagen != null && !imagen.isEmpty()) {
            File imagenFile = new File(imagen);
            if (imagenFile.exists()) {
                Glide.with(this).load(imagenFile).into(imagenClase);
            } else {
                imagenClase.setVisibility(View.GONE);
            }
        } else {
            imagenClase.setVisibility(View.GONE);
        }

        // 📄 Documento (con botón)
        if (documento != null && !documento.isEmpty()) {
            File archivo = new File(documento);
            if (archivo.exists()) {
                btnVerDocumento.setVisibility(View.VISIBLE);
                btnVerDocumento.setOnClickListener(v -> abrirDocumento(archivo));
            } else {
                btnVerDocumento.setVisibility(View.GONE);
            }
        } else {
            btnVerDocumento.setVisibility(View.GONE);
        }
    }

    private void abrirDocumento(File archivo) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", archivo);
        String mimeType = getContentResolver().getType(uri);
        if (mimeType == null) mimeType = "*/*";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ocultarComponentesOnline() {
        int[] ids = {
                R.id.rv_comentarios_verclase,
                R.id.et_comentario_verclase,
                R.id.btn_subir_comentario_clase,
                R.id.ll_estrellas,
                R.id.tv_puntuacion,
                R.id.btt_revisar_respuestas_verclase,
                R.id.rv_preguntasporclase_verclase
        };

        for (int id : ids) {
            View view = findViewById(id);
            if (view != null) view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        super.onDestroy();
    }
}
