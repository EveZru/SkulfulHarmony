package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class RecuperarContr extends AppCompatActivity {
    private Button btnrecuperar, btncancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contr);

        btnrecuperar = findViewById(R.id.btnrecuperarCont);
        btncancelar = findViewById(R.id.btncancelar_recuperar);

        // Acción del botón "Recuperar contraseña"
        btnrecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecuperarContr.this, "Te mandamos un correo", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción del botón "Cancelar"
        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
}