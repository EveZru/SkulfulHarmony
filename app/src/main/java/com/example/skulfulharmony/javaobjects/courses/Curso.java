package com.example.skulfulharmony.javaobjects.courses;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Dificultad;
import com.example.skulfulharmony.javaobjects.clasifications.Genero;
import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.clustering.ClusterClases;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.Date;
import java.util.List;

public class Curso {
    private Integer idCurso;
    private Image imagen;
    private String titulo;
    private String descripcion;
    private List<Clase> clases;
    private Usuario creador;
    private List<Usuario> seguidores;
    private List<Comentario> comentarios;
    private ClusterClases cluster;
    private CalificacionCurso calificacion;
    private Date fechaCreacion;
    public Instrumento instrumento;
    public Genero genero;
    public Dificultad dificultad;

    public Curso(String titulo, Usuario creador, Instrumento instrumento, Genero genero, Dificultad dificultad) {
       /* this.titulo = titulo;
        this.creador = creador;
        this.instrumento = instrumento;
        this.genero = genero;
        this.dificultad = dificultad;
        this.fechaCreacion = new Date();*/
        this.titulo = titulo;
        this.creador = creador!= null ? creador : null;;
        this.instrumento = instrumento != null ? instrumento : null;  // Si no se pasa un objeto, se usa null
        this.genero = genero != null ? genero : null;
        this.dificultad = dificultad != null ? dificultad : null;

        /* this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.creador = creador;
        this.instrumento = instrumento != null ? instrumento : null;  // Si no se pasa un objeto, se usa null
        this.genero = genero != null ? genero : null;
        this.dificultad = dificultad != null ? dificultad : null;*/
    }

    public Image getImagen() {
        return imagen;
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ClusterClases getCluster() {
        return cluster;
    }

    public void setCluster(ClusterClases cluster) {
        this.cluster = cluster;
    }

    public CalificacionCurso getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(CalificacionCurso calificacion) {
        this.calificacion = calificacion;
    }
}
