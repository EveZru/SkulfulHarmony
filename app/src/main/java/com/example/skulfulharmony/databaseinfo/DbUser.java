package com.example.skulfulharmony.databaseinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;

import com.example.skulfulharmony.databaseinfo.DbHelper;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Map;

public class DbUser extends DbHelper {

    Context context;
    private final Gson gson = new Gson();

    public DbUser(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertUser(Usuario usuario) {
        long id = 0;
        try {
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("correo", usuario.getCorreo());
            values.put("nombre", usuario.getNombre());
            values.put("imagen", usuario.getImagen());
            values.put("ultimo_acceso", System.currentTimeMillis());

            // Convertir el Map a JSON antes de almacenarlo
            String instrumentoJson = gson.toJson(usuario.getInstrumento());
            values.put("instrumento", instrumentoJson);

            id = database.insert(DbHelper.TABLE_USER, null, values);
            database.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public String getCorreoUser(){
        String correo = "";
        try{
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT correo FROM " + DbHelper.TABLE_USER + " LIMIT 1", null);
            if (cursor.moveToFirst()) {
                correo = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return correo;
    }

    public Usuario getUser(String correoBusqueda) {
        Usuario usuario = null;
        try {
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT nombre, correo, imagen, instrumento FROM " + DbHelper.TABLE_USER + " WHERE correo=?", new String[]{correoBusqueda});

            if (cursor.moveToFirst()) {
                String nombre = cursor.getString(0);
                String correo = cursor.getString(1);
                String imagen = cursor.getString(2);
                String instrumentoJson = cursor.getString(3);

                // Convertir JSON a Map<String, String>
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> instrumento = gson.fromJson(instrumentoJson, type);

                usuario = new Usuario(nombre, correo, imagen, instrumento);
            }
            cursor.close();
            database.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return usuario;
    }

    public boolean existUser(String correoBusqueda) {
        boolean exists = false;
        try {
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT 1 FROM " + DbHelper.TABLE_USER + " WHERE correo=? LIMIT 1", new String[]{correoBusqueda});

            exists = cursor.moveToFirst();
            cursor.close();
            database.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
    }

    public boolean anyUser() {
        boolean exists = false;
        try {
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT 1 FROM " + DbHelper.TABLE_USER + " LIMIT 1", null);

            exists = cursor.moveToFirst();
            cursor.close();
            database.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exists;
    }

    public void deleteUser() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_OPTIONS, null, null);
        db.delete(DbHelper.TABLE_QUESTION, null, null);
        db.delete(DbHelper.TABLE_PROGRESS, null, null);
        db.delete(DbHelper.TABLE_CLASS, null, null);
        db.delete(DbHelper.TABLE_COURSE, null, null);
        db.delete(DbHelper.TABLE_USER, null, null);
        db.close();
    }
}
