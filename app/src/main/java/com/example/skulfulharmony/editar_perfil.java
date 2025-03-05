package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class editar_perfil extends AppCompatActivity {
    private EditText et_nuevoNombre, et_nuevaDescripcion;
    private Button btn_actualizar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);

        et_nuevoNombre=findViewById(R.id.et_nombrecambiar);
        et_nuevaDescripcion=findViewById(R.id.et_nuevadescripcion);
       // et_nuevaDescripcion=findViewById(R.id.et_nuevadescripcion);
        btn_actualizar=findViewById(R.id.btn_cambiardatos);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btn_actualizar.setOnClickListener(v->{
            //cambiar info jsjs
        });
    }
}