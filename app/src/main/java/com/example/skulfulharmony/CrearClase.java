package com.example.skulfulharmony;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.io.IOException;
import java.util.ArrayList;
public class CrearClase extends AppCompatActivity {
    private EditText et_pregunta;
    private Button btn_subirpregunta,btn_subirarchivo;
    private LinearLayout containerOpciones;
    private final int MAX_OPCIONES = 4;
    private final ArrayList<View> opcionesList = new ArrayList<>();
    private ImageView vid,btn_subirvideo;
    private Uri im = Uri.EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearclase);

        btn_subirarchivo=findViewById(R.id.btn_cargar_archivos);
        btn_subirvideo=findViewById(R.id.btn_cargar_video);
        btn_subirpregunta=findViewById(R.id.btn_subir_pregunta);

        et_pregunta = findViewById(R.id.et_ingresar_pregunta);
        btn_subirpregunta = findViewById(R.id.btn_subir_pregunta);
        containerOpciones = findViewById(R.id.container_opciones);

       // vid=findViewById(R.id.im_foto);

        EdgeToEdge.enable(this);

        // Asegurar que el primer campo tenga el listener
        if (containerOpciones.getChildCount() > 0) {
            LinearLayout primeraOpcion = (LinearLayout) containerOpciones.getChildAt(0);
            EditText primerEditText = primeraOpcion.findViewById(R.id.et_ingresar_respuesta);
            if (primerEditText != null) {
                addTextWatcher(primerEditText);
            }
        }


        btn_subirvideo.setOnClickListener(v-> Subirvideo());
        btn_subirarchivo.setOnClickListener(v->SubirArchivo());
        btn_subirpregunta.setOnClickListener(v->SubirPregunta());
      //  btn_subirpregunta.setOnClickListener(v -> addNewOptionIfNeeded());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View rootView=findViewById(android.R.id.content);


        //___________________parte del teclado-----------------------------------
        View rootView2 = findViewById(R.id.main_scrollview); // Asegúrate de que este sea el ID de tu ScrollView

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView2.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView2.getRootView().getHeight();

            // Calcular la altura del teclado
          //  int keypadHeight = screenHeight - r.bottom;
            int keypadHeight = rootView2.getRootView().getHeight() - r.bottom;

            if (keypadHeight > screenHeight * 0.1) { // Umbral: si el teclado ocupa más del 15% de la pantalla
                // Teclado está visible, ajustar el paddingBottom del ScrollView
                View lastEditText = containerOpciones.getChildAt(containerOpciones.getChildCount() - 1);
                if (lastEditText != null) {
                    int[] location = new int[2];
                    lastEditText.getLocationOnScreen(location);
                    int editTextBottom = location[1] + lastEditText.getHeight();
                    int overlap = editTextBottom - r.bottom; // Cuánto se superpone el EditText con la parte visible

                    if (overlap > 0) {
                        rootView2.setPadding(
                                rootView2.getPaddingLeft(),
                                rootView2.getPaddingTop(),
                                rootView2.getPaddingRight(),
                                keypadHeight + overlap // Añadir un pequeño margen extra
                        );
                    } else {
                        rootView2.setPadding(
                                rootView2.getPaddingLeft(),
                                rootView2.getPaddingTop(),
                                rootView2.getPaddingRight(),
                                keypadHeight  // Añadir un pequeño margen si no hay superposición
                        );
                    }
                } else {
                    rootView2.setPadding(
                            rootView2.getPaddingLeft(),
                            rootView2.getPaddingTop(),
                            rootView2.getPaddingRight(),
                            keypadHeight + 20
                    );
                }
            } else {
                // Teclado no está visible, restablecer el paddingBottom
                rootView2.setPadding(
                        rootView2.getPaddingLeft(),
                        rootView2.getPaddingTop(),
                        rootView2.getPaddingRight(),
                        0
                );
            }
        });
        //_______________________________________________
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
                    addNewOptionIfNeeded();
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
    private void addNewOptionIfNeeded() {
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
                lastEmptyView = option; // Guarda la última vista vacía encontrada
            }
        }

        // Si hay más de un campo vacío, elimina solo uno
        if (emptyCount > 1 && lastEmptyView != null) {
            containerOpciones.removeView(lastEmptyView);
            opcionesList.remove(lastEmptyView);
        }
    }
    //---parte de subir el video
    private void Subirvideo(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        imagePickerLauncher.launch(intent);
    }


    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri videoUri = result.getData().getData();
                            im = videoUri;
                            long videoSizeInBytes = getFileSizeFromUri(videoUri);
                            long maxSizeInBytes = 200 * 1024 * 1024; // 200MB

                            if (videoSizeInBytes <= maxSizeInBytes) {

                                Toast.makeText(this, "Video válido", Toast.LENGTH_SHORT).show();

                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), videoUri);
                                     btn_subirvideo.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {

                                Toast.makeText(this, "El video supera los 200MB", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );
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


    private void SubirPregunta() {

        String preguntaTexto = et_pregunta.getText().toString().trim();
        ArrayList<String> respuestas = new ArrayList<>();
        Integer respuestaCorrectaIndex = null;
        int respuestasCorrectasCount = 0;

        Log.d("SubirPregunta", "Pregunta ingresada: " + preguntaTexto);

        // Recorrer las opciones de respuesta
        for (int i = 0; i < containerOpciones.getChildCount(); i++) {
            View opcionView = containerOpciones.getChildAt(i);
            EditText etRespuesta = opcionView.findViewById(R.id.et_opcrespuesta);
            CheckBox cbCorrecta = opcionView.findViewById(R.id.cb_correcta);

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
