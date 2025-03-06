package com.example.skulfulharmony.javaobjects.clasifications.lists;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Genero;
import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;

import java.util.ArrayList;

public class Generos {

    ArrayList<Genero> listaGeneros = new ArrayList<>();
    public Generos() {
        String []nombres = {"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues",
        "Reggaeton", "Reggae", "Clasica", "Coutry", "Metal", "Folk", "Independiente"};
        Image[] Imagenes ={null, null, null, null, null,
                null, null, null, null, null};

        for(int i = 0; i<= nombres.length; i++){
            Genero n = new Genero(nombres[i],Imagenes[i] );
            listaGeneros.add(n);
        }
    }
}
