package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class login extends AppCompatActivity {

    private EditText etCorreoOUser, etContraseña_Iniciar;
    private Button btnIniciar, btnGoToCrear;
    private ImageButton btnGoogleIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);


        etCorreoOUser = findViewById(R.id.Et_correoOuser);
        etContraseña_Iniciar = findViewById(R.id.Et_contraseña_iniciar);
        btnIniciar = findViewById(R.id.btnIniciarsecion);
        btnGoogleIniciar = findViewById(R.id.btn_google_iniciar);
        btnGoToCrear = findViewById(R.id.btn_gotocrearcuenta);


        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCorreoOUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etCorreoOUser.setText("");
                }
            }
        });
        etContraseña_Iniciar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etContraseña_Iniciar.setText("");
                }
            }
        });


        etContraseña_Iniciar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etContraseña_Iniciar.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                            | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etContraseña_Iniciar.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnGoToCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    Toast.makeText(login.this, "JEJE :)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(login.this, "Llena todos los campos por favor", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private boolean validarCampos() {
        String nombreUsuario =etCorreoOUser.getText().toString().trim();
        String contras = etContraseña_Iniciar.getText().toString().trim();

        return !nombreUsuario.isEmpty() && !contras.isEmpty() &&
        !nombreUsuario.equalsIgnoreCase("Nombre de usuario")
                && !contras.equalsIgnoreCase("Contraseña");
    }
}
