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

    private static final String ACCESS_TOKEN = DropboxConfig.ACCESS_TOKEN;
    private int idCurso;
    private List<PreguntaCuestionario> preguntasClase = new ArrayList<>();
    private final int MAX_OPCIONES = 4;
    private final ArrayList<View> opcionesList = new ArrayList<>();
    private Uri im = Uri.EMPTY;
    private String urlVideoSubido = null;

    //Elementos visibles
    private EditText et_pregunta,
            et_titulo,
            et_texto;
    private Button btn_subirpregunta,btn_subirarchivo, btn_subirclase;
    private LinearLayout containerOpciones;
    private ImageView vid,btn_subirvideo;

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
        btn_subirpregunta = findViewById(R.id.btn_subir_pregunta);
        containerOpciones = findViewById(R.id.container_opciones);

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
       /* if (containerOpciones.getChildCount() > 0) {
            LinearLayout primeraOpcion = (LinearLayout) containerOpciones.getChildAt(0);
            EditText primerEditText = primeraOpcion.findViewById(R.id.et_ingresar_respuesta);
            if (primerEditText != null) {
                addTextWatcher(primerEditText);
            }
        }*/


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

//______parte del las casillas de las preguntas------------------------
    // detectar cambios en los EditText
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

    // Agregar una nueva opción de respuesta si es necesario
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

        executor.execute(() -> {
            DbxClientV2 client = new DropboxConfig(ACCESS_TOKEN).getClient();

            try (FileInputStream fis = new FileInputStream(archivo)) {
                final long totalBytes = archivo.length();  // Hacemos esta variable final
                final long[] bytesTransferidos = {0};  // Hacemos esto un arreglo para modificar dentro de la lambda

                // Notificación inicial de progreso
                handler.post(() -> NotificacionHelper.mostrarProgreso(
                        CrearClase.this, 35, "Subiendo material", "Subiendo archivo...", 0));

                FileMetadata metadata = client.files()
                        .uploadBuilder("/Aplicaciones/skillfullharmony/ClasesVideos/" + archivo.getName())
                        .uploadAndFinish(fis, bytes -> {
                            bytesTransferidos[0] += bytes;
                            // Actualizar el progreso
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
                    Toast.makeText(this, "Video subido correctamente", Toast.LENGTH_SHORT).show();
                    guardarVideoTemporal(urlVideo); // Guardamos el enlace para usarlo al crear la clase
                    // Completa la notificación de progreso
                    NotificacionHelper.completarProgreso(CrearClase.this, 35, "Subida exitosa", "Tu video fue subido correctamente.");
                });

            } catch (Exception e) {
                Log.e("Dropbox", "Error al subir video", e);
                handler.post(() -> {
                    Toast.makeText(this, "Error al subir video", Toast.LENGTH_SHORT).show();
                    // Notificación de error
                    NotificacionHelper.mostrarError(CrearClase.this, 36, "Error de subida", "Hubo un problema al subir el video.");
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

    private void SubirArchivo(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",                   // .doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Para que no elija cosas que no se pueden abrir
        filePickerLauncher.launch(intent);
    }
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri fileUri = result.getData().getData();
            long fileSizeInBytes = getFileSizeFromUri(fileUri);
            long maxSizeInBytes = 20 * 124 * 124; //
            if (fileSizeInBytes <= maxSizeInBytes) {
                Toast.makeText(this, "Archivo válido", Toast.LENGTH_SHORT).show();
                // Aquí puedes hacer lo que necesites con el archivo
            } else {
                Toast.makeText(this, "El archivo supera los 200MB", Toast.LENGTH_LONG).show();
            }
        }
    });

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


                /*
                if (etRespuesta != null) {
                    String respuestaTexto = etRespuesta.getText().toString().trim();
                    Log.d("SubirPregunta", "Opción " + i + ": " + respuestaTexto);
                    if (!respuestaTexto.isEmpty()) {
                        respuestas.add(respuestaTexto);
                        if (cbCorrecta != null) {
                            Log.d("SubirPregunta", "Checkbox " + i + " isChecked: " + cbCorrecta.isChecked());
                            if (cbCorrecta.isChecked()) {
                                respuestaCorrectaIndex = i;
                                respuestasCorrectasCount++;
                            }
                        }
                    }
                }*/
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

        Toast.makeText(this, "Pidiendo datos al servidor", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CrearClase.this, "idClase: " + nuevoIdClase, Toast.LENGTH_SHORT).show();

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

                    Toast.makeText(CrearClase.this, "Creando clase", Toast.LENGTH_SHORT).show();
                    db.collection("clases").add(clase).addOnSuccessListener(documentReference -> {
                        db.collection("cursos").document(String.valueOf(idCurso)).update("fechaActualizacion", Timestamp.now())
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(CrearClase.this, "Clase creada exitosamente", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }).addOnFailureListener(e->{
                                            Toast.makeText(CrearClase.this, "Error al actualizar la fecha de actualización", Toast.LENGTH_SHORT).show();
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
            File tempFile = File.createTempFile("temp_video", ".mp4", getCacheDir());
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
            Log.e("Archivo", "Error al copiar URI a archivo temporal", e);
            return null;
        }
    }

}
