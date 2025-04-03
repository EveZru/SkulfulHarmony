package com.example.skulfulharmony;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.CheckBox;
import android.view.View;

import com.example.skulfulharmony.adapters.AdapterPreguntasAlCrearClase;
import com.example.skulfulharmony.adapters.AdapterPreguntasEnClasesOriginales;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.ArrayList;
public class CrearClase extends AppCompatActivity {
    private EditText et_pregunta;
    private Button btn_subirpregunta,btn_subirVideo;
    private LinearLayout containerOpciones;
    private final int MAX_OPCIONES = 5;
    private final ArrayList<View> opcionesList = new ArrayList<>();
    private RecyclerView rv_preguntas;
    // private AdapterPreguntasAlCrearClase adapterPreguntasAlCrearClase;
    private ArrayList<PreguntaCuestionario> listaPreguntas;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearclase);
        btn_subirVideo=findViewById(R.id.btn_cargar_video);

        et_pregunta = findViewById(R.id.et_ingresar_pregunta);
        containerOpciones = findViewById(R.id.container_opciones);

        // Configurar RecyclerView
        btn_subirpregunta = findViewById(R.id.btn_subir_pregunta);

        listaPreguntas = new ArrayList<>();
        rv_preguntas = findViewById(R.id.rv_preguntascrearclase);
        rv_preguntas.setLayoutManager(new LinearLayoutManager(this));

        btn_subirVideo.setOnClickListener(v->{OpenGalery();});

        // adapterPreguntasAlCrearClase = new AdapterPreguntasAlCrearClase(listaPreguntas);
        // rv_preguntas.setAdapter(adapterPreguntasAlCrearClase);



        EdgeToEdge.enable(this);  // Esto debe ir después de setContentView()

        // Asegurar que el primer campo tenga el listener
        if (containerOpciones.getChildCount() > 0) {
            LinearLayout primeraOpcion = (LinearLayout) containerOpciones.getChildAt(0);
            EditText primerEditText = primeraOpcion.findViewById(R.id.et_ingresar_respuesta);
            if (primerEditText != null) {
                addTextWatcher(primerEditText);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_subirpregunta.setOnClickListener(view ->
                agregarPregunta()
                //PreguntaCuestionario preguntacuestionario=new PerguntaCuestionaro;
              //  AdapterPreguntasAlCrearClase.agregarPregunta(PreguntaCuestionario);
        );}

    // Agregar TextWatcher para detectar cambios en los EditText
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
            addTextWatcher(newEditText); // Agregar el listener al nuevo EditText
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


    private void agregarPregunta() {

        String preguntaTexto = et_pregunta.getText().toString().trim();
        if (preguntaTexto.isEmpty()) return;  // No agregar preguntas vacías

        ArrayList<String> respuestas = new ArrayList<>();
        for (int i = 0; i < containerOpciones.getChildCount(); i++) {
            View opcionView = containerOpciones.getChildAt(i);
            EditText etRespuesta = opcionView.findViewById(R.id.et_opcrespuesta);
            if (etRespuesta != null) {
                String respuestaTexto = etRespuesta.getText().toString().trim();
                if (!respuestaTexto.isEmpty()) {
                    respuestas.add(respuestaTexto);
                }
            }

        }



        if (respuestas.isEmpty()) return;  // No agregar preguntas sin respuestas

        // Crear la nueva pregunta y agregarla a la lista
        PreguntaCuestionario nuevaPregunta = new PreguntaCuestionario(preguntaTexto, respuestas, 0);
        listaPreguntas.add(nuevaPregunta);


        // Mostrar las respuestas en el log
        Log.d("CrearClase", "Pregunta: " + preguntaTexto);
        for (int i = 0; i < respuestas.size(); i++) {
            Log.d("CrerClase", "Respuesta " + (i + 1) + ": " + respuestas.get(i));
        }

        // adapterPreguntasAlCrearClase.notifyDataSetChanged();

        // Limpiar los campos después de agregar
        et_pregunta.setText("");
        containerOpciones.removeAllViews();
    }

    private void OpenGalery(){
        Intent intent=new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
