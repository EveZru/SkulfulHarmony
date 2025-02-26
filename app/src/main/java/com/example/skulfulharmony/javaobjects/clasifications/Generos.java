package com.example.skulfulharmony.javaobjects.clasifications;

import com.example.skulfulharmony.javaobjects.clasifications.objects.Genero;

import java.util.ArrayList;

public class Generos {

    ArrayList<Genero> listaGeneros = new ArrayList<>();
    public Generos() {
        String toChange = "";
        Genero pop = new Genero("Pop", toChange);
        Genero rock = new Genero("Rock", toChange);
        Genero rap_hiphop = new Genero("Rap/Hip Hop", toChange);
        Genero electronica = new Genero("Electronica", toChange);
        Genero jazz = new Genero("Jazz", toChange);
        Genero blues = new Genero("Blues", toChange);
        Genero reggaeton = new Genero("Reggaetón", toChange);
        Genero reggae = new Genero("Reggae", toChange);
        Genero clasica = new Genero("Clasica", toChange);
        Genero country = new Genero("Country", toChange);
        Genero metal = new Genero("Metal", toChange);
        Genero folk = new Genero("Folk", toChange);
        Genero independiente = new Genero("Independiente", toChange);

        listaGeneros.add(pop);
        listaGeneros.add(rock);
        listaGeneros.add(rap_hiphop);
        listaGeneros.add(electronica);
        listaGeneros.add(jazz);
        listaGeneros.add(blues);
        listaGeneros.add(reggaeton);
        listaGeneros.add(reggae);
        listaGeneros.add(clasica);
        listaGeneros.add(country);
        listaGeneros.add(metal);
        listaGeneros.add(folk);
        listaGeneros.add(independiente);
    }
}
