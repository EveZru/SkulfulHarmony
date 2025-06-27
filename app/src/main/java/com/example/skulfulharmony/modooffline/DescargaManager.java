package com.example.skulfulharmony.modooffline;

import static com.example.skulfulharmony.databaseinfo.DbHelper.convertirPreguntasFirebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.example.skulfulharmony.databaseinfo.DbCourse;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.List;

public class DescargaManager {

    private static final String CHANNEL_ID = "descarga_clases";
    private static final int NOTI_ID = 4000;

    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Descarga de Clases",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notificaciones mientras se descargan las clases");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static void mostrarProgreso(Context context, String titulo, int progreso, int total) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_sh)
                .setContentTitle("Descargando clase")
                .setContentText(titulo)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(total, progreso, false)
                .setOngoing(true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return;

        NotificationManagerCompat.from(context).notify(NOTI_ID, builder.build());
    }

    public static void ocultarProgreso(Context context) {
        NotificationManagerCompat.from(context).cancel(NOTI_ID);
    }

    public static void mostrarFinal(Context context, String curso) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_sh)
                .setContentTitle("✅ Curso descargado")
                .setContentText("Se descargó el curso: " + curso)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return;

        NotificationManagerCompat.from(context).notify(NOTI_ID + 1, builder.build());
    }

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

        crearCanal(context);

        if (!cursoYaDescargado(curso.getIdCurso(), context)) {
            String imagenCursoUrl = curso.getImagen().split("\\?")[0];
            String nombreImagenCurso = imagenCursoUrl.substring(imagenCursoUrl.lastIndexOf("/") + 1);
            String imagenLocal = descargarArchivo(context, curso.getImagen(), nombreImagenCurso);
            curso.setImagen(imagenLocal);
            new DbCourse(context).insertCurso(curso);
        }

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM cursodescargado WHERE idCurso = ?", new String[]{String.valueOf(curso.getIdCurso())});
        int cursoLocalId = -1;
        if (cursor.moveToFirst()) {
            cursoLocalId = cursor.getInt(0);
            Log.d("DESCARGA", "📥 ID del curso local recuperado: " + cursoLocalId);
        } else {
            Log.e("DESCARGA", "❌ No se encontró el curso localmente.");
        }
        cursor.close();
        db.close();

        if (cursoLocalId == -1) return;

        for (int i = 0; i < clasesFirestore.size(); i++) {
            Clase clase = clasesFirestore.get(i);
            if (clase == null) continue;

            if (dbHelper.claseYaDescargadaConIdLocal(clase.getTitulo(), cursoLocalId)) {
                Log.d("DESCARGA", "⏩ Clase ya descargada, se omite: " + clase.getTitulo());
                continue;
            }

            mostrarProgreso(context, clase.getTitulo(), i + 1, clasesFirestore.size());

            ClaseFirebase claseFirebase = new ClaseFirebase(
                    clase.getTitulo(),
                    clase.getArchivos(),
                    clase.getImagen(),
                    clase.getVideoUrl()
            );

            // ✅ Convertir preguntas si vienen como Map

            if (clase.getPreguntas() != null && !clase.getPreguntas().isEmpty()) {
                Object primera = clase.getPreguntas().get(0);

                if (primera instanceof Map) {
                    Log.d("DESCARGA", "🔄 Convirtiendo preguntas desde Map...");
                    List<Map<String, Object>> listaMap = (List<Map<String, Object>>) (List<?>) clase.getPreguntas();
                    List<PreguntaCuestionario> convertidas = convertirPreguntasFirebase(listaMap);

                    // 🧪 Log de cada pregunta
                    for (PreguntaCuestionario p : convertidas) {
                        Log.d("DESCARGA", "🎯 Pregunta convertida: " + p.getPregunta());
                    }

                    claseFirebase.setPreguntas(convertidas);

                } else if (primera instanceof PreguntaCuestionario) {
                    Log.d("DESCARGA", "✅ Preguntas ya son tipo PreguntaCuestionario");
                    List<PreguntaCuestionario> preguntasOriginales = (List<PreguntaCuestionario>) clase.getPreguntas();

                    for (PreguntaCuestionario p : preguntasOriginales) {
                        Log.d("DESCARGA", "🎯 Pregunta original: " + p.getPregunta());
                    }

                    claseFirebase.setPreguntas(preguntasOriginales);
                } else {
                    Log.w("DESCARGA", "❌ Tipo de pregunta desconocido: " + primera.getClass().getName());
                }

                Log.d("DESCARGA", "🧪 Total preguntas convertidas: " +
                        (claseFirebase.getPreguntas() != null ? claseFirebase.getPreguntas().size() : 0));
            } else {
                    Log.d("DESCARGA", "ℹ No hay preguntas en esta clase");
            }

            // Video
            String videoUrl = clase.getVideoUrl().split("\\?")[0];
            String nombreVideo = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
            String videoLocal = descargarArchivo(context, clase.getVideoUrl(), nombreVideo);

            // Imagen
            String imagenUrl = clase.getImagen().split("\\?")[0];
            String nombreImagen = imagenUrl.substring(imagenUrl.lastIndexOf("/") + 1);
            String imagenLocal = descargarArchivo(context, clase.getImagen(), nombreImagen);

            // Archivos
            List<String> archivosLocales = new ArrayList<>();
            for (String url : clase.getArchivos()) {
                if (url == null || url.isEmpty()) continue;
                String cleanUrl = url.split("\\?")[0];
                String nombreArchivo = cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1);
                if (nombreArchivo.isEmpty()) {
                    nombreArchivo = "archivo_generico_" + archivosLocales.size() + ".bin";
                }
                String archivoLocal = descargarArchivo(context, url, nombreArchivo);
                if (archivoLocal != null) archivosLocales.add(archivoLocal);
            }

            claseFirebase.setVideoUrl(videoLocal);
            claseFirebase.setImagenUrl(imagenLocal);
            claseFirebase.setArchivosUrl(archivosLocales);

            dbHelper.guardarClaseDescargada(claseFirebase, cursoLocalId);
        }

        ocultarProgreso(context);
        mostrarFinal(context, curso.getTitulo());
        Log.d("DESCARGA", "✅ Descarga finalizada de curso + clases.");
    }


}