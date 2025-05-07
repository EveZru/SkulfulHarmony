package com.example.skulfulharmony.javaobjects.users;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class HistorialManager {
    private static final String PREFS_NAME = "HistorialPrefs";
    private static final String KEY_INTERACCIONES = "interacciones";

    public static void guardarInteraccion(Context context, String nombreElemento) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> historial = prefs.getStringSet(KEY_INTERACCIONES, new HashSet<>());
        Set<String> nuevoHistorial = new HashSet<>(historial);
        nuevoHistorial.add(nombreElemento);
        prefs.edit().putStringSet(KEY_INTERACCIONES, nuevoHistorial).apply();
    }

    public static Set<String> obtenerHistorial(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_INTERACCIONES, new HashSet<>());
    }
}