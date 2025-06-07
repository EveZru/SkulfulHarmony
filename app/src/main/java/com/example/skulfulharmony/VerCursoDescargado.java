package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterClasesLocales;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.modooffline.ClaseFirebase;

import java.util.ArrayList;
import java.util.List;

public class VerCursoDescargado extends AppCompatActivity {

    private TextView titulo, descripcion, autor;
    private ImageView imagen;
    private RecyclerView rvClases;
    private AdapterClasesLocales adapter;
    private List<ClaseFirebase> clases = new ArrayList<>();

    private int cursoId; // lo recibes por Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_curso_descargado);

        titulo = findViewById(R.id.tv_titulo_curso_descargado);
        descripcion = findViewById(R.id.tv_descripcion_curso_descargado);
        autor = findViewById(R.id.tv_autor_curso_descargado);
        imagen = findViewById(R.id.img_curso_descargado);
        rvClases = findViewById(R.id.rv_clases_descargadas);
        rvClases.setLayoutManager(new LinearLayoutManager(this));

        cursoId = getIntent().getIntExtra("curso_id", -1);
        if (cursoId != -1) {
            cargarCursoYClases();
        }
    }

    private void cargarCursoYClases() {
        DbHelper db = new DbHelper(this);
        Curso curso = db.obtenerCursoPorId(cursoId);
        clases = db.obtenerClasesPorCurso(cursoId);

        if (curso != null) {
            titulo.setText(curso.getTitulo());
            descripcion.setText(curso.getDescripcion());
            autor.setText("Guardado localmente");

            Glide.with(this)
                    .load(curso.getImagen())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.img_defaultclass)
                    .into(imagen);
        }

        adapter = new AdapterClasesLocales(clases, clase -> {
            Intent intent = new Intent(VerCursoDescargado.this, VerClaseOffline.class);
            intent.putExtra("titulo", clase.getTitulo());
            intent.putExtra("documento", clase.getDocumentoUrl());
            intent.putExtra("imagen", clase.getImagenUrl());
            intent.putExtra("video", clase.getVideoUrl());
            startActivity(intent);
        });

        rvClases.setAdapter(adapter);
    }
}