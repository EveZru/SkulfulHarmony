package com.example.skulfulharmony;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.CheckBox;
import android.view.View;

import java.util.ArrayList;
public class CrearClase extends AppCompatActivity {
    private EditText et_pregunta;
    private Button btn_subirpregunta;
    private LinearLayout containerOpciones;
    private final int MAX_OPCIONES = 5;
    private final ArrayList<View> opcionesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearclase);  // Esto debe ir primero

        // Inicializar vistas después de setContentView
        et_pregunta = findViewById(R.id.et_ingresar_pregunta);
        btn_subirpregunta = findViewById(R.id.btn_subir_pregunta);
        containerOpciones = findViewById(R.id.container_opciones);

        EdgeToEdge.enable(this);

        // Asegurar que el primer campo tenga el listener
        if (containerOpciones.getChildCount() > 0) {
            LinearLayout primeraOpcion = (LinearLayout) containerOpciones.getChildAt(0);
            EditText primerEditText = primeraOpcion.findViewById(R.id.et_ingresar_respuesta);
            if (primerEditText != null) {
                addTextWatcher(primerEditText);
            }
        }

      //  btn_subirpregunta.setOnClickListener(v -> addNewOptionIfNeeded());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

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

}
