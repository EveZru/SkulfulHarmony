package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Editable;
import android.text.TextWatcher; // Importación necesaria
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etNameUser, etCorreo, etContraseña;
    private Button btnCrearCuenta, btnGoToIniciar;
    private ImageButton btnGoogleCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        etNameUser = findViewById(R.id.Et_nameuser);
        etCorreo = findViewById(R.id.Et_correo);
        etContraseña = findViewById(R.id.Et_contraseña);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnGoogleCrear = findViewById(R.id.btn_googlecrear);
        btnGoToIniciar = findViewById(R.id.btnGotoiniciar);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etNameUser.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etNameUser.setText("");
            }
        });

        etCorreo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etCorreo.setText("");
            }
        });

        etContraseña.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etContraseña.setText("");
            }
        });


        etContraseña.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    etContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                            | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etContraseña.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        btnGoToIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        });


        btnCrearCuenta.setOnClickListener(v -> {
            if (validarCampos()) {
                Toast.makeText(MainActivity.this, "JEJE", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Llena todos los campos por favor", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validarCampos() {
        String nombreUsuario = etNameUser.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contraseña = etContraseña.getText().toString().trim();

        // Verificar que los campos no estén vacíos y no contengan los textos predeterminados
        return !nombreUsuario.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty()
                && !nombreUsuario.equalsIgnoreCase("Nombre de usuario")
                && !correo.equalsIgnoreCase("Correo")
                && !contraseña.equalsIgnoreCase("Contraseña");
    }
  /*  private boolean validarCampos() {
        String nombreUsuario = etNameUser.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contraseña = etContraseña.getText().toString().trim();

        return !nombreUsuario.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty();
    }*/
}
