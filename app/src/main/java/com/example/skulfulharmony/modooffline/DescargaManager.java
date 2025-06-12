package com.example.skulfulharmony.modooffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.skulfulharmony.databaseinfo.DbCourse;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.List;

public class DescargaManager {

    public static boolean cursoYaDescargado(int idCurso, Context context) {
        Log.d("DESCARGA", "🔍 Verificando si el curso ID " + idCurso + " ya está descargado...");
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM cursodescargado WHERE idCurso = ?", new String[]{String.valueOf(idCurso)});
        boolean existe = cursor.moveToFirst();
        Log.d("DESCARGA", existe ? "✅ Ya estaba descargado." : "🆕 No se ha descargado antes.");
        cursor.close();
        db.close();
        return existe;
    }

    public static String descargarArchivo(Context context, String url, String nombreArchivo) {
        if (url == null || url.isEmpty()) {
            Log.w("DESCARGA", "⚠ URL vacía o nula para archivo: " + nombreArchivo);
            return null;
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> resultado = new AtomicReference<>(null);

        DropboxDownloader.descargarArchivo(context, url, nombreArchivo, new DropboxDownloader.Callback() {
            @Override
            public void onSuccess(File archivoLocal) {
                Log.d("DESCARGA", "✅ Archivo descargado: " + archivoLocal.getAbsolutePath());
                resultado.set(archivoLocal.getAbsolutePath());
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                Log.e("DESCARGA", "❌ Error al descargar: " + url, e);
                latch.countDown();
            }
        });

        try {
            latch.await(); // Espera hasta que la descarga finalice
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultado.get();
    }

    public static void descargarCursoYClases(Curso curso, List<Clase> clasesFirestore, Context context) {
        Log.d("DESCARGA", "⏬ Iniciando descarga COMPLETA de: " + curso.getTitulo());

        if (cursoYaDescargado(curso.getIdCurso(), context)) {
            Log.d("DESCARGA", "⛔ Curso ya descargado. No se repite.");
            return;
        }

        String imagenLocal = descargarArchivo(context, curso.getImagen(), "curso_" + curso.getIdCurso() + "_img.jpg");
        Log.d("DESCARGA", "🖼 Imagen descargada: " + imagenLocal);
        curso.setImagen(imagenLocal);

        DbCourse dbCourse = new DbCourse(context);
        long idInsertado = dbCourse.insertCurso(curso);
        Log.d("DESCARGA", "📥 Curso guardado con ID SQLite: " + idInsertado);

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM cursodescargado WHERE idCurso = ?", new String[]{String.valueOf(curso.getIdCurso())});
        int cursoLocalId = -1;
        if (cursor.moveToFirst()) {
            cursoLocalId = cursor.getInt(0);
            Log.d("DESCARGA", "📌 ID interno del curso: " + cursoLocalId);
        } else {
            Log.e("DESCARGA", "❌ No se encontró el curso localmente.");
        }
        cursor.close();
        db.close();

        if (cursoLocalId == -1) return;

        Log.d("DESCARGA", "📚 Clases detectadas en Firestore: " + clasesFirestore.size());

        List<ClaseFirebase> clases = new ArrayList<>();

        for (Clase clase : clasesFirestore) {
            if (clase == null) continue;

            Log.d("DESCARGA", "🔁 Convirtiendo clase: " + clase.getTitulo());

            ClaseFirebase claseFirebase = new ClaseFirebase(
                    clase.getTitulo(),
                    clase.getContenido(), // o clase.getTextos() si tu campo PDF es ese
                    clase.getImagen(),
                    clase.getVideoUrl()
            );
            clases.add(claseFirebase);
        }

        Log.d("DESCARGA", "🎯 Clases convertidas listas para descarga: " + clases.size());

        for (ClaseFirebase clase : clases) {
            String titulo = clase.getTitulo().replaceAll("[^a-zA-Z0-9]", "_");
            Log.d("DESCARGA", "🔽 Descargando clase: " + clase.getTitulo());

            String videoLocal = descargarArchivo(context, clase.getVideoUrl(), "video_" + titulo + ".mp4");
            String docLocal = descargarArchivo(context, clase.getDocumentoUrl(), "doc_" + titulo + ".pdf");
            String imgLocal = descargarArchivo(context, clase.getImagenUrl(), "img_" + titulo + ".jpg");

            Log.d("DESCARGA", "📦 Clase descargada:\n - Video: " + videoLocal + "\n - Doc: " + docLocal + "\n - Img: " + imgLocal);

            clase.setVideoUrl(videoLocal);
            clase.setDocumentoUrl(docLocal);
            clase.setImagenUrl(imgLocal);

            dbHelper.guardarClaseDescargada(clase, cursoLocalId);
            Log.d("DESCARGA", "💾 Clase guardada en BD local.");
        }

        Log.d("DESCARGA", "✅ Descarga finalizada de curso + clases.");
    }
}