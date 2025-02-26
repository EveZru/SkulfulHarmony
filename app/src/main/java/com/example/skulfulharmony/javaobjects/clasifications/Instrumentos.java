package com.example.skulfulharmony.javaobjects.clasifications;

import com.example.skulfulharmony.javaobjects.clasifications.objects.Genero;
import com.example.skulfulharmony.javaobjects.clasifications.objects.Instrumento;

import java.util.ArrayList;

public class Instrumentos {

    ArrayList<Instrumento> listaInstrumentos = new ArrayList<>();
    public Instrumentos() {
        String toChange = "";
        Instrumento guitarra = new Instrumento("Guitarra", toChange);
        Instrumento bajo = new Instrumento("Bajo", toChange);
        Instrumento flauta = new Instrumento("Flauta", toChange);
        Instrumento trompeta = new Instrumento("Trompeta", toChange);
        Instrumento bateria = new Instrumento("Bateria", toChange);
        Instrumento piano = new Instrumento("Piano", toChange);
        Instrumento ukelele = new Instrumento("Ukelele", toChange);
        Instrumento violin = new Instrumento("Violin", toChange);
        Instrumento canto = new Instrumento("Canto", toChange);
        Instrumento otro = new Instrumento("Otro", toChange);


        listaInstrumentos.add(guitarra);
        listaInstrumentos.add(bajo);
        listaInstrumentos.add(flauta);
        listaInstrumentos.add(trompeta);
        listaInstrumentos.add(bateria);
        listaInstrumentos.add(piano);
        listaInstrumentos.add(ukelele);
        listaInstrumentos.add(violin);
        listaInstrumentos.add(canto);
        listaInstrumentos.add(otro);
    }
}
