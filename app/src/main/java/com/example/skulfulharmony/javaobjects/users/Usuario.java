package com.example.skulfulharmony.javaobjects.users;

import android.util.Log;

import com.example.skulfulharmony.javaobjects.clustering.ClusterCallback;
import com.example.skulfulharmony.javaobjects.clustering.DataClusterList;
import com.example.skulfulharmony.javaobjects.clustering.DataPoint;
import com.example.skulfulharmony.javaobjects.clustering.DificultadCallback;
import com.example.skulfulharmony.javaobjects.clustering.GeneroCallback;
import com.example.skulfulharmony.javaobjects.clustering.InstrumentoCallback;
import com.example.skulfulharmony.javaobjects.clustering.KMeans;
import com.example.skulfulharmony.javaobjects.clustering.PreferenciasUsuario;
import com.example.skulfulharmony.javaobjects.clustering.RespuestasCuestionario;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaRecomendacion;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private String rol;
    private Map<String,Integer> instrumento;

    public ArrayList<PreguntaCuestionario> getPreguntasRepaso() {
        return preguntasRepaso;
    }

    public void setPreguntasRepaso(ArrayList<PreguntaCuestionario> preguntasRepaso) {
        this.preguntasRepaso = preguntasRepaso;
    }

    private ArrayList<PreguntaCuestionario> preguntasRepaso;

    private String fotoPerfil;

    private String descripcion;

    private int cursos;
    private int seguidores;
    private int seguidos;

    // ATRIBUTOS DINAMICOS
    private List<Comentario> comentarios;
    private List<Integer> cursosSeguidos;
    private List<Curso> historialCursos;
    private List<Clase> historialClases;
    private List<Date> horasEntrada;
    private List<Date> fechasAcceso;
    private String tiempoDeNotificacion;
    private Date ultimoAcceso;
    private Integer cluster;
    private PreferenciasUsuario preferenciasUsuario;
    private RespuestasCuestionario respuestasCuestionario;
    private List<PreguntaRecomendacion> preguntaRecomendacionList;

    // CONSTRUCTORES

    public Usuario() { }

    public Usuario(String nombre, String correo, String imagen, Map<String, String> instrumento) {
        this.nombre = nombre;
        this.correo = correo;
        this.imagen = imagen;
        this.instrumento = new HashMap<>();
        if (instrumento != null && instrumento.get("instrumento") != null) {
            this.instrumento.put("instrumento", Integer.parseInt(instrumento.get("instrumento")));
        } else {
            this.instrumento.put("Guitarra", 0); // valor default si falla
        }
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


    public int getSeguidos() {
        return seguidos;
    }

    public void setSeguidos(int seguidos) {
        this.seguidos = seguidos;
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

    public Integer getCluster(){
        return cluster;
    }
    public void setCluster(Integer cluster){
        this.cluster = cluster;
    }

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
    public String getRol(){return rol;}

    public void setId(String id) { this.id = id; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion;}

    public int getCursos() { return cursos; }

    public void setCursos(int cursos) { this.cursos = cursos; }

    public int getSeguidores() { return seguidores; }

    public void setSeguidores(int seguidores) { this.seguidores = seguidores; }

    public List<Date> getHorasEntrada() {
        return horasEntrada;
    }

    public void setHorasEntrada(List<Date> horasEntrada) {
        this.horasEntrada = horasEntrada;
    }

    public Date getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Date ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
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

    public void setRol(String rol) {
        this.rol = rol;
    }

    public RespuestasCuestionario getRespuestasCuestionario() {
        return respuestasCuestionario;
    }

    public void setRespuestasCuestionario(RespuestasCuestionario respuestasCuestionario) {
        this.respuestasCuestionario = respuestasCuestionario;
    }

    public String getFotoPerfil() { return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public Map<String,Integer> getInstrumento() {
        if (instrumento == null) instrumento = new HashMap<>();
        return instrumento;
    }

    public void setInstrumento(Map<String,Integer> instrumento){
        this.instrumento = instrumento;
    }

    public List<Curso> getHistorialCursos() {
        return historialCursos;
    }

    public PreferenciasUsuario getPreferenciasUsuario() {
        return preferenciasUsuario;
    }

    public void setPreferenciasUsuario(PreferenciasUsuario preferenciasUsuario) {
        this.preferenciasUsuario = preferenciasUsuario;
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

    public List<PreguntaRecomendacion> getPreguntaRecomendacionList() {
        return preguntaRecomendacionList;
    }

    public void setPreguntaRecomendacionList(List<PreguntaRecomendacion> preguntaRecomendacionList) {
        this.preguntaRecomendacionList = preguntaRecomendacionList;
    }
    public void setTiempoDeNotificacion(String tiempoDeNotificacion) {
        this.tiempoDeNotificacion = tiempoDeNotificacion;
    }

    public void setFechasAcceso(List<Date> fechasAcceso) {
        this.fechasAcceso = fechasAcceso;
    }

    public String setRol(){return rol;}
    public List<Integer> getCursosSeguidos() {
        return cursosSeguidos;
    }
    public void setCursosSeguidos(List<Integer> cursosSeguidos) {
        this.cursosSeguidos = cursosSeguidos;
    }
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
            this.tiempoDeNotificacion = LocalTime.of(promedioHora, promedioMinuto).plusMinutes(20).toString();
        } else {
            this.tiempoDeNotificacion = null; // No hay datos recientes
        }
    }

    public void obtenerInstrumentoMasVisto(FirebaseFirestore firestore, InstrumentoCallback callback) {
        Map<String, Double> instrumentosPuntaje = new HashMap<>();

        // 1. Preferencias del usuario (20%)
        if (preferenciasUsuario != null && preferenciasUsuario.getInstrumentos() != null) {
            int maxPref = preferenciasUsuario.getInstrumentos().values().stream().max(Integer::compare).orElse(1);
            for (Map.Entry<String, Integer> entry : preferenciasUsuario.getInstrumentos().entrySet()) {
                String nombreInstrumento = getNombreInstrumentoPorId(entry.getKey());
                if (nombreInstrumento != null) {
                    double valor = (entry.getValue() * 1.0 / maxPref) * 20.0;
                    instrumentosPuntaje.merge(nombreInstrumento, valor, Double::sum);
                }
            }
        }

        // 2. Historial de cursos (10%)
        if (historialCursos != null) {
            Map<String, Integer> conteo = new HashMap<>();
            for (Curso curso : historialCursos) {
                if (curso.getInstrumento() != null) {
                    for (String instrumento : curso.getInstrumento().keySet()) {
                        String nombreInstrumento = getNombreInstrumentoPorId(instrumento);
                        if (nombreInstrumento != null) {
                            conteo.put(nombreInstrumento, conteo.getOrDefault(nombreInstrumento, 0) + 1);
                        }
                    }
                }
            }
            int max = conteo.values().stream().max(Integer::compare).orElse(1);
            for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                double valor = (entry.getValue() * 1.0 / max) * 10.0;
                instrumentosPuntaje.merge(entry.getKey(), valor, Double::sum);
            }
        }

        // 3. Respuestas del cuestionario (15%)
        if (respuestasCuestionario != null && respuestasCuestionario.getInstrumento() != null) {
            String instrumento = getNombreInstrumentoPorId(respuestasCuestionario.getInstrumento());
            if (instrumento != null) {
                instrumentosPuntaje.merge(instrumento, 15.0, Double::sum);
            }
        }

        // 4. Cursos seguidos (25%)
        if (cursosSeguidos != null && !cursosSeguidos.isEmpty()) {
            firestore.collection("cursos")
                    .whereIn("idCurso", cursosSeguidos)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        Map<String, Integer> conteo = new HashMap<>();
                        for (QueryDocumentSnapshot doc : snapshot) {
                            Curso curso = doc.toObject(Curso.class);
                            if (curso.getInstrumento() != null) {
                                for (String instrumento : curso.getInstrumento().keySet()) {
                                    String nombreInstrumento = getNombreInstrumentoPorId(instrumento);
                                    if (nombreInstrumento != null) {
                                        conteo.put(nombreInstrumento, conteo.getOrDefault(nombreInstrumento, 0) + 1);
                                    }
                                }
                            }
                        }
                        int max = conteo.values().stream().max(Integer::compare).orElse(1);
                        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                            double valor = (entry.getValue() * 1.0 / max) * 25.0;
                            instrumentosPuntaje.merge(entry.getKey(), valor, Double::sum);
                        }
                        procesarInstrumentosDesdeClases(firestore, instrumentosPuntaje, callback);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("InstrumentoUsuario", "Error al obtener cursos seguidos", e);
                        callback.onResult(new HashMap<>());
                    });
        } else {
            procesarInstrumentosDesdeClases(firestore, instrumentosPuntaje, callback);
        }
    }
    private void procesarInstrumentosDesdeClases(FirebaseFirestore firestore, Map<String, Double> instrumentosPuntaje, InstrumentoCallback callback) {
        if (historialClases == null || historialClases.isEmpty()) {
            finalizarConteo(instrumentosPuntaje, callback);
            return;
        }
        List<Integer> idsCursos = new ArrayList<>();
        for (Clase clase : historialClases) {
            if (clase.getIdCurso() != null && !idsCursos.contains(clase.getIdCurso())) {
                idsCursos.add(clase.getIdCurso());
            }
        }
        if (idsCursos.isEmpty()) {
            finalizarConteo(instrumentosPuntaje, callback);
            return;
        }
        firestore.collection("cursos")
                .whereIn("idCurso", idsCursos)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, Integer> conteo = new HashMap<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Curso curso = doc.toObject(Curso.class);
                        if (curso.getInstrumento() != null) {
                            for (String instrumento : curso.getInstrumento().keySet()) {
                                String nombreInstrumento = getNombreInstrumentoPorId(instrumento);
                                if (nombreInstrumento != null) {
                                    conteo.put(nombreInstrumento, conteo.getOrDefault(nombreInstrumento, 0) + 1);
                                }
                            }
                        }
                    }
                    int max = conteo.values().stream().max(Integer::compare).orElse(1);
                    for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                        double valor = (entry.getValue() * 1.0 / max) * 30.0;
                        instrumentosPuntaje.merge(entry.getKey(), valor, Double::sum);
                    }
                    finalizarConteo(instrumentosPuntaje, callback);
                })
                .addOnFailureListener(e -> {
                    Log.e("InstrumentoUsuario", "Error al obtener cursos desde clases", e);
                    callback.onResult(new HashMap<>());
                });
    }
    private void finalizarConteo(Map<String, Double> instrumentosPuntaje, InstrumentoCallback callback) {
        if (instrumentosPuntaje.isEmpty()) {
            Map<String, Integer> resultado = new HashMap<>();
            resultado.put("Guitarra", 0);
            callback.onResult(resultado);
            return;
        }
        String instrumentoMasFuerte = null;
        double max = -1;
        for (Map.Entry<String, Double> entry : instrumentosPuntaje.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                instrumentoMasFuerte = entry.getKey();
            }
        }
        Map<String, Integer> resultado = new HashMap<>();
        if (instrumentoMasFuerte != null) {
            for (Map<String, Integer> instrumentoMap : DataClusterList.listaInstrumentos) {
                if (instrumentoMap.containsKey(instrumentoMasFuerte)) {
                    resultado.put(instrumentoMasFuerte, instrumentoMap.get(instrumentoMasFuerte));
                    break;
                }
            }
        }
        callback.onResult(resultado);
    }
    private String getNombreInstrumentoPorId(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            for (Map<String, Integer> map : DataClusterList.listaInstrumentos) {
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (entry.getValue() == id) {
                        return entry.getKey();
                    }
                }
            }
        } catch (NumberFormatException e) {
            return idStr;
        }
        return null;
    }
    public void obtenerGeneroMasVisto(FirebaseFirestore firestore, GeneroCallback callback) {
        Map<String, Double> generosPuntaje = new HashMap<>();
        // 1. Preferencias del usuario (20%)
        if (preferenciasUsuario != null && preferenciasUsuario.getGeneros() != null) {
            int maxPref = preferenciasUsuario.getGeneros().values().stream().max(Integer::compare).orElse(1);
            for (Map.Entry<String, Integer> entry : preferenciasUsuario.getGeneros().entrySet()) {
                String nombreGenero = getNombreGeneroPorId(entry.getKey());
                if (nombreGenero != null) {
                    double valor = (entry.getValue() * 1.0 / maxPref) * 20.0;
                    generosPuntaje.merge(nombreGenero, valor, Double::sum);
                }
            }
        }
        // 2. Historial de cursos (10%)
        if (historialCursos != null) {
            Map<String, Integer> conteo = new HashMap<>();
            for (Curso curso : historialCursos) {
                if (curso.getGenero() != null) {
                    for (String genero : curso.getGenero().keySet()) {
                        String nombreGenero = getNombreGeneroPorId(genero);
                        if (nombreGenero != null) {
                            conteo.put(nombreGenero, conteo.getOrDefault(nombreGenero, 0) + 1);
                        }
                    }
                }
            }
            int max = conteo.values().stream().max(Integer::compare).orElse(1);
            for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                double valor = (entry.getValue() * 1.0 / max) * 10.0;
                generosPuntaje.merge(entry.getKey(), valor, Double::sum);
            }
        }
        // 3. Respuestas del cuestionario (15%)
        if (respuestasCuestionario != null && respuestasCuestionario.getGenero() != null) {
            String genero = getNombreGeneroPorId(respuestasCuestionario.getGenero());
            if (genero != null) {
                generosPuntaje.merge(genero, 15.0, Double::sum);
            }
        }
        // 4. Cursos seguidos (25%)
        if (cursosSeguidos != null && !cursosSeguidos.isEmpty()) {
            firestore.collection("cursos")
                    .whereIn("idCurso", cursosSeguidos)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        Map<String, Integer> conteo = new HashMap<>();
                        for (QueryDocumentSnapshot doc : snapshot) {
                            Curso curso = doc.toObject(Curso.class);
                            if (curso.getGenero() != null) {
                                for (String genero : curso.getGenero().keySet()) {
                                    String nombreGenero = getNombreGeneroPorId(genero);
                                    if (nombreGenero != null) {
                                        conteo.put(nombreGenero, conteo.getOrDefault(nombreGenero, 0) + 1);
                                    }
                                }
                            }
                        }
                        int max = conteo.values().stream().max(Integer::compare).orElse(1);
                        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                            double valor = (entry.getValue() * 1.0 / max) * 25.0;
                            generosPuntaje.merge(entry.getKey(), valor, Double::sum);
                        }
                        procesarGenerosDesdeClases(firestore, generosPuntaje, callback);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("GeneroUsuario", "Error al obtener cursos seguidos", e);
                        callback.onResult(new HashMap<>());
                    });
        } else {
            procesarGenerosDesdeClases(firestore, generosPuntaje, callback);
        }
    }
    private void procesarGenerosDesdeClases(FirebaseFirestore firestore, Map<String, Double> generosPuntaje, GeneroCallback callback) {
        if (historialClases == null || historialClases.isEmpty()) {
            finalizarConteoGenero(generosPuntaje, callback);
            return;
        }
        List<Integer> idsCursos = new ArrayList<>();
        for (Clase clase : historialClases) {
            if (clase.getIdCurso() != null && !idsCursos.contains(clase.getIdCurso())) {
                idsCursos.add(clase.getIdCurso());
            }
        }
        if (idsCursos.isEmpty()) {
            finalizarConteoGenero(generosPuntaje, callback);
            return;
        }
        firestore.collection("cursos")
                .whereIn("idCurso", idsCursos)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, Integer> conteo = new HashMap<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Curso curso = doc.toObject(Curso.class);
                        if (curso.getGenero() != null) {
                            for (String genero : curso.getGenero().keySet()) {
                                String nombreGenero = getNombreGeneroPorId(genero);
                                if (nombreGenero != null) {
                                    conteo.put(nombreGenero, conteo.getOrDefault(nombreGenero, 0) + 1);
                                }
                            }
                        }
                    }
                    int max = conteo.values().stream().max(Integer::compare).orElse(1);
                    for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                        double valor = (entry.getValue() * 1.0 / max) * 30.0;
                        generosPuntaje.merge(entry.getKey(), valor, Double::sum);
                    }
                    finalizarConteoGenero(generosPuntaje, callback);
                })
                .addOnFailureListener(e -> {
                    Log.e("GeneroUsuario", "Error al obtener cursos desde clases", e);
                    callback.onResult(new HashMap<>());
                });
    }
    private void finalizarConteoGenero(Map<String, Double> generosPuntaje, GeneroCallback callback) {
        if (generosPuntaje.isEmpty()) {
            Map<String, Integer> resultado = new HashMap<>();
            resultado.put("Pop", 0);
            callback.onResult(resultado);
            return;
        }
        String generoMasFuerte = null;
        double max = -1;
        for (Map.Entry<String, Double> entry : generosPuntaje.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                generoMasFuerte = entry.getKey();
            }
        }
        Map<String, Integer> resultado = new HashMap<>();
        if (generoMasFuerte != null) {
            for (Map<String, Integer> generoMap : DataClusterList.listaGenero) {
                if (generoMap.containsKey(generoMasFuerte)) {
                    resultado.put(generoMasFuerte, generoMap.get(generoMasFuerte));
                    break;
                }
            }
        }
        callback.onResult(resultado);
    }
    private String getNombreGeneroPorId(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            for (Map<String, Integer> map : DataClusterList.listaGenero) {
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (entry.getValue() == id) {
                        return entry.getKey();
                    }
                }
            }
        } catch (NumberFormatException e) {
            return idStr; // ya es nombre
        }
        return null;
    }
    public void obtenerDificultadMasVistaPorInstrumento(FirebaseFirestore firestore, DificultadCallback callback) {
        obtenerInstrumentoMasVisto(firestore, instrumentoMap -> {
            if (instrumentoMap.isEmpty()) {
                Map<String, Integer> resultado = new HashMap<>();
                resultado.put("Principiante", 0);
                callback.onResult(resultado);
                return;
            }
            String instrumentoClave = instrumentoMap.keySet().iterator().next();
            Map<String, Double> dificultadPuntaje = new HashMap<>();
            // 1. Preferencias del usuario (20%)
            if (preferenciasUsuario != null && preferenciasUsuario.getDificultades() != null) {
                int maxPref = preferenciasUsuario.getDificultades().values().stream().max(Integer::compare).orElse(1);
                for (Map.Entry<String, Integer> entry : preferenciasUsuario.getDificultades().entrySet()) {
                    double valor = (entry.getValue() * 1.0 / maxPref) * 20.0;
                    dificultadPuntaje.merge(entry.getKey(), valor, Double::sum);
                }
            }
            // 2. Historial de cursos (10%)
            if (historialCursos != null) {
                Map<String, Integer> conteo = new HashMap<>();
                for (Curso curso : historialCursos) {
                    if (curso.getInstrumento() != null && curso.getInstrumento().containsKey(instrumentoClave)) {
                        agregarDificultad(conteo, curso);
                    }
                }
                int max = conteo.values().stream().max(Integer::compare).orElse(1);
                for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                    double valor = (entry.getValue() * 1.0 / max) * 10.0;
                    dificultadPuntaje.merge(entry.getKey(), valor, Double::sum);
                }
            }
            // 3. Respuesta del cuestionario (15%)
            if (respuestasCuestionario != null && respuestasCuestionario.getDificultad() != null) {
                dificultadPuntaje.merge(respuestasCuestionario.getDificultad(), 15.0, Double::sum);
            }
            // 4. Cursos seguidos (25%)
            if (cursosSeguidos != null && !cursosSeguidos.isEmpty()) {
                firestore.collection("cursos")
                        .whereIn("idCurso", cursosSeguidos)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            Map<String, Integer> conteo = new HashMap<>();
                            for (QueryDocumentSnapshot doc : snapshot) {
                                Curso curso = doc.toObject(Curso.class);
                                if (curso.getInstrumento() != null && curso.getInstrumento().containsKey(instrumentoClave)) {
                                    agregarDificultad(conteo, curso);
                                }
                            }
                            int max = conteo.values().stream().max(Integer::compare).orElse(1);
                            for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                                double valor = (entry.getValue() * 1.0 / max) * 25.0;
                                dificultadPuntaje.merge(entry.getKey(), valor, Double::sum);
                            }
                            procesarDificultadesDesdeClases(firestore, instrumentoClave, dificultadPuntaje, callback);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DificultadUsuario", "Error en cursos seguidos", e);
                            callback.onResult(Map.of("Principiante", 0));
                        });
            } else {
                procesarDificultadesDesdeClases(firestore, instrumentoClave, dificultadPuntaje, callback);
            }
        });
    }
    private void procesarDificultadesDesdeClases(FirebaseFirestore firestore, String instrumentoClave, Map<String, Double> dificultadPuntaje, DificultadCallback callback) {
        if (historialClases == null || historialClases.isEmpty()) {
            finalizarConteoDificultad(dificultadPuntaje, callback);
            return;
        }
        List<Integer> idsCursos = new ArrayList<>();
        for (Clase clase : historialClases) {
            if (clase.getIdCurso() != null && !idsCursos.contains(clase.getIdCurso())) {
                idsCursos.add(clase.getIdCurso());
            }
        }
        if (idsCursos.isEmpty()) {
            finalizarConteoDificultad(dificultadPuntaje, callback);
            return;
        }
        firestore.collection("cursos")
                .whereIn("idCurso", idsCursos)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, Integer> conteo = new HashMap<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Curso curso = doc.toObject(Curso.class);
                        if (curso.getInstrumento() != null && curso.getInstrumento().containsKey(instrumentoClave)) {
                            agregarDificultad(conteo, curso);
                        }
                    }
                    int max = conteo.values().stream().max(Integer::compare).orElse(1);
                    for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
                        double valor = (entry.getValue() * 1.0 / max) * 30.0;
                        dificultadPuntaje.merge(entry.getKey(), valor, Double::sum);
                    }
                    finalizarConteoDificultad(dificultadPuntaje, callback);
                })
                .addOnFailureListener(e -> {
                    Log.e("DificultadUsuario", "Error al obtener cursos desde clases", e);
                    callback.onResult(Map.of("Principiante", 0));
                });
    }
    private void agregarDificultad(Map<String, Integer> contador, Curso curso) {
        if (curso.getDificultad() != null) {
            for (String dificultad : curso.getDificultad().keySet()) {
                contador.put(dificultad, contador.getOrDefault(dificultad, 0) + 1);
            }
        }
    }

    private void finalizarConteoDificultad(Map<String, Double> dificultadPuntaje, DificultadCallback callback) {
        Map<String, Integer> resultado = new HashMap<>();

        if (dificultadPuntaje.isEmpty()) {
            resultado.put("Principiante", 0);
            callback.onResult(resultado);
            return;
        }
        String dificultadMasVista = null;
        double max = -1;
        for (Map.Entry<String, Double> entry : dificultadPuntaje.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                dificultadMasVista = entry.getKey();
            }
        }
        if (dificultadMasVista != null) {
            for (Map<String, Integer> dificultadMap : DataClusterList.listaDificultad) {
                if (dificultadMap.containsKey(dificultadMasVista)) {
                    resultado.put(dificultadMasVista, dificultadMap.get(dificultadMasVista));
                    break;
                }
            }
        }
        callback.onResult(resultado);
    }

    public void calcularClusterUsuario(FirebaseFirestore firestore, Usuario usuario, ClusterCallback callback) {
        obtenerInstrumentoMasVisto(firestore, instrumentoMap -> {
            // Si no se encontró instrumento, asignar "Guitarra" por defecto
            if (instrumentoMap == null || instrumentoMap.isEmpty()) {
                instrumentoMap = new HashMap<>();
                instrumentoMap.put("Guitarra", 0);
            }

            String instrumento = instrumentoMap.keySet().iterator().next();
            Integer instrumentoNum = instrumentoMap.get(instrumento);
            if (instrumento == null || instrumentoNum == null) {
                instrumento = "Guitarra";
                instrumentoNum = 0;
            }
            String finalInstrumento = instrumento;
            Integer finalInstrumentoNum = instrumentoNum;
            Map<String, Integer> finalInstrumentoMap = instrumentoMap;
            obtenerGeneroMasVisto(firestore, generoMap -> {
                // Si no se encontró género, asignar "Pop" por defecto
                if (generoMap == null || generoMap.isEmpty()) {
                    generoMap = new HashMap<>();
                    generoMap.put("Pop", 0);
                }

                String genero = generoMap.keySet().iterator().next();
                int generoNum = obtenerIndiceGenero(genero != null ? genero : "Pop");

                Map<String, Integer> finalGeneroMap = generoMap;
                obtenerDificultadMasVistaPorInstrumento(firestore, dificultadMap -> {
                    // Si no se encontró dificultad, asignar "Principiante" por defecto
                    if (dificultadMap == null || dificultadMap.isEmpty()) {
                        dificultadMap = new HashMap<>();
                        dificultadMap.put("Principiante", 0);
                    }

                    String dificultad = dificultadMap.keySet().iterator().next();
                    int dificultadNum = obtenerIndiceDificultad(dificultad != null ? dificultad : "Principiante");

                    Map<String, Integer> finalDificultadMap = dificultadMap;
                    firestore.collection("cursos")
                            .whereEqualTo("instrumento." + finalInstrumento, finalInstrumentoNum)
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                List<DataPoint> puntos = new ArrayList<>();

                                for (QueryDocumentSnapshot doc : snapshot) {
                                    Map<String, Long> dificultadDoc = (Map<String, Long>) doc.get("dificultad");
                                    Map<String, Long> generoDoc = (Map<String, Long>) doc.get("genero");

                                    if (dificultadDoc != null && generoDoc != null) {
                                        String difKey = dificultadDoc.keySet().iterator().next();
                                        String genKey = generoDoc.keySet().iterator().next();

                                        Integer d = obtenerIndiceDificultad(difKey);
                                        Integer g = obtenerIndiceGenero(genKey);

                                        if (d != null && g != null) {
                                            puntos.add(new DataPoint(new double[]{d, g}));
                                        }
                                    }
                                }

                                DataPoint puntoUsuario = new DataPoint(new double[]{dificultadNum, generoNum});
                                puntos.add(puntoUsuario);

                                int k = Math.min(3, puntos.size());
                                KMeans kmeans = new KMeans(k, 100);
                                kmeans.fit(puntos);

                                int clusterUsuario = kmeans.predict(puntoUsuario);
                                usuario.setCluster(clusterUsuario);

                                Log.d("ClusterUsuario", "Asignado cluster " + clusterUsuario + " al usuario");

                                if (usuario.getId() != null) {
                                    firestore.collection("usuarios")
                                            .document(usuario.getId())
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    // Traer todos los datos actuales del usuario
                                                    Usuario usuarioActual = documentSnapshot.toObject(Usuario.class);
                                                    if (usuarioActual == null) {
                                                        Log.e("ClusterUsuario", "No se pudo convertir el documento a Usuario");
                                                        return;
                                                    }

                                                    // Actualizamos campos manualmente
                                                    usuarioActual.setInstrumento(finalInstrumentoMap);
                                                    usuarioActual.setCluster(clusterUsuario);

                                                    if (finalGeneroMap != null && !finalGeneroMap.isEmpty()) {
                                                        documentSnapshot.getReference().update("genero", finalGeneroMap);
                                                    }

                                                    if (finalDificultadMap != null && !finalDificultadMap.isEmpty()) {
                                                        documentSnapshot.getReference().update("dificultad", finalDificultadMap);
                                                    }

                                                    // Actualizamos el documento entero (incluyendo instrumento y cluster)
                                                    firestore.collection("usuarios")
                                                            .document(usuario.getId())
                                                            .set(usuarioActual, SetOptions.merge())
                                                            .addOnSuccessListener(aVoid -> Log.d("ClusterUsuario", "Datos actualizados correctamente"))
                                                            .addOnFailureListener(e -> Log.e("ClusterUsuario", "Error al actualizar datos del usuario", e));

                                                } else {
                                                    Log.e("ClusterUsuario", "Documento del usuario no encontrado");
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("ClusterUsuario", "Error al obtener datos actuales del usuario", e));
                                }
                                usuario.setInstrumento(finalInstrumentoMap);
                                usuario.setCluster(clusterUsuario);
                                callback.onClusterReady(usuario);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ClusterUsuario", "Error al buscar cursos por instrumento", e);
                            });
                });
            });
        });
    }

    private int obtenerIndiceGenero(String genero) {
        for (Map<String, Integer> map : DataClusterList.listaGenero) {
            if (map.containsKey(genero)) return map.get(genero);
        }
        return 0;
    }

    private int obtenerIndiceDificultad(String dificultad) {
        for (Map<String, Integer> map : DataClusterList.listaDificultad) {
            if (map.containsKey(dificultad)) return map.get(dificultad);
        }
        return 0;
    }

}