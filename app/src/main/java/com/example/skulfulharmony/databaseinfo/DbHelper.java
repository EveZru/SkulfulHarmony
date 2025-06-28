package com.example.skulfulharmony.databaseinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.modooffline.ClaseFirebase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5; // Incrementado de 4 a 5
    protected static final String DATABASE_NAME = "localdata.db";
    public static final String TABLE_USER = "usuario";
    public static final String TABLE_COURSE = "cursodescargado";
    public static final String TABLE_CLASS = "clasedescargada";
    public static final String TABLE_QUESTION = "preguntas_locales";

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
                "instrumento_principal TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_COURSE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idCurso INTEGER UNIQUE, " +
                "titulo TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "imagen TEXT, " +
                "fecha_descarga DATETIME DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + TABLE_CLASS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "curso_id INTEGER NOT NULL, " +
                "titulo TEXT NOT NULL, " +
                "documento TEXT, " +
                "imagen TEXT, " +
                "video TEXT, " +
                "completada INTEGER DEFAULT 0, " +  // <-- CAMPO NUEVO
                "FOREIGN KEY (curso_id) REFERENCES " + TABLE_COURSE + "(id) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + TABLE_QUESTION + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_clase TEXT NOT NULL, " +
                "pregunta TEXT NOT NULL, " +
                "opciones TEXT NOT NULL, " +
                "respuesta_correcta INTEGER NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS preguntasdescargadas (" +
                "idPregunta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tituloClase TEXT NOT NULL, " +
                "respuestaUsuario INTEGER NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS progreso_clase_offline (" +
                "tituloClase TEXT PRIMARY KEY, " +
                "tiempoVisto INTEGER DEFAULT 0, " +
                "cuestionarioCompletado INTEGER DEFAULT 0);");

        // Nuevo índice para mejorar búsqueda de clases
        db.execSQL("CREATE INDEX idx_clase_titulo_curso ON " + TABLE_CLASS + " (titulo, curso_id)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS preguntasdescargadas");
        onCreate(db);
    }

    public void guardarCursoDescargado(Curso curso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCurso", curso.getIdCurso());
        values.put("titulo", curso.getTitulo());
        values.put("descripcion", curso.getDescripcion());
        values.put("imagen", curso.getImagen());
        db.insertWithOnConflict(TABLE_COURSE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void guardarClaseDescargada(ClaseFirebase clase, int cursoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("curso_id", cursoId);
            values.put("titulo", clase.getTitulo());

            List<String> archivos = clase.getArchivosUrl();
            if (archivos == null) archivos = new ArrayList<>();
            String archivosJson = new Gson().toJson(archivos);

            values.put("documento", archivosJson);
            values.put("imagen", clase.getImagenUrl());
            values.put("video", clase.getVideoUrl());

            db.insert(TABLE_CLASS, null, values);

            String idClaseNormalizado = normalizarIdClase(clase.getTitulo());
            Log.d("DBHELPER", "🧩 ID clase normalizado para guardar: " + idClaseNormalizado);

            if (clase.getPreguntas() != null && !clase.getPreguntas().isEmpty()) {
                Log.d("DBHELPER", "📋 Preguntas detectadas para clase '" + clase.getTitulo() + "': " + clase.getPreguntas().size());
                Log.d("DBHELPER", "📦 Preguntas convertidas antes de guardar: " + clase.getPreguntas().size());
                for (PreguntaCuestionario pregunta : clase.getPreguntas()) {
                    Log.d("DBHELPER", "📝 Guardando pregunta: " + pregunta.getPregunta());
                    ContentValues preguntaValues = new ContentValues();
                    preguntaValues.put("id_clase", idClaseNormalizado);
                    preguntaValues.put("pregunta", pregunta.getPregunta());
                    preguntaValues.put("opciones", new Gson().toJson(pregunta.getRespuestas()));
                    preguntaValues.put("respuesta_correcta", pregunta.getRespuestaCorrecta() != null ? pregunta.getRespuestaCorrecta() : -1);
                    db.insert(TABLE_QUESTION, null, preguntaValues);
                }
            } else {
                Log.w("DBHELPER", "❌ No se encontraron preguntas para la clase: " + clase.getTitulo());
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean claseYaDescargadaConIdLocal(String titulo, int cursoIdLocal) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tituloNormalizado = titulo.trim().toLowerCase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_CLASS + " WHERE LOWER(TRIM(titulo)) = ? AND curso_id = ?",
                new String[]{tituloNormalizado, String.valueOf(cursoIdLocal)}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }

    public List<Curso> obtenerCursosDescargados() {
        List<Curso> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, titulo, descripcion, imagen FROM " + TABLE_COURSE, null);

        if (cursor.moveToFirst()) {
            do {
                Curso curso = new Curso();
                curso.setId(cursor.getInt(0));
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
        Cursor cursor = db.rawQuery("SELECT titulo, documento, imagen, video FROM " + TABLE_CLASS + " WHERE curso_id = ?", new String[]{String.valueOf(cursoId)});

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(0);
                String documentos = cursor.getString(1);
                String imagen = cursor.getString(2);
                String video = cursor.getString(3);

                List<String> archivos;

                try {
                    if (documentos != null && documentos.trim().startsWith("[")) {
                        archivos = new Gson().fromJson(documentos, new TypeToken<List<String>>() {}.getType());
                        if (archivos == null) archivos = new ArrayList<>();
                    } else {
                        archivos = new ArrayList<>();
                        if (documentos != null && !documentos.trim().isEmpty()) {
                            archivos.add(documentos.trim());
                            ContentValues update = new ContentValues();
                            update.put("documento", new Gson().toJson(archivos));
                            db.update(TABLE_CLASS, update, "curso_id = ? AND titulo = ?", new String[]{String.valueOf(cursoId), titulo});
                            Log.w("DBHelper", "📦 Reparado documento plano: " + titulo);
                        }
                    }
                } catch (Exception e) {
                    Log.e("DBHelper", "❌ Error parseando JSON documento en clase: " + titulo, e);
                    archivos = new ArrayList<>();
                }

                ClaseFirebase clase = new ClaseFirebase(titulo, archivos, imagen, video);
                //String idClaseNormalizado = normalizarIdClase(titulo);
                clase.setPreguntas(obtenerPreguntasPorClase(titulo));
                clases.add(clase);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return clases;
    }

    public List<PreguntaCuestionario> obtenerPreguntasPorClase(String idClase) {
        List<PreguntaCuestionario> preguntas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String idClaseNormalizado = normalizarIdClase(idClase);
        Log.d("DBHELPER", "🔍 Buscando preguntas con ID clase: '" + idClaseNormalizado + "'");

        Cursor cursor = db.rawQuery(
                "SELECT pregunta, opciones, respuesta_correcta FROM " + TABLE_QUESTION + " WHERE id_clase = ?",
                new String[]{idClaseNormalizado}
        );

        if (cursor.moveToFirst()) {
            do {
                PreguntaCuestionario p = new PreguntaCuestionario();
                p.setPregunta(cursor.getString(0));
                p.setRespuestas(new Gson().fromJson(cursor.getString(1), new TypeToken<List<String>>(){}.getType()));
                p.setRespuestaCorrecta(cursor.getInt(2));
                preguntas.add(p);
            } while (cursor.moveToNext());
        } else {
            Log.w("DBHELPER", "❌ No se encontraron preguntas para clase ID: " + idClaseNormalizado);
        }

        cursor.close();
        db.close();
        return preguntas;
    }

    public static List<PreguntaCuestionario> convertirPreguntasFirebase(List<Map<String, Object>> listaPreguntasMap) {
        List<PreguntaCuestionario> preguntas = new ArrayList<>();

        for (Map<String, Object> map : listaPreguntasMap) {
            PreguntaCuestionario pregunta = new PreguntaCuestionario();
            pregunta.setPregunta((String) map.get("pregunta"));

            Object respuestaCorrectaObj = map.get("respuestaCorrecta");
            if (respuestaCorrectaObj instanceof Number) {
                pregunta.setRespuestaCorrecta(((Number) respuestaCorrectaObj).intValue());
            }

            List<String> respuestas = new ArrayList<>();
            List<Object> respuestasRaw = (List<Object>) map.get("respuestas");
            for (Object obj : respuestasRaw) {
                respuestas.add(String.valueOf(obj));
            }
            pregunta.setRespuestas(respuestas);

            pregunta.setRespuestaUsuario(null); // o también podrías poner map.get("respuestaUsuario") si la tiene
            preguntas.add(pregunta);
            Log.d("DBHELPER", "🎯 Pregunta convertida: " + pregunta.getPregunta());
        }

        return preguntas;
    }

    private static String normalizarIdClase(String titulo) {
        return titulo.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }

    public List<Integer> obtenerRespuestasUsuarioPorClase(String tituloClase) {
        List<Integer> respuestas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT respuestaUsuario FROM preguntasdescargadas WHERE tituloClase = ? ORDER BY idPregunta ASC", new String[]{tituloClase});

        if (cursor.moveToFirst()) {
            do {
                respuestas.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return respuestas;
    }
    public void marcarClaseComoCompletadaLocal(String tituloClase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completada", 1); // Marca como completada

        int filas = db.update(TABLE_CLASS, values, "titulo = ?", new String[]{tituloClase});
        if (filas > 0) {
            Log.d("DBHELPER", "✅ Clase marcada como completada localmente: " + tituloClase);
        } else {
            Log.w("DBHELPER", "⚠️ No se encontró clase para marcar como completada: " + tituloClase);
        }

        db.close();
    }

    public void eliminarClasePorTitulo(String tituloClase) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS, "titulo = ?", new String[]{tituloClase});
        db.delete("preguntas_locales", "id_clase = ?", new String[]{normalizarIdClase(tituloClase)});
        db.delete("preguntasdescargadas", "tituloClase = ?", new String[]{tituloClase});
        db.delete("progreso_clase_offline", "tituloClase = ?", new String[]{tituloClase});
        db.close();
    }

    public void eliminarCursoYClasesPorId(int cursoId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            List<String> titulosClase = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT titulo FROM " + TABLE_CLASS + " WHERE curso_id = ?", new String[]{String.valueOf(cursoId)});
            while (cursor.moveToNext()) {
                titulosClase.add(cursor.getString(0));
            }
            cursor.close();

            db.delete(TABLE_CLASS, "curso_id = ?", new String[]{String.valueOf(cursoId)});
            db.delete(TABLE_COURSE, "id = ?", new String[]{String.valueOf(cursoId)});

            for (String titulo : titulosClase) {
                String idClaseNormalizado = normalizarIdClase(titulo);
                db.delete(TABLE_QUESTION, "id_clase = ?", new String[]{idClaseNormalizado});
                db.delete("preguntasdescargadas", "tituloClase = ?", new String[]{titulo});
            }

            db.setTransactionSuccessful();
            Log.d("DBHELPER", "🧹 Curso y clases eliminados junto con preguntas");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void guardarProgresoOffline(String tituloClase, long tiempoVisto, boolean cuestionarioCompletado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tituloClase", tituloClase);
        values.put("tiempoVisto", tiempoVisto);
        values.put("cuestionarioCompletado", cuestionarioCompletado ? 1 : 0);

        db.insertWithOnConflict("progreso_clase_offline", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Map<String, Object> obtenerProgresoOffline(String tituloClase) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tiempoVisto, cuestionarioCompletado FROM progreso_clase_offline WHERE tituloClase = ?", new String[]{tituloClase});

        Map<String, Object> datos = new HashMap<>();
        if (cursor.moveToFirst()) {
            datos.put("tiempoVisto", cursor.getLong(0));
            datos.put("cuestionarioCompletado", cursor.getInt(1) == 1);
        }
        cursor.close();
        db.close();
        return datos;
    }

    public List<Map<String, Object>> obtenerTodosLosProgresosOffline() {
        List<Map<String, Object>> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT p.tituloClase, p.tiempoVisto, p.cuestionarioCompletado, c.curso_id, c.id FROM progreso_clase_offline p INNER JOIN clasedescargada c ON p.tituloClase = c.titulo", null);

        while (cursor.moveToNext()) {
            Map<String, Object> progreso = new HashMap<>();
            progreso.put("tituloClase", cursor.getString(0));
            progreso.put("tiempoVisto", cursor.getLong(1));
            progreso.put("cuestionarioCompletado", cursor.getInt(2) == 1);
            progreso.put("idCurso", cursor.getInt(3));
            progreso.put("idClase", cursor.getInt(4));
            lista.add(progreso);
        }

        cursor.close();
        db.close();
        return lista;
    }

}