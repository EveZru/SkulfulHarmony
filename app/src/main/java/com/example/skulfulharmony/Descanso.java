package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class Descanso extends AppCompatActivity {
    private Chronometer cronometro;
    private TextView tv_mensaje;
    private String mensajes[]={"charchas bien","puto el que se rinda","5 comentarios "};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
         tv_mensaje=findViewById(R.id.tv_mensajebonito);

         Random random =new Random();
         int ifrase=random.nextInt(mensajes.length);
         String tv_mensaje=mensajes[ifrase];


        setContentView(R.layout.activity_descanso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}