package com.example.skulfulharmony.javaobjects.courses;

import com.example.skulfulharmony.javaobjects.clasifications.Dificultad;
import com.example.skulfulharmony.javaobjects.clasifications.Genero;
import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.clustering.ClusterCursos;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.Date;
import java.util.List;

public class Curso {
    private Integer idCurso;
    private String imagen;
    private String titulo;
    private String descripcion;
    private List<Clase> clases;
    private Usuario creador;
    private List<Usuario> seguidores;
    private List<Comentario> comentarios;
    private ClusterCursos cluster;
    private CalificacionCurso calificacion;

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    private Date fechaCreacion;

    public Instrumento getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(Instrumento instrumento) {
        this.instrumento = instrumento;
    }

    public Instrumento instrumento;

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public Genero genero;
    public Dificultad dificultad;

    public Curso(String titulo, Usuario creador, Instrumento instrumento, Genero genero, Dificultad dificultad) {
        this.titulo = titulo;
        this.creador = creador;
        this.instrumento = instrumento;
        this.genero = genero;
        this.dificultad = dificultad;
    }
    public Curso(String titulo, Usuario creador, String imagen, Instrumento instrumento, Genero genero, Dificultad dificultad) {
        this.titulo = titulo;
        this.creador = creador;
        this.imagen = imagen;
        this.instrumento = instrumento;
        this.genero = genero;
        this.dificultad = dificultad;
    }

    public Curso(Integer idCurso, String imagen, String titulo, Date fechaCreacion) {
        this.idCurso = idCurso;
        this.imagen = imagen;
        this.titulo = titulo;
        this.fechaCreacion = fechaCreacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
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

    public ClusterCursos getCluster() {
        return cluster;
    }

    public void setCluster(ClusterCursos cluster) {
        this.cluster = cluster;
    }

    public CalificacionCurso getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(CalificacionCurso calificacion) {
        this.calificacion = calificacion;
    }
}
