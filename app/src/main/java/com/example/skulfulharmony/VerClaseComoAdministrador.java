package com.example.skulfulharmony;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterArchivosClase;
import com.example.skulfulharmony.adapters.AdapterPreguntasEnVerClase;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerClaseComoAdministrador extends AppCompatActivity {

    private FirebaseFirestore db;
    private String docDenuncia;
    private String docClase;
    private int idClase;
    private int idCurso;
    private Clase clase;

    private TextView tvTitulo, tvTexto, txtTipoDenuncia, txtContenidoDenuncia;
    private RecyclerView rvArchivos, rvPreguntas;
    private PlayerView playerView;
    private ExoPlayer player;
    private Button btnDesestimar, btnEliminar;

    private AdapterPreguntasEnVerClase adapterPreguntas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_clase_como_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        idCurso = getIntent().getIntExtra("idCurso", -1);
        idClase = getIntent().getIntExtra("idClase", -1);
        int idDenuncia = getIntent().getIntExtra("idDenuncia", -1);

        tvTitulo = findViewById(R.id.vvca_txt_titulo_clase);
        tvTexto = findViewById(R.id.vvca_txt_informacion_clase);
        txtTipoDenuncia = findViewById(R.id.vcca_txt_tipodenuncia_clase);
        txtContenidoDenuncia = findViewById(R.id.vcca_txt_textodenuncia_clase);
        rvArchivos = findViewById(R.id.vvca_rv_archivos_clase);
        rvPreguntas = findViewById(R.id.vvca_rv_preguntas_clase);
        playerView = findViewById(R.id.vvca_pv_video_clase);
        btnDesestimar = findViewById(R.id.vcca_btn_desestimar_clase);
        btnEliminar = findViewById(R.id.vcca_btn_eliminar_clase);

        cargarClaseYDenuncia(idCurso, idClase, idDenuncia);

        btnDesestimar.setOnClickListener(v -> mostrarDialogoDesestimar());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void cargarClaseYDenuncia(int idCurso, int idClase, int idDenuncia) {
        db.collection("denuncias")
                .whereEqualTo("idDenuncia", idDenuncia)
                .limit(1)
                .get()
                .addOnSuccessListener(denuncias -> {
                    if (!denuncias.isEmpty()) {
                        DocumentSnapshot doc = denuncias.getDocuments().get(0);
                        docDenuncia = doc.getId();
                        Denuncia denuncia = doc.toObject(Denuncia.class);
                        txtTipoDenuncia.setText("Tipo: " + denuncia.getTipo_denuncia());
                        txtContenidoDenuncia.setText("Contenido: " + denuncia.getDenuncia());
                    }
                });

        db.collection("clases")
                .whereEqualTo("idCurso", idCurso)
                .whereEqualTo("idClase", idClase)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot claseDoc = snapshot.getDocuments().get(0);
                        docClase = claseDoc.getId();

                        try {
                            clase = claseDoc.toObject(Clase.class);
                        } catch (Exception e) {
                            finish();
                            return;
                        }

                        if (clase == null) {
                            Toast.makeText(this, "Clase no encontrada", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        tvTitulo.setText(clase.getTitulo());
                        tvTexto.setText(clase.getTextos());

                        if (clase.getVideoUrl() != null && !clase.getVideoUrl().isEmpty()) {
                            player = new ExoPlayer.Builder(this).build();
                            playerView.setPlayer(player);
                            player.setMediaItem(MediaItem.fromUri(clase.getVideoUrl()));
                            player.prepare();
                        }

                        if (clase.getArchivos() != null && !clase.getArchivos().isEmpty()) {
                            rvArchivos.setLayoutManager(new LinearLayoutManager(this));
                            rvArchivos.setAdapter(new AdapterArchivosClase(clase.getArchivos(), this));
                        }

                        if (clase.getPreguntas() != null && !clase.getPreguntas().isEmpty()) {
                            rvPreguntas.setLayoutManager(new LinearLayoutManager(this));
                            adapterPreguntas = new AdapterPreguntasEnVerClase(clase.getPreguntas(), this);
                            rvPreguntas.setAdapter(adapterPreguntas);
                        }
                    }
                });
    }

    private void mostrarDialogoDesestimar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_desestimar_denuncia, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_desestimar_dialog_cancelar).setOnClickListener(view -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_desestimar_dialog_desestimar).setOnClickListener(view -> {
            if (docDenuncia != null) {
                db.collection("denuncias").document(docDenuncia)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            finish();
                        });
            }
        });

        dialog.show();
    }

    private void mostrarDialogoEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_comprobacion_de_eliminar, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_cancelar_dialog_eliminarcontenido).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_enviar_dialog_eliminarcontenido).setOnClickListener(v -> {
            if (docClase != null && docDenuncia != null) {
                db.collection("clases").document(docClase)
                        .delete()
                        .addOnSuccessListener(aVoid -> db.collection("denuncias").document(docDenuncia)
                                .delete()
                                .addOnSuccessListener(aVoid2 -> {
                                    dialog.dismiss();
                                    finish();
                                }));
            }
        });

        dialog.show();
    }

    @Override
    protected void onStop() {
        if (player != null) player.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (player != null) player.release();
        super.onDestroy();
    }
}

