package com.example.skulfulharmony.javaobjects.clasifications.lists;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Dificultad;
import com.example.skulfulharmony.javaobjects.clasifications.Genero;

import java.util.ArrayList;

public class Dificultades {
    ArrayList<Dificultad> listaDificultades = new ArrayList<>();

    public Dificultades() {
        String []nombres = {"Principiante", "Intermedio","Avanzado"};
        String[] Imagenes ={null, null, null, null, null,
                null, null, null, null, null};

        for(int i = 0; i<= nombres.length-1; i++){//mens 1 para evitar que se salga segun yo jsjjs
            Dificultad n = new Dificultad(nombres[i],Imagenes[i] );
            listaDificultades.add(n);
        }
    }
}
