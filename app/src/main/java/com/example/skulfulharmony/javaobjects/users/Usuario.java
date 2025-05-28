package com.example.skulfulharmony.javaobjects.users;

import com.example.skulfulharmony.javaobjects.clustering.GestionClustering;
import com.example.skulfulharmony.javaobjects.clustering.RanqueadorCluster;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaInicio;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATRIBUTOS DE IDENTIFICACION
    private String imagen;
    private String nombre;
    private String id;
    private String user;
    private String correo;
    private Map<String,String> instrumento;

    private String fotoPerfil;

    private String descripcion;

    private int cursos;
    private int seguidores;

    // ATRIBUTOS DINAMICOS
    private List<Comentario> comentarios;
    private List<Curso> cursosSeguidos;
    private GestionClustering gestionClustering;
    private List<Curso> historialCursos;
    private List<Clase> historialClases;
    private RecomendacionDeUsuario recomendacionesUsuario;
    private List<Date> horasEntrada;
    private LocalTime tiempoDeNotificacion;
    private Date ultimoAcceso;

    // CONSTRUCTORES

    public Usuario() { }

    public Usuario(String nombre, String correo, String imagen, Map<String, String> instrumento) {
        this.nombre = nombre;
        this.correo = correo;
        this.imagen = imagen;
        this.instrumento = instrumento;
    }

    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
        this.user = nombre;
    }

    public Usuario(String nombre, String correo, String user){
        this.nombre = nombre;
        this.correo = correo;
        this.user = user;
    }

    public Usuario(String nombre, String correo, String imagen, Date ultimoAcceso){
        this.nombre = nombre;
        this.correo = correo;
        this.imagen = imagen;
        this.ultimoAcceso = ultimoAcceso;
    }

    public Usuario(String nombre, String correo, GestionClustering gestionClustering) {
        this.nombre = nombre;
        this.correo = correo;
        this.gestionClustering = gestionClustering;
    }

    // Métodos nuevos para progreso

    /**
     * Obtiene el nivel de dificultad mínimo de los cursos asociados a las clases del historial
     * @param todosLosCursos lista completa de cursos disponibles en el sistema
     * @return nivel mínimo de dificultad (1=Principiante, 2=Intermedio, 3=Avanzado)
     */
    public int obtenerNivelDificultadInicial(List<Curso> todosLosCursos) {
        if (historialClases == null || historialClases.isEmpty()) return 1; // Principiante por default

        int nivelMin = Integer.MAX_VALUE;

        for (Clase clase : historialClases) {
            Curso curso = buscarCursoPorId(clase.getIdCurso(), todosLosCursos);
            if (curso != null) {
                int nivel = curso.getNivelDificultad(); // Debes agregar este método en Curso
                if (nivel < nivelMin) nivelMin = nivel;
            }
        }
        return nivelMin == Integer.MAX_VALUE ? 1 : nivelMin;
    }

    /**
     * Obtiene el nivel de dificultad máximo de los cursos asociados a las clases del historial
     * @param todosLosCursos lista completa de cursos disponibles en el sistema
     * @return nivel máximo de dificultad (1=Principiante, 2=Intermedio, 3=Avanzado)
     */
    public int obtenerNivelDificultadActual(List<Curso> todosLosCursos) {
        if (historialClases == null || historialClases.isEmpty()) return 1;

        int nivelMax = Integer.MIN_VALUE;

        for (Clase clase : historialClases) {
            Curso curso = buscarCursoPorId(clase.getIdCurso(), todosLosCursos);
            if (curso != null) {
                int nivel = curso.getNivelDificultad();
                if (nivel > nivelMax) nivelMax = nivel;
            }
        }
        return nivelMax == Integer.MIN_VALUE ? 1 : nivelMax;
    }

    /**
     * Busca un curso por su id dentro de la lista completa de cursos
     * @param idCurso id del curso buscado
     * @param todosLosCursos lista completa de cursos
     * @return objeto Curso si se encuentra, null en otro caso
     */
    private Curso buscarCursoPorId(int idCurso, List<Curso> todosLosCursos) {
        if (todosLosCursos == null) return null;
        for (Curso c : todosLosCursos) {
            if (c.getIdCurso() != null && c.getIdCurso() == idCurso) {
                return c;
            }
        }
        return null;
    }

    /**
     * Obtiene una lista con las fechas de acceso a las clases del historial
     * @return lista de fechas de acceso
     */
    public List<Date> getFechasAcceso() {
        List<Date> fechas = new ArrayList<>();
        if (historialClases == null) return fechas;

        for (Clase clase : historialClases) {
            if (clase.getFechaAcceso() != null) {
                fechas.add(clase.getFechaAcceso().toDate());
            }
        }
        return fechas;
    }

    // Métodos getters y setters

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen){
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo){
        this.correo = correo;
    }

    public String getDescripcion() { return descripcion; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion;}

    public int getCursos() { return cursos; }

    public void setCursos(int cursos) { this.cursos = cursos; }

    public int getSeguidores() { return seguidores; }

    public void setSeguidores(int seguidores) { this.seguidores = seguidores; }

    public String getFotoPerfil() { return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public Map<String,String> getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(Map<String,String> instrumento){
        this.instrumento = instrumento;
    }

    public List<Curso> getHistorialCursos() {
        return historialCursos;
    }

    public List<Clase> getHistorialClases() {
        return historialClases;
    }

    public void setHistorialClases(List<Clase> historialClases) {
        this.historialClases = historialClases;
    }

    public void setHistorialCursos(List<Curso> historialCursos) {
        this.historialCursos = historialCursos;
    }

    public List<Curso> obtenerCursosRecomendados() {
        RecomendacionDeUsuario recomendacion = new RecomendacionDeUsuario(new RanqueadorCluster(), gestionClustering);
        return recomendacion.generarRecomendaciones(this);
    }

    // Método para actualizar tiempo de notificaciones (ya lo tienes, no modificado)
    public void actualizarTiempoNotificaciones() {
        Date haceDosSemanas = new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000);
        ArrayList<Date> temp = new ArrayList<>();
        long sumaHoras = 0, sumaMinutos = 0;

        for (Date d : horasEntrada) {
            if (d.after(haceDosSemanas)) {
                temp.add(d);
                LocalTime hora = d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                sumaHoras += hora.getHour();
                sumaMinutos += hora.getMinute();
            }
        }

        this.horasEntrada = temp;

        if (!temp.isEmpty()) {
            int promedioHora = (int) (sumaHoras / temp.size());
            int promedioMinuto = (int) (sumaMinutos / temp.size());
            this.tiempoDeNotificacion = LocalTime.of(promedioHora, promedioMinuto).plusMinutes(20);
        } else {
            this.tiempoDeNotificacion = null; // No hay datos recientes
        }
    }
}