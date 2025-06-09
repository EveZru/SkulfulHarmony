package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterClasesLocales;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.modooffline.ClaseFirebase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VerCursoDescargado extends AppCompatActivity {

    private TextView titulo, descripcion, autor;
    private ImageView imagen, ivMenu;
    private RecyclerView rvClases;
    private AdapterClasesLocales adapter;
    private List<ClaseFirebase> clases = new ArrayList<>();
    private int cursoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_curso_descargado);

        titulo = findViewById(R.id.tv_titulo_curso_descargado);
        descripcion = findViewById(R.id.tv_descripcion_curso_descargado);
        autor = findViewById(R.id.tv_autor_curso_descargado);
        imagen = findViewById(R.id.img_curso_descargado);
        ivMenu = findViewById(R.id.iv_menu_curso_descargado);
        rvClases = findViewById(R.id.rv_clases_descargadas);
        rvClases.setLayoutManager(new LinearLayoutManager(this));

        cursoId = getIntent().getIntExtra("curso_id", -1);
        if (cursoId != -1) {
            cargarCursoYClases();
        }

        ivMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_curso_descargado, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.it_borrar_curso) {
                    eliminarCursoDescargado();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
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

    private void eliminarCursoDescargado() {
        DbHelper db = new DbHelper(this);

        // Elimina los archivos de cada clase
        for (ClaseFirebase clase : clases) {
            eliminarArchivo(clase.getImagenUrl());
            eliminarArchivo(clase.getVideoUrl());
            eliminarArchivo(clase.getDocumentoUrl());
        }

        // Elimina de la base de datos local
        db.eliminarCursoYClasesPorId(cursoId);

        Toast.makeText(this, "Curso eliminado correctamente", Toast.LENGTH_SHORT).show();

        // Marcar resultado para que Biblioteca sepa que refresque
        setResult(RESULT_OK);
        finish();
    }

    private void eliminarArchivo(String ruta) {
        if (ruta != null) {
            File archivo = new File(ruta);
            if (archivo.exists()) archivo.delete();
        }
    }
}