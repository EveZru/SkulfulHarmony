//package com.example.skulfulharmony.modooffline;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.skulfulharmony.R;
//import com.example.skulfulharmony.VerClaseOffline;
//import com.example.skulfulharmony.adapters.AdapterClasesLocales;
//import com.example.skulfulharmony.databaseinfo.DbHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ClasesDescargadas extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private AdapterClasesLocales adapter;
//    private List<ClaseFirebase> clasesLocales = new ArrayList<>();
//    private String cursoId = "ID_DEL_CURSO"; // Reemplaza con el ID real
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_clases_descargadas);
//
//        recyclerView = findViewById(R.id.recyclerViewLocales);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        cargarDesdeSQLite();
//    }
//
//    private void cargarDesdeSQLite() {
//        SQLiteDatabase db = new DbHelper(this).getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT titulo, textos, imagen, video FROM clasedescargada WHERE curso_id = ?",
//                new String[]{cursoId});
//
//        if (cursor.moveToFirst()) {
//            do {
//                String titulo = cursor.getString(0);
//                String documento = cursor.getString(1);
//                String imagen = cursor.getString(2);
//                String video = cursor.getString(3);
//
//                clasesLocales.add(new ClaseFirebase(titulo, documento, imagen, video));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//
//        // Configura el adaptador con el listener lambda
//        adapter = new AdapterClasesLocales(clasesLocales, clase -> {
//            Intent intent = new Intent(this, VerClaseOffline.class);
//            intent.putExtra("titulo", clase.getTitulo());
//            intent.putExtra("documento", clase.getDocumentoUrl());
//            intent.putExtra("imagen", clase.getImagenUrl());
//            intent.putExtra("video", clase.getVideoUrl());
//            startActivity(intent);
//        });
//
//        recyclerView.setAdapter(adapter);
//    }
//}