package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasDeClasesOriginales;

public class ClasesOriginales extends AppCompatActivity {
    private ImageView iv_infografia;
    private RecyclerView rv_targPreguntas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        init();
        setContentView(R.layout.activity_clases_originales);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
    RecyclerView recyclerView = findViewById(R.id.rv_preguntasclaseoriginal);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    AdapterPreguntasDeClasesOriginales adapter = new AdapterPreguntasDeClasesOriginales(listaDePreguntas);
    recyclerView.setAdapter(adapter);
    }



}