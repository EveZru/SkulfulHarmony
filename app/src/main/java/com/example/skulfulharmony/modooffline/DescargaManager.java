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
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultado.get();
    }

    public static void descargarCursoYClases(Curso curso, List<Clase> clasesFirestore, Context context) {
        Log.d("DESCARGA", "⏬ Iniciando descarga COMPLETA de: " + curso.getTitulo());

        if (cursoYaDescargado(curso.getIdCurso(), context)) {
            Log.d("DESCARGA", "⛔ Curso ya descargado. Se usará el ID existente.");
        } else {
            String imagenLocal = descargarArchivo(context, curso.getImagen(), "curso_" + curso.getIdCurso() + "_img.jpg");
            curso.setImagen(imagenLocal);
            DbCourse dbCourse = new DbCourse(context);
            dbCourse.insertCurso(curso);
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

        for (Clase clase : clasesFirestore) {
            if (clase == null) continue;

            Log.d("DESCARGA", "🔁 Convirtiendo clase: " + clase.getTitulo());

            ClaseFirebase claseFirebase = new ClaseFirebase(
                    clase.getTitulo(),
                    clase.getArchivos(),
                    clase.getImagen(),
                    clase.getVideoUrl()
            );

            // ✅ Agregar preguntas si existen
            if (clase.getPreguntas() != null && !clase.getPreguntas().isEmpty()) {
                Log.d("DESCARGA", "📘 Preguntas encontradas: " + clase.getPreguntas().size());
                claseFirebase.setPreguntas(clase.getPreguntas());
            } else {
                Log.w("DESCARGA", "📭 Clase sin preguntas: " + clase.getTitulo());
            }

            String titulo = clase.getTitulo().replaceAll("[^a-zA-Z0-9]", "_");
            Log.d("DESCARGA", "🔽 Descargando clase: " + clase.getTitulo());

            String videoLocal = descargarArchivo(context, clase.getVideoUrl(), "video_" + titulo + ".mp4");
            String imgLocal = descargarArchivo(context, clase.getImagen(), "img_" + titulo + ".jpg");

            List<String> archivosLocales = new ArrayList<>();
            for (String url : clase.getArchivos()) {
                // Obtener extensión real desde el link limpio (sin parámetros)
                String cleanUrl = url.split("\\?")[0];
                String extension = ".bin";
                int lastDot = cleanUrl.lastIndexOf(".");
                if (lastDot != -1 && lastDot < cleanUrl.length() - 1) {
                    extension = cleanUrl.substring(lastDot);
                }

                String nombreArchivo = "archivo_" + titulo + "_" + archivosLocales.size() + extension;
                String archivoLocal = descargarArchivo(context, url, nombreArchivo);
                if (archivoLocal != null) archivosLocales.add(archivoLocal);
            }

            claseFirebase.setVideoUrl(videoLocal);
            claseFirebase.setImagenUrl(imgLocal);
            claseFirebase.setArchivosUrl(archivosLocales);

            dbHelper.guardarClaseDescargada(claseFirebase, cursoLocalId);
            Log.d("DESCARGA", "💾 Clase guardada en BD local.");
        }

        Log.d("DESCARGA", "✅ Descarga finalizada de curso + clases.");
    }
}