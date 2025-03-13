package com.example.skulfulharmony.databaseinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.skulfulharmony.javaobjects.users.Usuario;

public class DbUser extends DbHelper{

    Context context;


    public DbUser(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertUser(Usuario usuario) {

        long id = 0;

        try{
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("correo", usuario.getCorreo());
            values.put("nombre", usuario.getNombre());
            values.put("imagen", usuario.getImagen());
            values.put("ultimo_acceso", System.currentTimeMillis());
            values.put("instrumento", usuario.getInstrumento().getTitulo());

            id = database.insert(DbHelper.TABLE_USER, null, values);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public Usuario getUser(String correoBusqueda){
        String nombre = "";
        String correoDeclarado = "";

        try{
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT *  FROM " + DbHelper.TABLE_USER, new String[]{correoBusqueda} );
            if(cursor.moveToFirst()){
                correoDeclarado = cursor.getString(0);
                nombre = cursor.getString(1);
            }
            cursor.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Usuario(nombre,correoDeclarado);
    }

    public Boolean existUser(String correoBusqueda){
        try{
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT *  FROM " + DbHelper.TABLE_USER, new String[]{correoBusqueda} );
            if(cursor.moveToFirst()){
                cursor.close();
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public  Boolean anyUser(){
        try{
            DbHelper dbHelper = new DbHelper(this.context);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT *  FROM " + DbHelper.TABLE_USER, null);
            if(cursor.moveToFirst()){
                cursor.close();
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}
