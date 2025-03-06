package com.example.skulfulharmony.javaobjects.clasifications.lists;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;

import java.util.ArrayList;

public class Instrumentos {
    ArrayList<Instrumento> listaInstrumento = new ArrayList<>();

    public Instrumentos() {
        String []Nombres = {"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería",
        "Piano", "Ukelele", "Violin", "Canto","Otro"};

        Image [] Imagenes ={null, null, null, null, null,
        null, null, null, null, null};

        for(int i = 0; i<= Nombres.length; i++){
            Instrumento n = new Instrumento(Nombres[i],Imagenes[i] );
            listaInstrumento.add(n);
        }

    }
}
