package com.example.skulfulharmony.javaobjects.clasifications.lists;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Dificultad;
import com.example.skulfulharmony.javaobjects.clasifications.Genero;

import java.util.ArrayList;

public class Dificultades {
    ArrayList<Dificultad> listaDificultades = new ArrayList<>();

    public Dificultades() {
        String []nombres = {"Principiante", "Intermedio","Avanzado", "Experto"};
        Image[] Imagenes ={null, null, null, null, null,
                null, null, null, null, null};

        for(int i = 0; i<= nombres.length; i++){
            Dificultad n = new Dificultad(nombres[i],Imagenes[i] );
            listaDificultades.add(n);
        }
    }
}
