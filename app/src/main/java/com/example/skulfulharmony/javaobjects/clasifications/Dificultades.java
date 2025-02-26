package com.example.skulfulharmony.javaobjects.clasifications;

import com.example.skulfulharmony.javaobjects.clasifications.objects.Dificultad;

import java.util.ArrayList;
import java.util.Collection;

public class Dificultades {
    private  ArrayList<Dificultad> listaDificultades = new ArrayList<Dificultad>();

    public Dificultades() {
        String toChange="";
        Dificultad principiante = new Dificultad("Principiante",toChange);
        Dificultad intermedio = new Dificultad("Intermedio", toChange);
        Dificultad avanzado = new Dificultad("Avanzado", toChange);
        Dificultad experto = new Dificultad("Experto", toChange);
        listaDificultades.add(principiante);
        listaDificultades.add(intermedio);
        listaDificultades.add(avanzado);
        listaDificultades.add(experto);
    }
}
