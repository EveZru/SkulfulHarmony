package com.example.skulfulharmony;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasEnClasesOriginales;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClasesOriginales extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clases_originales);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //------------------------------------------ esto es para probar que funcionan las preguntasss localmente falta que se conecten a la bd y consigan la info
        List<PreguntaCuestionario> listaPreguntas = new ArrayList<>();
        listaPreguntas.add(new PreguntaCuestionario("¿Cuál es la capital de Francia?",
                new ArrayList<>(Arrays.asList("París", "Madrid", "Berlín")), 0));

        listaPreguntas.add(new PreguntaCuestionario("¿Cuántos continentes hay?",
                new ArrayList<>(Arrays.asList("4", "6")), 1));

        listaPreguntas.add(new PreguntaCuestionario("¿Cuáles son colores primarios?",
                new ArrayList<>(Arrays.asList("Rojo", "Verde", "Azul", "Amarillo")), 0));

        RecyclerView recyclerView = findViewById(R.id.rv_preguntas_claseoriginal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterPreguntasEnClasesOriginales adapter = new AdapterPreguntasEnClasesOriginales(listaPreguntas, this);
        recyclerView.setAdapter(adapter);



    }
}