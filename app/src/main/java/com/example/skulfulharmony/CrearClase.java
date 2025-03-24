package com.example.skulfulharmony;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class CrearClase extends AppCompatActivity {
    private EditText et_respuestas, et_pregunta;
    private Button btn_subirvideo, btn_subirarchivos,btn_subirpregunta;
    private LinearLayout opcionesLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        et_pregunta = findViewById(R.id.et_ingresar_pregunta);
        btn_subirpregunta = findViewById(R.id.btn_subir_pregunta);
        opcionesLayout = findViewById(R.id.ll_oprespuestas);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        et_respuestas = findViewById(R.id.et_opcrespuesta);

        setContentView(R.layout.activity_crearclase);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
    private void agregarNuevaOpcion() {
        LinearLayout nuevaOpcion = new LinearLayout(this);
        nuevaOpcion.setOrientation(LinearLayout.HORIZONTAL);
        nuevaOpcion.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        editText.setHint("Ingresa una opción de respuesta");

        nuevaOpcion.addView(checkBox);
        nuevaOpcion.addView(editText);
        opcionesLayout.addView(nuevaOpcion);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && esUltimoEditText(editText)) {
                    agregarNuevaOpcion();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    eliminarOpcionesVacias();
                }
            }
        });
    }

    private boolean esUltimoEditText(EditText editText) {
        int index = opcionesLayout.indexOfChild((View) editText.getParent());
        return index == opcionesLayout.getChildCount() - 1;
    }

    private void eliminarOpcionesVacias() {
        for (int i = 0; i < opcionesLayout.getChildCount(); i++) {
            LinearLayout opcion = (LinearLayout) opcionesLayout.getChildAt(i);
            EditText editText = (EditText) opcion.getChildAt(1);

            if (editText.getText().toString().trim().isEmpty() && opcionesLayout.getChildCount() > 1) {
                opcionesLayout.removeView(opcion);
                break;
            }
        }
    }


}