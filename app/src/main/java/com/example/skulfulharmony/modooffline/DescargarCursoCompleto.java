package com.example.skulfulharmony.modooffline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.skulfulharmony.VerCursoDescargado;
import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                            mostrarToast(context, "Error al leer el curso.");
                            callback.onError("Error al leer curso");
                            return;
                        }

                        DbHelper dbHelper = new DbHelper(context);
                        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();

                        // Verificar si ya existe
                        String[] projection = { "id" };
                        String selection = "idCurso = ?";
                        String[] selectionArgs = { String.valueOf(curso.getIdCurso()) };

                        Cursor cursor = sqlDB.query(
                                "cursodescargado",
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                null
                        );

                        if (cursor.moveToFirst()) {
                            cursor.close();
                            mostrarToast(context, "Este curso ya ha sido descargado anteriormente.");
                            callback.onError("Curso ya descargado");
                            return;
                        }
                        cursor.close();

                        // Guardar curso sin clases
                        ContentValues values = new ContentValues();
                        values.put("idCurso", curso.getIdCurso());
                        values.put("titulo", curso.getTitulo());
                        values.put("descripcion", curso.getDescripcion());
                        values.put("imagen", curso.getImagen());

                        long cursoLocalId = sqlDB.insertWithOnConflict(
                                "cursodescargado", null, values, SQLiteDatabase.CONFLICT_IGNORE
                        );

                        if (cursoLocalId == -1) {
                            mostrarToast(context, "Este curso ya está en la base local.");
                            callback.onError("Curso ya descargado (conflicto SQL)");
                        } else {
                            mostrarToast(context, "Curso descargado correctamente.");
                            callback.onFinalizado(curso.getTitulo());
                            lanzarVerCursoDescargado(context, (int) cursoLocalId);
                        }

                    } else {
                        mostrarToast(context, "Curso no encontrado.");
                        callback.onError("Curso no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarToast(context, "Error consultando Firestore.");
                    callback.onError("Error consultando Firestore");
                });
    }

    private static void mostrarToast(Context context, String mensaje) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        );
    }

    private static void lanzarVerCursoDescargado(Context context, int cursoLocalId) {
        Intent intent = new Intent(context, VerCursoDescargado.class);
        intent.putExtra("curso_id", cursoLocalId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}