package com.example.skulfulharmony.databaseinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbCourse extends DbHelper {

    private Context context;

    public DbCourse(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    // Insertar un curso en la base de datos
    public long insertCurso(Curso curso) {
        long id = -1;
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("titulo", curso.getTitulo());
            values.put("descripcion", curso.getDescripcion());
            values.put("imagen", curso.getImagen());

            id = database.insert(DbHelper.TABLE_COURSE, null, values);
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // Obtener todos los cursos descargados
    public List<Curso> getCursosDescargados() {
        List<Curso> listaCursos = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DbHelper.TABLE_COURSE, null);

        if (cursor.moveToFirst()) {
            do {
                int idCurso = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
                long fechaMillis = cursor.getLong(cursor.getColumnIndexOrThrow("fecha_descarga"));
                Date fechaDescarga = new Date(fechaMillis);

                Curso curso = new Curso(idCurso, imagen, titulo, fechaDescarga);
                curso.setDescripcion(descripcion);
                listaCursos.add(curso);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return listaCursos;
    }

    // Obtener un curso por ID
    public Curso getCursoById(int idCurso) {
        SQLiteDatabase database = this.getReadableDatabase();
        Curso curso = null;

        Cursor cursor = database.rawQuery("SELECT * FROM " + DbHelper.TABLE_COURSE + " WHERE id = ?", new String[]{String.valueOf(idCurso)});

        if (cursor.moveToFirst()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
            long fechaMillis = cursor.getLong(cursor.getColumnIndexOrThrow("fecha_descarga"));
            Date fechaDescarga = new Date(fechaMillis);

            curso = new Curso(idCurso, imagen, titulo, fechaDescarga);
            curso.setDescripcion(descripcion);
        }

        cursor.close();
        database.close();
        return curso;
    }

    // Eliminar un curso por ID
    public boolean deleteCurso(int idCurso) {
        SQLiteDatabase database = this.getWritableDatabase();
        int rowsDeleted = database.delete(DbHelper.TABLE_COURSE, "id = ?", new String[]{String.valueOf(idCurso)});
        database.close();
        return rowsDeleted > 0;
    }
}
