package com.example.skulfulharmony.javaobjects.clasifications;

import android.media.Image;

public class Instrumento {
    private String titulo;
    private Image imagen;

    public Instrumento(String titulo, Image imagen) {
        this.titulo = titulo;
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public Image getImagen() {
        return imagen;
    }

    public Instrumento(String titulo){
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
