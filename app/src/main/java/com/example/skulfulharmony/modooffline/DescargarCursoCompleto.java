package com.example.skulfulharmony.modooffline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.skulfulharmony.VerCursoDescargado;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class DescargarCursoCompleto {

    public interface Callback {
        void onFinalizado(String titulo);
        void onError(String mensaje);
    }

    public static void descargar(Context context, int idCurso, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cursos")
                .whereEqualTo("idCurso", idCurso)
                .limit(1)
                .get()
                .addOnSuccessListener(cursoQuery -> {
                    if (!cursoQuery.isEmpty()) {
                        DocumentSnapshot cursoDoc = cursoQuery.getDocuments().get(0);
                        Curso curso = cursoDoc.toObject(Curso.class);
                        if (curso == null) {
                            callback.onError("Error al leer curso");
                            return;
                        }

                        DbHelper dbHelper = new DbHelper(context);
                        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();

                        // Guardar curso
                        ContentValues values = new ContentValues();
                        values.put("titulo", curso.getTitulo());
                        values.put("descripcion", curso.getDescripcion());
                        values.put("imagen", curso.getImagen());
                        long cursoLocalId = sqlDB.insert("cursodescargado", null, values);

                        if (cursoLocalId == -1) {
                            callback.onError("Error guardando curso local");
                            return;
                        }

                        db.collection("clases")
                                .whereEqualTo("idCurso", idCurso)
                                .get()
                                .addOnSuccessListener(clasesQuery -> {
                                    List<DocumentSnapshot> documentos = clasesQuery.getDocuments();
                                    if (documentos.isEmpty()) {
                                        callback.onFinalizado(curso.getTitulo());

                                        // Lanzar VerCursoDescargado aunque no haya clases
                                        lanzarVerCursoDescargado(context, (int) cursoLocalId);
                                        return;
                                    }

                                    final int total = documentos.size();
                                    final int[] contador = {0};

                                    for (DocumentSnapshot claseDoc : documentos) {
                                        Clase clase = claseDoc.toObject(Clase.class);
                                        if (clase == null) continue;

                                        ClaseFirebase claseFirebase = new ClaseFirebase(
                                                clase.getTitulo(),
                                                clase.getContenido(),
                                                null,
                                                null
                                        );

                                        String tituloArchivo = clase.getTitulo().replace(" ", "_");

                                        DropboxDownloader.descargarArchivo(context, clase.getImagen(), "img_" + tituloArchivo + ".jpg", new DropboxDownloader.Callback() {
                                            @Override
                                            public void onSuccess(File imagenLocal) {
                                                claseFirebase.setImagenUrl(imagenLocal.getAbsolutePath());

                                                DropboxDownloader.descargarArchivo(context, clase.getVideoUrl(), "video_" + tituloArchivo + ".mp4", new DropboxDownloader.Callback() {
                                                    @Override
                                                    public void onSuccess(File videoLocal) {
                                                        claseFirebase.setVideoUrl(videoLocal.getAbsolutePath());

                                                        dbHelper.guardarClaseDescargada(claseFirebase, (int) cursoLocalId);

                                                        contador[0]++;
                                                        if (contador[0] == total) {
                                                            callback.onFinalizado(curso.getTitulo());

                                                            // ✅ Lanzar VerCursoDescargado cuando termina todo
                                                            lanzarVerCursoDescargado(context, (int) cursoLocalId);
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        callback.onError("Error al descargar video");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                callback.onError("Error al descargar imagen");
                                            }
                                        });
                                    }

                                })
                                .addOnFailureListener(e -> callback.onError("Error al obtener clases"));

                    } else {
                        callback.onError("Curso no encontrado");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error consultando Firestore"));
    }

    private static void lanzarVerCursoDescargado(Context context, int cursoLocalId) {
        Intent intent = new Intent(context, VerCursoDescargado.class);
        intent.putExtra("curso_id", cursoLocalId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario si llamas desde contexto no-Activity
        context.startActivity(intent);
    }
}