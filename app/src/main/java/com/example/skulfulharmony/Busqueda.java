package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Busqueda extends AppCompatActivity {
    private EditText et_buscar;
    private Button btn_genero, btn_instrumrnto, btn_dificultad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busqueda);
        btn_genero=findViewById(R.id.btn_bgenero);
        btn_dificultad=findViewById(R.id.btn_bdificultad);
        btn_instrumrnto=findViewById(R.id.btn_binstumento);
        et_buscar=findViewById(R.id.et_parabuscar);
        //et_buscar = findViewById(R.id.et_parabuscar);
        if (getIntent().getBooleanExtra("focus", false)) {
            et_buscar.requestFocus(); // Solicita el foco
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // Muestra el teclado
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}