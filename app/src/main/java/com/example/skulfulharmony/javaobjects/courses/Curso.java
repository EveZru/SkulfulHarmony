package com.example.skulfulharmony.javaobjects.courses;

//import com.example.skulfulharmony.javaobjects.clustering.ClusterClases;
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
    private Date fechaCreacion;
    private Timestamp fechaCreacionf;
    private Timestamp fechaActualizacion;
    private Timestamp fechaAcceso;
    private Map<String,Integer> instrumento;
    private Map<String,Integer> genero;
    private Map<String,Integer> dificultad;
    private Integer visitas;
    private Integer cantidadDescargas;
    private List<Integer> calificacionCursos;

    private Map<String, Integer> calificacionesPorUsuario; // Nuevo: Usuario UID/Correo -> Calificación
    private Double promedioCalificacion = 0.0;
    private Double popularidad;
    private String firestoreId;
    private Integer cluster;

    private Boolean strike1;
    private Boolean strike2;
    private Boolean strike3;

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
    public Curso(String titulo, String creador, Map<String,Integer> instrumento, Map<String,Integer> genero, Map<String,Integer> dificultad) {
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
    public Curso(String titulo, String creador, Map<String,Integer> instrumento, Map<String,Integer> genero, Map<String,Integer> dificultad, String imagen, Timestamp fechaCreacionf) {
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
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public void setDificultad(Map<String,Integer> dificultad) { this.dificultad = dificultad; }
    public void setGenero(Map<String,Integer> genero) {
        this.genero = genero;
    }
    public void setInstrumento(Map<String,Integer> instrumento) {
        this.instrumento = instrumento;
    }
    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }
    public void setCalificacionesPorUsuario(Map<String, Integer> calificacionesPorUsuario) {
        this.calificacionesPorUsuario = calificacionesPorUsuario;
    }
    public void setPromedioCalificacion(Double promedioCalificacion) {
        this.promedioCalificacion = promedioCalificacion;
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

    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    public Map<String,Integer> getDificultad() {
        return dificultad;
    }
    public Map<String,Integer> getGenero() {
        return genero;
    }
    public Map<String,Integer> getInstrumento() {
        return instrumento;
    }
    public List<Comentario> getComentarios() {
        if (comentarios == null) {
            comentarios = new ArrayList<>();
        }
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
    public Map<String, Integer> getCalificacionesPorUsuario() {
        return calificacionesPorUsuario;
    }
    public Double getPromedioCalificacion() {
        return promedioCalificacion;
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
        if (dificultad == null) return 1; // Principiante por defecto
        Integer nivel = dificultad.get("nivel"); // Obtenemos el valor Integer directamente
        if (nivel == null) return 1; // Si no existe la clave "nivel", se usa el valor por defecto
        // Validamos que esté en el rango 1-3 (1: Principiante, 2: Intermedio, 3: Avanzado)
        if (nivel >= 1 && nivel <= 3) {
            return nivel;
        }
        return 1; // Valor por defecto si está fuera del rango esperado
    }

    public Integer getCluster() {
        return cluster;
    }

    public void setCluster(Integer cluster) {
        this.cluster = cluster;
    }

    public Timestamp getFechaActualizacion(){
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Timestamp fechaActualizacion){
        this.fechaActualizacion = fechaActualizacion;
    }

    public Timestamp getFechaAcceso() {
        return fechaAcceso;
    }

    public void setFechaAcceso(Timestamp fechaAcceso) {
        this.fechaAcceso = fechaAcceso;
    }

    public Boolean getStrike1() {
        return strike1;
    }

    public void setStrike1(Boolean strike1) {
        this.strike1 = strike1;
    }

    public Boolean getStrike2() {
        return strike2;
    }

    public void setStrike2(Boolean strike2) {
        this.strike2 = strike2;
    }

    public Boolean getStrike3() {
        return strike3;
    }

    public void setStrike3(Boolean strike3) {
        this.strike3 = strike3;
    }

}