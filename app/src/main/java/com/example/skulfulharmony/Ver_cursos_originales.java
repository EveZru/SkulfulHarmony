package com.example.skulfulharmony;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterClasesOriginales;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursosOriginales;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ver_cursos_originales extends AppCompatActivity implements Serializable{

    private List<Curso> listaCursos;
    private RecyclerView rvCursosOriginales;
    private AdapterClasesOriginales adapterClasesOriginales;
    private ImageView ivRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_cursos_originales);
        rvCursosOriginales = findViewById(R.id.rv_cursos_originales);
        rvCursosOriginales.setLayoutManager(new LinearLayoutManager(this));
        ivRegresar = findViewById(R.id.iv_regresar);

        ivRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Ver_cursos_originales.this, Home.class);
            startActivity(intent);
        });

        listaCursos = new ArrayList<>();
        ArrayList<Curso> listaFundamentos = (ArrayList<Curso>) getIntent().getSerializableExtra("lista_cursos_fundamentos");
        if (listaFundamentos == null) {
            listaFundamentos = new ArrayList<>();
        }

        if (!listaFundamentos.isEmpty()) {
            listaCursos.addAll(listaFundamentos);
        } else {

            Curso curso1 = new Curso("Clave sol", null, null, null, null);
            Curso curso2 = new Curso("Clave fa", null, null, null, null);
            Curso curso3 = new Curso("Clave do", null, null, null, null);
            listaCursos.add(curso1);
            listaCursos.add(curso2);
            listaCursos.add(curso3);
        }

        Log.d("VerCursosOriginales", "Tamaño de listaCursos: " + listaCursos.size());
        if (!listaCursos.isEmpty()) {
            Log.d("VerCursosOriginales", "Título del primer curso: " + listaCursos.get(0).getTitulo());
        }
        adapterClasesOriginales = new AdapterClasesOriginales(this, listaCursos);
        rvCursosOriginales.setAdapter(adapterClasesOriginales);

    }
}