package com.example.skulfulharmony.databaseinfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "localdata.db";
    public static final String TABLE_USER = "usuario";
    public static final String TABLE_COURSE = "cursodescargado";
    public static final String TABLE_CLASS = "clasedescargada";
    public static final String TABLE_QUESTION = "pregunta";
    public static final String TABLE_OPTIONS = "respuesta";
    public static final String TABLE_PROGRESS = "progreso";
    ;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREAR TABLA USUARIO
        {
            db.execSQL("CREATE TABLE " + TABLE_USER + "(" +
                    "correo TEXT PRIMARY KEY,  "                         +
                    "nombre TEXT NOT NULL, "                             +
                    "imagen TEXT, "                                      +
                    "ultimo_acceso DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "instrumento_principal TEXT "                        +
                    ")"
            );
        }

        //CREAR TABLA CURSO DESCARGADO
        {
            db.execSQL("CREATE TABLE " + TABLE_COURSE + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, "             +
                    "titulo TEXT NOT NULL, "                             +
                    "descripcion TEXT, "                                 +
                    "imagen TEXT, "                                      +
                    "fecha_descarga DATETIME DEFAULT CURRENT_TIMESTAMP " +
                    ")"
            );
        }

        //CREAR TABLA CLASES DESCARGADAS
        {
            db.execSQL("CREATE TABLE " + TABLE_CLASS + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "curso_id INTEGER NOT NULL ," +
                    "titulo TEXT NOT NULL, " +
                    "textos TEXT, " +
                    "imagen TEXT, " +
                    "video TEXT, "  +
                    "FOREIGN KEY (curso_id) REFERENCES cursodescargado(id) ON DELETE CASCADE" +
                    ")"
            );
        }

        //CREAR TABLA PREGUNTA
        {
            db.execSQL("CREATE TABLE " + TABLE_QUESTION + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "clase_id INTEGER NOT NULL, " +
                    "pregunta TEXT NOT NULL, " +
                    "respuesta_correcta TEXT NOT NULL, " +
                    "respuesta_usuario TEXT, " +
                    "es_pregunta_inicio INTEGER NOT NULL DEFAULT 0, " +
                    "fecha DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (clase_id) REFERENCES clasedescargada(id) ON DELETE CASCADE" +
                    ")"
            );
        }

        //CREAR TABLA OPCIONES DE RESPUESTAS
        {
            db.execSQL("CREATE TABLE " + TABLE_OPTIONS + "(" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT, "                               +
                    "    pregunta_id INTEGER NOT NULL, "                                       +
                    "    opcion TEXT NOT NULL, "                                               +
                    "    FOREIGN KEY (pregunta_id) REFERENCES pregunta(id) ON DELETE CASCADE" +
                    ")"
            );
        }

        //CREAR TABLA PROGRESO DEL USUARIO FUERA DE LINEA
        {
            db.execSQL("CREATE TABLE " + TABLE_PROGRESS + "(" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT, "                                   +
                    "    curso_id INTEGER NOT NULL, "                                              +
                    "    indice_progreso REAL DEFAULT 0, "                                         +
                    "    respuestas_correctas INTEGER DEFAULT 0, "                                 +
                    "    respuestas_incorrectas INTEGER DEFAULT 0, "                               +
                    "    FOREIGN KEY (curso_id) REFERENCES cursodescargado(id) ON DELETE CASCADE" +
                    ")"
            );
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_PROGRESS);
        db.execSQL("DROP TABLE " + TABLE_OPTIONS);
        db.execSQL("DROP TABLE " + TABLE_QUESTION);
        db.execSQL("DROP TABLE " + TABLE_CLASS);
        db.execSQL("DROP TABLE " + TABLE_COURSE);
        db.execSQL("DROP TABLE " + TABLE_USER);

        onCreate(db);

    }
}
