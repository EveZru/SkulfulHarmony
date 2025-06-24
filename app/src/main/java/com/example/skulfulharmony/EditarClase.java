package com.example.skulfulharmony;

import android.app.Activity; // Necesario para ActivityResultLauncher
import android.content.Intent;
import android.database.Cursor; // Necesario para obtener tamaño de archivo
import android.net.Uri; // Necesario para manejar URIs de video
import android.os.Bundle;
import android.os.Handler; // Necesario para subida a Dropbox
import android.os.Looper; // Necesario para subida a Dropbox
import android.provider.MediaStore; // Necesario para selección de video
import android.provider.OpenableColumns; // Necesario para obtener nombre/tamaño de archivo
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
// import android.widget.LinearLayout; // Ya no es necesario si quitamos preguntas y opciones
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher; // Necesario para seleccionar video
import androidx.activity.result.contract.ActivityResultContracts; // Necesario para seleccionar video
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importaciones de ExoPlayer
import androidx.media3.common.MediaItem; // Necesario para MediaItem
import androidx.media3.exoplayer.ExoPlayer; // Necesario para ExoPlayer
import androidx.media3.ui.PlayerView; // Necesario para PlayerView

// Importaciones de Dropbox (Asegúrate de tener estas dependencias en tu build.gradle)
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.example.skulfulharmony.server.config.DropboxConfig; // Asegúrate de que esta clase exista y tenga ACCESS_TOKEN

// Importaciones de Firestore y tus objetos de modelo
import com.example.skulfulharmony.javaobjects.courses.Clase;
// import com.example.skulfulharmony.javaobjects.courses.Curso; // No utilizada directamente en esta clase
// import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario; // Eliminada
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario; // Se mantiene si es parte del modelo Clase

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot; // Necesario para QueryDocumentSnapshot

import java.io.File; // Necesario para manejar archivos
import java.io.FileInputStream; // Necesario para leer archivos
import java.io.FileOutputStream; // Necesario para escribir archivos
import java.io.IOException; // Necesario para manejar excepciones de I/O
import java.io.InputStream; // Necesario para leer streams de archivos
import java.util.ArrayList; // Se mantiene por si hay otras listas en Clase (ej. archivos, comentarios)
import java.util.HashMap;
import java.util.List; // Se mantiene por si hay otras listas en Clase
import java.util.Map;
import java.util.concurrent.ExecutorService; // Necesario para tareas en segundo plano
import java.util.concurrent.Executors; // Necesario para ExecutorService

public class EditarClase extends AppCompatActivity {

    private Button btn_cambiar, btn_cancelar, btn_cambiarVid;
    private TextView tvNombreClase, tvDescripAnterior;
    private EditText etNuevaDescripcion;
    private PlayerView playerViewEditar;
    private ExoPlayer player;
    private String urlVideoSubido = null;
    private FirebaseFirestore firestore;
    private int idClase;
    private int idCurso;
    private DocumentReference claseDocRef;

