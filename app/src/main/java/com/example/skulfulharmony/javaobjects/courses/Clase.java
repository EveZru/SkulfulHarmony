package com.example.skulfulharmony.javaobjects.courses;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.Pregunta;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Clase {

    private String titulo;
    private String textos;
    private String imagen;
    private List<File> archivos;
    private String video;
    private List<Pregunta> preguntas;
    private List<Comentario> comentarios;
    private int meGusta;
    private Date fechaCreacion;

    public Clase(String titulo, List<Pregunta> preguntas) {
        this.titulo = titulo;
        this.preguntas = preguntas;
        this.fechaCreacion = new Date();
    }

    public String getTextos() {
        return textos;
    }

    public void setTextos(String textos) {
        this.textos = textos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<File> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<File> archivos) {
        this.archivos = archivos;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
