package com.example.skulfulharmony.javaobjects.courses;

import android.media.Image;
import android.net.Uri;

import com.example.skulfulharmony.R;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.Pregunta;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Clase {


    public String getImagenCurso() {
        return imagenCurso;
    }

    public void setImagenCurso(String imagenCurso) {
        this.imagenCurso = imagenCurso;
    }

    private String imagenCurso;
    private String titulo;
    private ArrayList<String> textos = new ArrayList<>();
    private String imagen;
    private List<File> archivos;
    private Uri video;
    private List<PreguntaCuestionario> preguntas;
    private List<Comentario> comentarios;
    private int meGusta;
    private Integer idCurso;

    public String getNombreCurso() {
        return nombreCurso;
    }

    private String nombreCurso;

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    private Date fechaCreacion;

    public Clase(String titulo, List<PreguntaCuestionario> preguntas) {
        this.titulo = titulo;
        this.preguntas = preguntas;
        this.fechaCreacion = new Date();
    }

    public Clase(String titulo, String  nombreCurso, String image, List<PreguntaCuestionario> preguntas) {
        this.titulo = titulo;
        this.preguntas = preguntas;
        this.fechaCreacion = new Date();
        this.nombreCurso = nombreCurso;
        this.imagenCurso = image;

    }

    public ArrayList<String> getTextos() {
        return textos;
    }

    public void setTextos(ArrayList<String> textos) {
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

    public Uri getVideo() {
        return video;
    }

    public void setVideo(Uri video) {
        this.video = video;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
