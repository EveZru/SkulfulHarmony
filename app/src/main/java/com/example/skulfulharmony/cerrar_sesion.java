package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class cerrar_sesion extends AppCompatActivity {
    private Button cancelar_cerrarsesion,cerrarsesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        cancelar_cerrarsesion=findViewById(R.id.btncancelar_cerrarsesion);
        cerrarsesion=findViewById(R.id.btncancelar_cerrarsesion);


        setContentView(R.layout.activity_cerrar_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cancelar_cerrarsesion.setOnClickListener(view->{}
        );
        cerrarsesion.setOnClickListener(view->{}
        );

    }
}