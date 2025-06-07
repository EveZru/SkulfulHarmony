package com.example.skulfulharmony.databaseinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.modooffline.ClaseFirebase;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "localdata.db";
    public static final String TABLE_USER = "usuario";
    public static final String TABLE_COURSE = "cursodescargado";
    public static final String TABLE_CLASS = "clasedescargada";
    public static final String TABLE_QUESTION = "pregunta";
    public static final String TABLE_OPTIONS = "respuesta";
    public static final String TABLE_PROGRESS = "progreso";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                "correo TEXT PRIMARY KEY, " +
                "nombre TEXT NOT NULL, " +
                "imagen TEXT, " +
                "ultimo_acceso DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "instrumento_principal TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_COURSE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "imagen TEXT, " +
                "fecha_descarga DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_CLASS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "curso_id INTEGER NOT NULL, " +
                "titulo TEXT NOT NULL, " +
                "textos TEXT, " +
                "imagen TEXT, " +
                "video TEXT, " +
                "FOREIGN KEY (curso_id) REFERENCES " + TABLE_COURSE + "(id) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_QUESTION + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "clase_id INTEGER NOT NULL, " +
                "pregunta TEXT NOT NULL, " +
                "respuesta_correcta TEXT NOT NULL, " +
                "respuesta_usuario TEXT, " +
                "es_pregunta_inicio INTEGER NOT NULL DEFAULT 0, " +
                "fecha DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (clase_id) REFERENCES " + TABLE_CLASS + "(id) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_OPTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pregunta_id INTEGER NOT NULL, " +
                "opcion TEXT NOT NULL, " +
                "FOREIGN KEY (pregunta_id) REFERENCES " + TABLE_QUESTION + "(id) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_PROGRESS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "curso_id INTEGER NOT NULL, " +
                "indice_progreso REAL DEFAULT 0, " +
                "respuestas_correctas INTEGER DEFAULT 0, " +
                "respuestas_incorrectas INTEGER DEFAULT 0, " +
                "FOREIGN KEY (curso_id) REFERENCES " + TABLE_COURSE + "(id) ON DELETE CASCADE" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void guardarCursoDescargado(Curso curso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("titulo", curso.getTitulo());
        values.put("descripcion", curso.getDescripcion());
        values.put("imagen", curso.getImagen());
        db.insert(TABLE_COURSE, null, values);
    }

    public void guardarClaseDescargada(ClaseFirebase clase, int cursoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("curso_id", cursoId);
        values.put("titulo", clase.getTitulo());
        values.put("documento", clase.getDocumentoUrl());
        values.put("imagen", clase.getImagenUrl());
        values.put("video", clase.getVideoUrl());
        db.insert(TABLE_CLASS, null, values);
    }

    public List<Curso> obtenerCursosDescargados() {
        List<Curso> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, titulo, descripcion, imagen FROM " + TABLE_COURSE, null);

        if (cursor.moveToFirst()) {
            do {
                Curso curso = new Curso();
                curso.setId(cursor.getInt(0));               // 🔥 Aquí seteas el ID
                curso.setTitulo(cursor.getString(1));
                curso.setDescripcion(cursor.getString(2));
                curso.setImagen(cursor.getString(3));
                lista.add(curso);
            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        return lista;
    }

    public Curso obtenerCursoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo, descripcion, imagen FROM " + TABLE_COURSE + " WHERE id = ?", new String[]{String.valueOf(id)});

        Curso curso = null;
        if (cursor.moveToFirst()) {
            curso = new Curso();
            curso.setTitulo(cursor.getString(0));
            curso.setDescripcion(cursor.getString(1));
            curso.setImagen(cursor.getString(2));
        }

        cursor.close();
        db.close();
        return curso;
    }

    public List<ClaseFirebase> obtenerClasesPorCurso(int cursoId) {
        List<ClaseFirebase> clases = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo, textos, imagen, video FROM " + TABLE_CLASS + " WHERE curso_id = ?", new String[]{String.valueOf(cursoId)});

        if (cursor.moveToFirst()) {
            do {
                ClaseFirebase clase = new ClaseFirebase(
                        cursor.getString(0),
                        cursor.getString(1), // documento
                        cursor.getString(2), // imagen
                        cursor.getString(3)  // video
                );
                clases.add(clase);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return clases;
    }
}