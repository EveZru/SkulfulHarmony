package com.example.skulfulharmony.javaobjects.courses;

//import com.example.skulfulharmony.javaobjects.clustering.ClusterClases;
import com.example.skulfulharmony.javaobjects.clustering.ClusterCursos;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Curso implements Serializable {


    //Atributos

    private Integer idCurso;
    private String imagen;
    private String titulo;
    private String descripcion;
    private List<Clase> clases;
    private String creador;
    private List<Usuario> seguidores;
    private List<Comentario> comentarios;
    private ClusterCursos cluster;
    private Date fechaCreacion;
    private Timestamp fechaCreacionf;
    private Map<String,String> instrumento;
    private Map<String,String> genero;
    private Map<String,String> dificultad;
    private Integer visitas;
    private Integer cantidadDescargas;
    private List<Integer> calificacionCursos;
    private Double popularidad;
    private String firestoreId;

    //Constructores

    public Curso(String notaDeSol, String s) {
        // Initialize collections and maps to avoid NullPointerException
        this.clases = new ArrayList<>();
        this.seguidores = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.instrumento = new HashMap<>();
        this.genero = new HashMap<>();
        this.dificultad = new HashMap<>();
    }
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
    public Curso() {
        // Puedes inicializar las variables a valores por defecto si es necesario
        this.titulo = null;
        this.imagen = null;
        this.descripcion = null;

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




    //Setters
    public void setPopularidad(Double popularidad) {this.popularidad = popularidad;}

    public void setId(Integer id) { this.idCurso = id; }
    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }
    public void setFechaCreacionf(Timestamp fechaCreacionf) {
        this.fechaCreacionf = fechaCreacionf;
    }
    public void setCreador(String creador) {
        this.creador = creador;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setCluster(ClusterCursos cluster) {
        this.cluster = cluster;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public void setDificultad(Map<String,String> dificultad) { this.dificultad = dificultad; }
    public void setGenero(Map<String,String> genero) {
        this.genero = genero;
    }
    public void setInstrumento(Map<String,String> instrumento) {
        this.instrumento = instrumento;
    }
    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    //Getters

    public Double getPopularidad() { return popularidad; }

    public Integer getId() {
        return idCurso;
    }
    public Integer getIdCurso() {
        return idCurso;
    }
    public Timestamp getFechaCreacionf() {
        return fechaCreacionf;
    }
    public String getCreador() {
        return creador;
    }
    public String getImagen() {
        return imagen;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getTitulo() {
        return titulo;
    }
    public ClusterCursos getCluster() {
        return cluster;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    public Map<String,String> getDificultad() {
        return dificultad;
    }
    public Map<String,String> getGenero() {
        return genero;
    }
    public Map<String,String> getInstrumento() {
        return instrumento;
    }
    public List<Comentario> getComentarios() {
        return comentarios;
    }
    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    public List<Usuario> getSeguidores() {
        return seguidores;
    }
    public void setSeguidores(List<Usuario> seguidores) {
        this.seguidores = seguidores;
    }
    public List<Clase> getClases() {
        return clases;
    }
    public void setClases(List<Clase> clases) {
        this.clases = clases;
    }

    public Integer getVisitas() {
        return visitas;
    }

    public void setVisitas(Integer visitas) {
        this.visitas = visitas;
    }

    public Integer getCantidadDescargas() {
        return cantidadDescargas;
    }

    public void setCantidadDescargas(Integer cantidadDescargas) {
        this.cantidadDescargas = cantidadDescargas;
    }

    public List<Integer> getCalificacionCursos() {
        return calificacionCursos;
    }

    public void setCalificacionCursos(List<Integer> calificacionCursos) {
        this.calificacionCursos = calificacionCursos;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    // Método nuevo para obtener el nivel de dificultad en número
    public int getNivelDificultad() {
        if (dificultad == null) return 1; // Principiante por default
        String nivel = dificultad.get("nivel"); // Ajusta la clave si usas otro nombre
        if (nivel == null) return 1;
        switch (nivel.toLowerCase()) {
            case "principiante": return 1;
            case "intermedio": return 2;
            case "avanzado": return 3;
            default: return 1;
        }
    }
}