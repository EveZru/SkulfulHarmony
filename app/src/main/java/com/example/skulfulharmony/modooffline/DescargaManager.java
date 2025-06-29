package com.example.skulfulharmony.modooffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import com.example.skulfulharmony.databaseinfo.DbHelper;

public class  DescargaManager {

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

    public static boolean claseYaDescargada(Context context, String tituloClase, int cursoIdLocal) {
        Log.d("DESCARGA", "🔍 Verificando si la clase '" + tituloClase + "' ya fue descargada en el curso local ID: " + cursoIdLocal);
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String tituloNormalizado = tituloClase.trim().toLowerCase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM clasedescargada WHERE LOWER(TRIM(titulo)) = ? AND curso_id = ?",
                new String[]{tituloNormalizado, String.valueOf(cursoIdLocal)}
        );

        boolean yaDescargada = cursor.moveToFirst();
        Log.d("DESCARGA", yaDescargada ? "✅ Clase ya estaba descargada." : "🆕 Clase nueva, se descargará.");

        cursor.close();
        db.close();
        return yaDescargada;
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

}