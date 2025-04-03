package com.example.skulfulharmony.javaobjects.clasifications;

import android.media.Image;

public class Genero {
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    private String titulo;
    private Image imagen;

    public Genero(String titulo, Image imagen) {
        this.titulo = titulo;
        this.imagen = imagen;
    }

    public Genero(String titulo){
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

        }//SUPERNECESITA CORRECCION
    }
}
