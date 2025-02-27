package com.example.skulfulharmony.javaobjects.courses;

import android.media.Image;
import android.net.Uri;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.Pregunta;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Clase {

    private String titulo;
    private ArrayList<String> textos = new ArrayList<>();
    private Image imagen;
    private List<File> archivos;
    private Uri video;
    private List<Pregunta> preguntas;
    private List<Comentario> comentarios;
    private int meGusta;
    private Date fechaCreacion;

    public Clase(String titulo, List<Pregunta> preguntas) {
        this.titulo = titulo;
        this.preguntas = preguntas;
        this.fechaCreacion = new Date();
    }

    public ArrayList<String> getTextos() {
        return textos;
    }

    public void setTextos(ArrayList<String> textos) {
        this.textos = textos;
    }

    public Image getImagen() {
        return imagen;
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
    }

    public List<File> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<File> archivos) {
        this.archivos = archivos;
    }

    public Uri getVideo() {
        return video;
    }

    public void setVideo(Uri video) {
        this.video = video;
    }
}
