package com.example.skulfulharmony.javaobjects.courses;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.Pregunta;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Clase {

    private Integer idClase;
    private String nombreCurso;
    private String titulo;
    private String textos;
    private String imagen;
    private List<File> archivos;
    private String video;
    private List<PreguntaCuestionario> preguntas;
    private List<Comentario> comentarios;
    private int meGusta;
    private Date fechaCreacion;


    public Clase(String titulo, List<PreguntaCuestionario> preguntas) {
        this.titulo = titulo;
        this.preguntas = preguntas;
        this.fechaCreacion = new Date();
    }

    public Clase(String titulo, String nombreCurso, String imagen, List<PreguntaCuestionario>  preguntaCuestionarios){
        this.titulo = titulo;
        this.nombreCurso = nombreCurso;
        this.imagen = imagen;
        preguntas = preguntaCuestionarios;
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

    public String getNombreCurso() {
        return nombreCurso;
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

    public String getImagenCurso() {
        return imagenCurso;
    }

    public void setImagenCurso(String imagenCurso) {
        this.imagenCurso = imagenCurso;
    }

    private String imagenCurso;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }



}