    private static final String ACCESS_TOKEN = DropboxConfig.ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_clase);


        btn_cancelar = findViewById(R.id.btn_cancelar);
        tvNombreClase = findViewById(R.id.tv_nombrecurso);
        tvDescripAnterior = findViewById(R.id.tv_descripantreior);
        etNuevaDescripcion = findViewById(R.id.tv_info_verclase);
        btn_cambiar = findViewById(R.id.btn_updatecurso);
        btn_cambiarVid = findViewById(R.id.btn_camiarvid);


        playerViewEditar = findViewById(R.id.vv_videoclaseditar);
        player = new ExoPlayer.Builder(this).build();
        playerViewEditar.setPlayer(player);

        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        idClase = intent.getIntExtra("idClase", -1);
        idCurso = intent.getIntExtra("idCurso", -1);

        if (idClase != -1 && idCurso != -1) {
            cargarDatosClase(idClase, idCurso);
         //   Toast.makeText(this, "ID Clase: " + idClase + ", ID Curso: " + idCurso, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: No se pudo obtener la información de la clase. idClase: " + idClase + ", idCurso: " + idCurso, Toast.LENGTH_LONG).show();
            finish();
        }


        btn_cancelar.setOnClickListener(v -> {   finish();   });
        btn_cambiar.setOnClickListener(v -> { updateClase();    });
        btn_cambiarVid.setOnClickListener(v -> {  Subirvideo();   });
    }


    private void cargarDatosClase(int claseId, int cursoId) {
        firestore.collection("clases")
                .whereEqualTo("idClase", claseId)
                .whereEqualTo("idCurso", cursoId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        claseDocRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        Clase clase = queryDocumentSnapshots.getDocuments().get(0).toObject(Clase.class);

                        if (clase != null) {
                            tvNombreClase.setText("Editarás los datos de la clase: " + clase.getTitulo());
                            tvDescripAnterior.setText("Antigua descripción: " + clase.getTextos());
                            etNuevaDescripcion.setText(clase.getTextos());


                            if (clase.getVideoUrl() != null && !clase.getVideoUrl().isEmpty()) {
                                urlVideoSubido = clase.getVideoUrl();
                                setupVideoPlayer(clase.getVideoUrl());
                            } else {
                                Toast.makeText(this, "No hay video disponible para esta clase.", Toast.LENGTH_SHORT).show();
                                playerViewEditar.setVisibility(View.GONE);
                                urlVideoSubido = null;
                            }

                        } else {
                            Toast.makeText(EditarClase.this, "Error: No se pudo convertir el documento a Clase. Verifique la estructura de su modelo Clase o los datos en Firestore.", Toast.LENGTH_LONG).show();
                            Log.e("EditarClase", "Fallo al convertir documento a objeto Clase para claseId: " + claseId + " y cursoId: " + cursoId + ". Datos del documento: " + queryDocumentSnapshots.getDocuments().get(0).getData());
                            finish();
                        }
                    } else{
                        Toast.makeText(EditarClase.this, "Clase no encontrada con los IDs proporcionados. idClase: " + claseId + ", idCurso: " + cursoId, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarClase.this, "Error al cargar clase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("EditarClase", "Error al cargar clase para claseId: " + claseId + ", cursoId: " + cursoId, e);
                    finish();
                });
    }


    private void setupVideoPlayer(String videoUrl) {
        if (player != null && videoUrl != null && !videoUrl.isEmpty()) {
            Uri videoUri = Uri.parse(videoUrl);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.prepare();
            playerViewEditar.setVisibility(View.VISIBLE);
            player.play();
        } else {
            playerViewEditar.setVisibility(View.GONE);
        }
    }


    private void updateClase() {
        if (claseDocRef == null) {
            Toast.makeText(this, "Error: Referencia a la clase no encontrada para actualizar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevaDescripcion = etNuevaDescripcion.getText().toString().trim();
        Map<String, Object> updates = new HashMap<>();

        if (!nuevaDescripcion.isEmpty()) {
            updates.put("textos", nuevaDescripcion);
        } else {
            Toast.makeText(this, "La descripción no puede estar vacía.", Toast.LENGTH_SHORT).show();
            return;
        }

        updates.put("videoUrl", urlVideoSubido); // Se actualiza la URL del video
        updates.put("fechaActualizacion", Timestamp.now());

        claseDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarClase.this, "Clase actualizada correctamente.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarClase.this, "Error al actualizar la clase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("EditarClase", "Error al actualizar la clase", e);
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
            player = null;
        }
    }



    private void Subirvideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        videoPickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri videoUri = result.getData().getData();

            long videoSizeInBytes = getFileSizeFromUri(videoUri);
            long maxSizeInBytes = 500 * 1024 * 1024;

            if (videoSizeInBytes <= maxSizeInBytes) {
                Toast.makeText(this, "Video seleccionado. Subiendo...", Toast.LENGTH_SHORT).show();
                try {
                    File archivoTemporal = copiarUriAArchivoTemporal(videoUri);
                    if (archivoTemporal != null) {
                        subirVideoADropbox(archivoTemporal);
                    } else {
                        Toast.makeText(this, "Error al preparar el archivo para subir.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("EditarClase", "Error al procesar video seleccionado", e);
                    Toast.makeText(this, "Error al procesar el video.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "El video supera los 500MB (máximo permitido).", Toast.LENGTH_LONG).show();
            }
        }
    });

    private void subirVideoADropbox(File archivo) {
        if (archivo == null) {
            Toast.makeText(this, "Archivo inválido para subir a Dropbox", Toast.LENGTH_SHORT).show();
            return;
        }

        long fileSize = archivo.length();
        long maxFileSize = 500 * 1024 * 1024;

        if (fileSize > maxFileSize) {
            Toast.makeText(this, "El video es demasiado grande (máx 500MB)", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> Toast.makeText(this, "Subiendo video a Dropbox...", Toast.LENGTH_SHORT).show());

        executor.execute(() -> {
            DbxClientV2 client = new DropboxConfig(ACCESS_TOKEN).getClient();

            try (FileInputStream fis = new FileInputStream(archivo)) {
                FileMetadata metadata = client.files()
                        .uploadBuilder("/Aplicaciones/skillfullharmony/ClasesVideos/" + archivo.getName())
                        .uploadAndFinish(fis);

                SharedLinkMetadata linkMetadata = client.sharing()
                        .createSharedLinkWithSettings(metadata.getPathLower());

                String urlVideo = linkMetadata.getUrl()
                        .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                        .replace("?dl=0", "");

                handler.post(() -> {
                    urlVideoSubido = urlVideo;
                    setupVideoPlayer(urlVideo);
                    Toast.makeText(this, "Video subido y cargado correctamente.", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("EditarClase", "Error al subir video a Dropbox", e);
                handler.post(() -> {
                    Toast.makeText(this, "Error al subir video a Dropbox: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (archivo.exists()) {
                    // archivo.delete(); // Descomentar si quieres eliminarlo inmediatamente después de la subida
                }
            }
        });
    }

    private long getFileSizeFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            long size = cursor.getLong(sizeIndex);
            cursor.close();
            return size;
        }
        return 0;
    }

    private File copiarUriAArchivoTemporal(Uri uri) {
        try {
            String nombreArchivo = obtenerNombreArchivoDesdeUri(uri);
            String extension = obtenerExtension(nombreArchivo);

            File tempFile = File.createTempFile("temp_video_", "." + extension, getCacheDir());

            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;

        } catch (IOException e) {
            Log.e("EditarClase", "Error al copiar URI a archivo temporal", e);
            return null;
        }
    }

    private String obtenerNombreArchivoDesdeUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
           try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo != null && nombreArchivo.contains(".")) {
            return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
        } else {
            return "tmp";
        }
    }
}