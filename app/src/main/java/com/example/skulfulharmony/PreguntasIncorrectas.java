package com.example.skulfulharmony;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class PreguntasIncorrectas extends AppCompatActivity {
    private RecyclerView rvPreguntasIncorrectas;
    private TextView tvPreguntasIncorrectas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_incorrectas);

        tvPreguntasIncorrectas = findViewById(R.id.tv_preguntas_incorrectas);

        // Cargar preguntas desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("cuestionario", MODE_PRIVATE);
        String json = prefs.getString("preguntas_incorrectas", null);

        if (json != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> preguntasIncorrectas = gson.fromJson(json, listType);

            StringBuilder texto = new StringBuilder();
            for (String item : preguntasIncorrectas) {
                texto.append(item).append("\n\n");
            }

            tvPreguntasIncorrectas.setText(texto.toString());
        } else {
            tvPreguntasIncorrectas.setText("No hay preguntas incorrectas registradas.");
        }
    }
}