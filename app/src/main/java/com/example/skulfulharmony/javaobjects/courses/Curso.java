package com.example.skulfulharmony.javaobjects.courses;

//import com.example.skulfulharmony.javaobjects.clustering.ClusterClases;
import com.example.skulfulharmony.javaobjects.clustering.ClusterCursos;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Curso {

    public Curso() {
        // Initialize collections and maps to avoid NullPointerException
        this.clases = new ArrayList<>();
        this.seguidores = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.instrumento = new HashMap<>();

        this.genero = new HashMap<>();
        this.dificultad = new HashMap<>();
    }
    public Integer getId() {
        return idCurso;
    }

    public void setId(Integer id) {
        this.idCurso = id;
    }

    private Integer idCurso;
    private String imagen;
    private String titulo;
    private String descripcion;
    private List<Clase> clases;
    private String creador;
    private List<Usuario> seguidores;
    private List<Comentario> comentarios;
    private ClusterCursos cluster;
    private CalificacionCurso calificacion;
    private Date fechaCreacion;

    public Timestamp getFechaCreacionf() {
        return fechaCreacionf;
    }

    public void setFechaCreacionf(Timestamp fechaCreacionf) {
        this.fechaCreacionf = fechaCreacionf;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    private Timestamp fechaCreacionf;
    private Map<String,String> instrumento;
    private Map<String,String> genero;
    private Map<String,String> dificultad;

    public Curso(Integer id, String titulo, String imagen, Date fechaCreacion){
        this.idCurso = id;
        this.titulo = titulo;
        this.imagen = imagen;
        this.fechaCreacion = fechaCreacion;
    }

    public Curso(String titulo, String creador, Map<String,String> instrumento, Map<String,String> genero, Map<String,String> dificultad) {
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

    public Curso(String titulo, String creador, Map<String,String> instrumento, Map<String,String> genero, Map<String,String> dificultad, String imagen, Timestamp fechaCreacionf) {
        this.titulo = titulo;
        this.creador = creador;
        this.instrumento = instrumento;
        this.genero = genero;
        this.dificultad = dificultad;
        this.imagen = imagen;
        this.fechaCreacionf = fechaCreacionf;
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
    public void setCalificacion(CalificacionCurso calificacion) { this.calificacion = calificacion; }
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public Map<String,String> getDificultad() {
        return dificultad;
    }
    public void setDificultad(Map<String,String> dificultad) { this.dificultad = dificultad; }
    public Map<String,String> getGenero() {
        return genero;
    }
    public void setGenero(Map<String,String> genero) {
        this.genero = genero;
    }
    public Map<String,String> getInstrumento() {
        return instrumento;
    }
    public void setInstrumento(Map<String,String> instrumento) {
        this.instrumento = instrumento;
    }
}
