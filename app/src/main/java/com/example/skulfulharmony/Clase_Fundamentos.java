package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasEnCuestionariosparaContestar;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clase_Fundamentos extends AppCompatActivity {
    private ImageView ivImagenCurso;
    private RecyclerView rvPreguntasCurso;
    private AdapterPreguntasEnCuestionariosparaContestar adapterPreguntas;
    private Button btnComprobar;
    private Button btnReintentar;
    private List<PreguntaCuestionario> listaPreguntas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clase_fundamentos);


        ivImagenCurso = findViewById(R.id.iv_imagen_curso);
        rvPreguntasCurso = findViewById(R.id.rv_preguntas_curso);
        rvPreguntasCurso.setLayoutManager(new LinearLayoutManager(this));
        btnComprobar = findViewById(R.id.btn_comprobar);
        btnReintentar = findViewById(R.id.btn_reintentar);

        listaPreguntas = (List<PreguntaCuestionario>) getIntent().getSerializableExtra("lista_preguntas");
        String nombreCurso = getIntent().getStringExtra("nombre_curso");

        adapterPreguntas = new  AdapterPreguntasEnCuestionariosparaContestar(this, listaPreguntas);

        int imagenResId = obtenerImagenPorCurso(nombreCurso);
        ivImagenCurso.setImageResource(imagenResId);

        rvPreguntasCurso.setAdapter(adapterPreguntas);
//botones para comprobar respuestas
        btnComprobar.setOnClickListener(v -> {
            adapterPreguntas.comprobarRespuestas();
            List<PreguntaCuestionario> incorrectas = adapterPreguntas.getPreguntasIncorrectas();
            Toast.makeText(this, "Incorrectas: " + incorrectas.size(), Toast.LENGTH_SHORT).show();
            btnComprobar.setVisibility(View.GONE);
            btnReintentar.setVisibility(View.VISIBLE);
        });

        btnReintentar.setOnClickListener(v -> {
            // Aquí puedes implementar la lógica para reintentar el cuestionario
            // Por ejemplo, podrías recrear la Activity o resetear el adapter
            Toast.makeText(this, "Reintentando cuestionario...", Toast.LENGTH_SHORT).show();
            btnComprobar.setVisibility(View.VISIBLE);
            btnReintentar.setVisibility(View.GONE);
            adapterPreguntas.setMostrarResultados(false);
            // Para resetear las selecciones,
            // adapterPreguntas.resetSelecciones();
            adapterPreguntas.notifyDataSetChanged();
        });
    }

    private int obtenerImagenPorCurso(String tituloCurso) {
        switch (tituloCurso) {

            case "Curso 1":
                return R.drawable.curso_1;
            case "Curso 2":
                return R.drawable.curso_2;
            case "Curso 3":
                return R.drawable.curso_3;
            case"Curso 4":
                return R.drawable.curso_4;
            case "Curso 5":
                return R.drawable.curso_5;
            case "Curso 6":
                return R.drawable.curso_6;
            default:
                return R.drawable.img_background;
        }
    }
}