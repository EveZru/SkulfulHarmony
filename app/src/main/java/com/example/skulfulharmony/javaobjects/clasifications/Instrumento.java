package com.example.skulfulharmony.javaobjects.clasifications;

import android.media.Image;

public class Instrumento {
    private String titulo;
    private Image imagen;

    public Instrumento(String titulo, Image imagen) {
        this.titulo = titulo;
        this.imagen = imagen;
    }
    public Instrumento(String titulo){
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public Image getImagen() {
        return imagen;
    }
}
