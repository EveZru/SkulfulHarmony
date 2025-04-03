package com.example.skulfulharmony.javaobjects.clasifications;

import android.media.Image;

public class Dificultad {
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    private String titulo;
    private String imagen;
    public String[] dificultades;

    public Dificultad(String titulo, String imagen) {
        dificultades = new String[2];
        dificultades[0]="Principiante";
        dificultades[1]="Intermedio";
        dificultades[2]="Avanzado";
        this.titulo = titulo;
        this.imagen = imagen;
    }

    public Dificultad(String titulo){
        this.titulo = titulo;
        switch (titulo){
            case "Principiaante":
                this.imagen = null;
                break;
            case "Intermedio":
                this.imagen = null;
                break;
            case "Avanzado":
                this.imagen = null;
                break;

        }
    }
}
