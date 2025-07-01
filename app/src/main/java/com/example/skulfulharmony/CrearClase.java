package com.example.skulfulharmony;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.CheckBox;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.notifications.NotificacionHelper;
import com.example.skulfulharmony.server.config.DropboxConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrearClase extends AppCompatActivity {

    //Variables

    private int idCurso;
    private List<PreguntaCuestionario> preguntasClase = new ArrayList<>();
    private final int MAX_OPCIONES = 5;
    private final ArrayList<View> opcionesList = new ArrayList<>();
    private Uri im = Uri.EMPTY;
    private String urlVideoSubido = null;
    private TextView tvEstadoVideo, tvEstadoArchivos;
    private List<String> archivosAdjuntosUrls = new ArrayList<>();


    private EditText et_pregunta, et_titulo, et_texto;
    private Button btn_subirpregunta,btn_subirarchivo, btn_subirclase;
    private LinearLayout containerOpciones;
    private ImageView vid, btn_subirvideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearclase);


        btn_subirclase=findViewById(R.id.btn_subir_clase);
        btn_subirarchivo=findViewById(R.id.btn_cargar_archivos);
        btn_subirvideo=findViewById(R.id.btn_cargar_video);
        btn_subirpregunta=findViewById(R.id.btn_subir_pregunta);
        et_titulo=findViewById(R.id.et_titulo_nueva_clase);
        et_texto=findViewById(R.id.et_descripcion_crear_clase);
        et_pregunta = findViewById(R.id.et_ingresar_pregunta);
        containerOpciones = findViewById(R.id.container_opciones);
        tvEstadoVideo = findViewById(R.id.tv_estado_video);
        tvEstadoArchivos = findViewById(R.id.tv_estado_archivos);

        idCurso = getIntent().getIntExtra("idCurso", -1);

       // vid=findViewById(R.id.im_foto);

        EdgeToEdge.enable(this);

        // Asegurar que el primer campo tenga el listener
        LayoutInflater inflater = LayoutInflater.from(this);
        View initialOption = inflater.inflate(R.layout.holder_opciones, containerOpciones, false);
        EditText initialEditText = initialOption.findViewById(R.id.et_opcrespuesta);
        if (initialEditText != null) {
            addTextWatcher(initialEditText);
        }
        containerOpciones.addView(initialOption);
        opcionesList.add(initialOption);



        btn_subirvideo.setOnClickListener(v-> Subirvideo());
        btn_subirarchivo.setOnClickListener(v->SubirArchivo());
        btn_subirpregunta.setOnClickListener(v->SubirPregunta());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View rootView=findViewById(android.R.id.content);

        btn_subirclase.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  subirClaseAFirebase();
              }
          });


    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    agregarOpSiFalta();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                removeExtraEmptyOptions();
                if (s.length() == 0) {
                    removeEmptyOptions();
                }
            }
        });
    }


    private void agregarOpSiFalta() {
        if (opcionesList.size() >= MAX_OPCIONES) return; // No más de 5 opciones

        LayoutInflater inflater = LayoutInflater.from(this);
        View newOption = inflater.inflate(R.layout.holder_opciones, containerOpciones, false);

        EditText newEditText = newOption.findViewById(R.id.et_opcrespuesta);
        if (newEditText != null) {
            addTextWatcher(newEditText);
        }

        containerOpciones.addView(newOption);
        opcionesList.add(newOption);
    }

    // Eliminar opciones vacías
    private void removeEmptyOptions() {
        for (int i = opcionesList.size() - 1; i >= 0; i--) {
            View option = opcionesList.get(i);
            EditText editText = option.findViewById(R.id.et_ingresar_respuesta);

            if (editText != null && editText.getText().toString().isEmpty()) {
                containerOpciones.removeView(option);
                opcionesList.remove(i);
            }
        }
    }
    private void removeExtraEmptyOptions() {
        int emptyCount = 0;
        View lastEmptyView = null;

        for (View option : opcionesList) {
            EditText editText = option.findViewById(R.id.et_opcrespuesta);
            if (editText != null && editText.getText().toString().trim().isEmpty()) {
                emptyCount++;
                lastEmptyView = option;
            }
        }

        // Si hay más de un campo vacío, elimina solo uno
        if (emptyCount > 1 && lastEmptyView != null) {
            containerOpciones.removeView(lastEmptyView);
            opcionesList.remove(lastEmptyView);
        }
    }
    //---parte de subir el video

    private void subirVideoADropbox(File archivo) {
        if (archivo == null) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        long fileSize = archivo.length(); // Tamaño en bytes
        long maxFileSize = 500 * 1024 * 1024; // 500 MB

        if (fileSize > maxFileSize) {
            Toast.makeText(this, "El video es demasiado grande (máx 500MB)", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Aviso visual inmediato
        handler.post(() -> Toast.makeText(this, "Subiendo video a Dropbox...", Toast.LENGTH_SHORT).show());

        DropboxConfig.obtenerAccessTokenFirebase(new DropboxConfig.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                executor.execute(() -> {
                    DbxClientV2 client = new DropboxConfig(token).getClient();

                    try (FileInputStream fis = new FileInputStream(archivo)) {
                        final long totalBytes = archivo.length();
                        final long[] bytesTransferidos = {0};

                        handler.post(() -> NotificacionHelper.mostrarProgreso(
                                CrearClase.this, 35, "Subiendo material", "Subiendo archivo...", 0));

                        FileMetadata metadata = client.files()
                                .uploadBuilder("/Aplicaciones/skillfullharmony/ClasesVideos/" + archivo.getName())
                                .uploadAndFinish(fis, bytes -> {
                                    bytesTransferidos[0] += bytes;
                                    int progreso = (int) ((bytesTransferidos[0] * 100) / totalBytes);
                                    handler.post(() -> NotificacionHelper.mostrarProgreso(
                                            CrearClase.this, 35, "Subiendo material", "Subiendo archivo...", progreso));
                                });

                        SharedLinkMetadata linkMetadata = client.sharing()
                                .createSharedLinkWithSettings(metadata.getPathLower());

                        String urlVideo = linkMetadata.getUrl()
                                .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                                .replace("?dl=0", "");

                        handler.post(() -> {
                            Toast.makeText(CrearClase.this, "Video subido correctamente", Toast.LENGTH_SHORT).show();
                            guardarVideoTemporal(urlVideo);
                            tvEstadoVideo.setText("🎥 Video subido");
                            NotificacionHelper.completarProgreso(CrearClase.this, 35, "Subida exitosa", "Tu video fue subido correctamente.");
                        });

                    } catch (Exception e) {
                        Log.e("Dropbox", "Error al subir video", e);
                        handler.post(() -> {
                            Toast.makeText(CrearClase.this, "Error al subir video", Toast.LENGTH_SHORT).show();
                            NotificacionHelper.mostrarError(CrearClase.this, 36, "Error de subida", "Hubo un problema al subir el video.");
                        });
                    } finally {
                        executor.shutdown();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(() -> {
                    Toast.makeText(CrearClase.this, "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void guardarVideoTemporal(String url) {
        urlVideoSubido = url;
    }

    private void Subirvideo(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        videoPickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri videoUri = result.getData().getData();
            im = videoUri;

            long videoSizeInBytes = getFileSizeFromUri(videoUri);
            long maxSizeInBytes = 500 * 1024 * 1024; // 500MB

                if (videoSizeInBytes <= maxSizeInBytes) {
                    Toast.makeText(this, "Video válido", Toast.LENGTH_SHORT).show();
                        try {
                            // Mostrar imagen previa del video (opcional)
                            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                                    getContentResolver(),
                                    Long.parseLong(videoUri.getLastPathSegment()),
                                    MediaStore.Video.Thumbnails.MINI_KIND,
                                    null
                            );
                            btn_subirvideo.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.e("VideoPreview", "Error al generar miniatura", e);
                        }
                        File archivoTemporal = copiarUriAArchivoTemporal(videoUri);
                        subirVideoADropbox(archivoTemporal);
                } else {
                    Toast.makeText(this, "El video supera los 500MB", Toast.LENGTH_LONG).show();
                }
        }
    });


    private  long getFileSizeFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            long size = cursor.getLong(sizeIndex);
            cursor.close();
            return size;
        }
        return 0;
    }
//---parte de subir los archivos

    private void subirArchivoADropbox(File archivo) {
        if (archivo == null) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Obtener token primero (en UI)
        DropboxConfig.obtenerAccessTokenFirebase(new DropboxConfig.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                executor.execute(() -> {
                    DbxClientV2 client = new DropboxConfig(token).getClient();

                    try (FileInputStream fis = new FileInputStream(archivo)) {
                        FileMetadata metadata = client.files()
                                .uploadBuilder("/Aplicaciones/skillfullharmony/ClasesArchivos/" + archivo.getName())
                                .uploadAndFinish(fis);

                        SharedLinkMetadata linkMetadata = client.sharing()
                                .createSharedLinkWithSettings(metadata.getPathLower());

                        String urlArchivo = linkMetadata.getUrl()
                                .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                                .replace("?dl=0", "");

                        handler.post(() -> {
                            archivosAdjuntosUrls.add(urlArchivo);
                            tvEstadoArchivos.setText("📎 Archivos adjuntos: " + archivosAdjuntosUrls.size());
                            Toast.makeText(CrearClase.this, "Archivo subido correctamente", Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        Log.e("Dropbox", "Error al subir archivo", e);
                        handler.post(() -> {
                            Toast.makeText(CrearClase.this, "Error al subir archivo", Toast.LENGTH_SHORT).show();
                        });
                    } finally {
                        executor.shutdown();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(() -> {
                    Toast.makeText(CrearClase.this, "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void SubirArchivo(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf"); // ✅ Solo PDF
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        // 🎯 Múltiples archivos seleccionados
                        int total = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < total; i++) {
                            Uri fileUri = result.getData().getClipData().getItemAt(i).getUri();
                            manejarArchivoSeleccionado(fileUri);
                        }
                    } else if (result.getData().getData() != null) {
                        // ✅ Solo un archivo seleccionado
                        Uri fileUri = result.getData().getData();
                        manejarArchivoSeleccionado(fileUri);
                    }
                }
            });


    private void manejarArchivoSeleccionado(Uri fileUri) {
        String nombreArchivo = obtenerNombreArchivoDesdeUri(fileUri);
        if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".pdf")) {
            Toast.makeText(this, "Solo se permiten archivos PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        long fileSizeInBytes = getFileSizeFromUri(fileUri);
        long maxSizeInBytes = 200 * 1024 * 1024; // 200MB

        if (fileSizeInBytes <= maxSizeInBytes) {
            File archivoTemporal = copiarUriAArchivoTemporal(fileUri);
            subirArchivoADropbox(archivoTemporal);
        } else {
            Toast.makeText(this, "El archivo supera los 200MB", Toast.LENGTH_LONG).show();
        }
    }


    //__________________coso de subir la pregunta
    private void SubirPregunta() {

        if (preguntasClase.size() >= 5) {
            Toast.makeText(this, "Solo puedes subir un máximo de 5 preguntas", Toast.LENGTH_SHORT).show();
            return;
        }else {
            String preguntaTexto = et_pregunta.getText().toString().trim();
            List<String> respuestas = new ArrayList<>();
            Integer respuestaCorrectaIndex = null;
            int respuestasCorrectasCount = 0;

            Log.d("SubirPregunta", "Pregunta ingresada: " + preguntaTexto);

            // Recorrer las opciones de respuesta
            for (int i = 0; i < containerOpciones.getChildCount(); i++) {
                View opcionView = containerOpciones.getChildAt(i);
                EditText etRespuesta = opcionView.findViewById(R.id.et_opcrespuesta);
                CheckBox cbCorrecta = opcionView.findViewById(R.id.cb_correcta);

                if (etRespuesta != null && cbCorrecta != null) {
                    String respuestaTexto = etRespuesta.getText().toString().trim();
                    if (!respuestaTexto.isEmpty()) {
                        respuestas.add(respuestaTexto);
                        if (cbCorrecta.isChecked()) {
                            respuestaCorrectaIndex = respuestas.size() - 1; // Usar el índice de la lista de respuestas no vacías
                            respuestasCorrectasCount++;
                        }
                    }
                }


            }

            Log.d("SubirPregunta", "Respuestas correctas encontradas: " + respuestasCorrectasCount);

            // Validar si se seleccionó una única respuesta correcta
            if (respuestasCorrectasCount == 0) {
                Toast.makeText(this, "No marcaste ninguna respuesta como correcta.", Toast.LENGTH_SHORT).show();
                return; // Salir si no hay respuesta correcta
            } else if (respuestasCorrectasCount > 1) {
                Toast.makeText(this, "Marcaste más de una respuesta como correcta.", Toast.LENGTH_SHORT).show();
                return; // Salir si hay más de una respuesta correcta
            }

            // Crear el objeto PreguntaCuestionario
            if (!respuestas.isEmpty() && respuestaCorrectaIndex != null) {
                PreguntaCuestionario preguntaCuestionario = new PreguntaCuestionario(preguntaTexto, respuestas, respuestaCorrectaIndex);
                preguntasClase.add(preguntaCuestionario);
                // Inflar el CardView
                LayoutInflater inflater = LayoutInflater.from(CrearClase.this);
                View cardView = inflater.inflate(R.layout.holder_preguntasinicio, null);

                // Obtener referencias a las vistas dentro del CardView
                TextView tvPreguntaTextoCard = cardView.findViewById(R.id.tv_preguntatexto);
                TextView tvRespuestasCard = cardView.findViewById(R.id.tv_respuestas);
                TextView tvRespuestaCorrectaCard = cardView.findViewById(R.id.tv_respuesta_correcta);
                ImageView btnEliminarPreguntaCard = cardView.findViewById(R.id.btn_eliminar_pregunta);

                // Mostrar la pregunta
                tvPreguntaTextoCard.setText(preguntaCuestionario.getPregunta());

                // Mostrar las respuestas con un enter entre cada una
                StringBuilder respuestasTexto = new StringBuilder();
                for (String respuesta : preguntaCuestionario.getRespuestas()) {
                    respuestasTexto.append("- ").append(respuesta).append("\n");
                }
                tvRespuestasCard.setText(respuestasTexto.toString().trim());

                // Mostrar la respuesta correcta (basándonos en el índice)
                if (preguntaCuestionario.getRespuestaCorrecta() != null &&
                        preguntaCuestionario.getRespuestaCorrecta() >= 0 &&
                        preguntaCuestionario.getRespuestaCorrecta() < preguntaCuestionario.getRespuestas().size()) {
                    tvRespuestaCorrectaCard.setText(preguntaCuestionario.getRespuestas().get(preguntaCuestionario.getRespuestaCorrecta()));
                } else {
                    tvRespuestaCorrectaCard.setText("Error al obtener la respuesta correcta");
                }

                // Configurar OnClickListener para el botón de eliminar
                btnEliminarPreguntaCard.setOnClickListener(v -> {
                    LinearLayout containerPreguntasCreadas = findViewById(R.id.container_preguntas_creadas);
                    if (containerPreguntasCreadas != null) {
                        containerPreguntasCreadas.removeView(cardView);
                        preguntasClase.remove(preguntaCuestionario);
                        Toast.makeText(CrearClase.this, "Pregunta eliminada.", Toast.LENGTH_SHORT).show();
                    }
                });

                // Agregar el CardView al contenedor
                LinearLayout containerPreguntasCreadas = findViewById(R.id.container_preguntas_creadas);
                if (containerPreguntasCreadas != null) {
                    containerPreguntasCreadas.addView(cardView);
                } else {
                    Toast.makeText(this, "No se encontró el contenedor para la pregunta creada.", Toast.LENGTH_SHORT).show();
                }

                // Mostrar mensaje de éxito
                Toast.makeText(this, "Se cargó bien la pregunta.", Toast.LENGTH_SHORT).show();

                // Opcional: Limpiar los campos
                et_pregunta.setText("");
                containerOpciones.removeAllViews();
                LayoutInflater inflaterOpciones = LayoutInflater.from(CrearClase.this);
                View newOption = inflaterOpciones.inflate(R.layout.holder_opciones, containerOpciones, false);
                EditText newEditText = newOption.findViewById(R.id.et_opcrespuesta);
                if (newEditText != null) {
                    addTextWatcher(newEditText);
                }

                containerOpciones.addView(newOption);
                opcionesList.clear();
                opcionesList.add(newOption);

            } else {
                Toast.makeText(this, "Error al crear la pregunta.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void subirClaseAFirebase() {
        //Verificacion de datos
        String titulo = et_titulo.getText().toString();
        String imagen = !Uri.EMPTY.equals(im) ? im.toString() : null;
        String texto = et_texto.getText().toString();

        if (titulo.isEmpty() || titulo.equals("")) {
            Toast.makeText(this, "Debes ingresar un titulo", Toast.LENGTH_SHORT).show();
            return;
        }if (texto.isEmpty() || texto.equals("")) {
            Toast.makeText(this, "Debes ingresar un texto", Toast.LENGTH_SHORT).show();
            return;
        }
        //Logica para subir archivos a firebase

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No estás logueado", Toast.LENGTH_SHORT).show();
            return;
        }else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("clases")
                .whereEqualTo("idCurso", idCurso)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int maxIdClase = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Long idClase = document.getLong("idClase");
                        if (idClase != null && idClase > maxIdClase) {
                            maxIdClase = idClase.intValue();
                        }
                    }

                    int nuevoIdClase = maxIdClase + 1;

                    Clase clase = new Clase();
                    clase.setIdCurso(idCurso);
                    clase.setIdClase(nuevoIdClase);
                    clase.setTitulo(titulo);
                    clase.setImagen(imagen);
                    clase.setTextos(texto);
                    clase.setPreguntas(preguntasClase);
                    clase.setVideoUrl(urlVideoSubido); // 🔥 Guarda el enlace de Dropbox del video
                    Timestamp  timestamp = Timestamp.now();
                    clase.setFechaCreacionf(timestamp);
                    clase.setCreadorUid(currentUser.getUid());
                    clase.setCreadorCorreo(currentUser.getEmail());
                    clase.setArchivos(archivosAdjuntosUrls);

                    db.collection("clases").add(clase).addOnSuccessListener(documentReference -> {
                        db.collection("cursos")
                                .whereEqualTo("idCurso", idCurso) // buscar por el campo, no por el ID de documento
                                .limit(1)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshotsc -> {
                                    if (!queryDocumentSnapshotsc.isEmpty()) {
                                        DocumentSnapshot cursoDoc = queryDocumentSnapshotsc.getDocuments().get(0);
                                        String docId = cursoDoc.getId();

                                        db.collection("cursos")
                                                .document(docId)
                                                .update("fechaActualizacionf", Timestamp.now())
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(CrearClase.this, "Clase creada exitosamente", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(CrearClase.this, "Error al actualizar la fecha de actualización", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CrearClase.this, "Error al pedir datos al servidor" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    }).addOnFailureListener(e -> {
                        Toast.makeText(CrearClase.this, "Error al subir la clase", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener( e -> {
                    Toast.makeText(CrearClase.this, "Error al obtener datos"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
        }
        //finish();
    }

    private File copiarUriAArchivoTemporal(Uri uri) {
        try {
            String nombreArchivo = obtenerNombreArchivoDesdeUri(uri);
            String extension = obtenerExtension(nombreArchivo);

            File tempFile = File.createTempFile("temp_archivo", "." + extension, getCacheDir());

            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Cambia el nombre final para Dropbox
            File renamedFile = new File(getCacheDir(), nombreArchivo);
            if (tempFile.renameTo(renamedFile)) {
                return renamedFile;
            } else {
                return tempFile; // fallback
            }

        } catch (IOException e) {
            Log.e("Archivo", "Error al copiar URI a archivo temporal", e);
            return null;
        }
    }

    private String obtenerNombreArchivoDesdeUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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
