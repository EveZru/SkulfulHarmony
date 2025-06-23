package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditarClase extends AppCompatActivity {
    private Button btn_cambiar, btn_cancelar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_clase);

        btn_cancelar = findViewById(R.id.btn_cancelar);


        btn_cancelar.setOnClickListener(v -> {
            finish();
        });

    }
}