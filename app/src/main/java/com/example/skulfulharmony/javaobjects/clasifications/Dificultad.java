package com.example.skulfulharmony.javaobjects.clasifications;

import android.media.Image;

public class Dificultad {
    private String titulo;
    private Image imagen;
    public String[] dificultades;

    public Dificultad(String titulo, Image imagen) {
        dificultades = new String[2];
        dificultades[0]="Principiante";
        dificultades[1]="Intermedio";
        dificultades[2]="Avanzado";
        this.titulo = titulo;
        this.imagen = imagen;
    }
}
