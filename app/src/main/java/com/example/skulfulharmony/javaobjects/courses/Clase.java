package com.example.skulfulharmony.javaobjects.courses;

import android.content.Context;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.Pregunta;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.google.firebase.Timestamp;

import org.apache.commons.net.ntp.TimeStamp;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clase implements Serializable {

    private Integer idClase;
    private String nombreCurso;
    private Integer idCurso;
    private String titulo;
    private String textos;
    private String imagen;
    private List<String> archivos;
    private String video;
    private List<PreguntaCuestionario> preguntas;
    private List<Comentario> comentarios;
    private Integer meGusta;
    private Integer noGusta;
    private String creadorUid;
    private String creadorCorreo;
    private Map<String, Boolean> calificacionPorUsuario = new HashMap<>(); // true = like, false = dislike
    private Timestamp fechaCreacionf;
    private Timestamp fechaAcceso;
    private String contenido;
    private String videoUrl;
    private transient Context context;


    public Clase() {
        this.archivos = new ArrayList<>();
        this.preguntas = new ArrayList<>();
        this.comentarios = new ArrayList<>();
    }
    public Clase(String titulo, List<PreguntaCuestionario> preguntas) {
        this.titulo = titulo;
        this.preguntas = preguntas;
        this.fechaCreacionf = new Timestamp(new Date());
    }

    public Clase(String titulo, String nombreCurso, String imagen, List<PreguntaCuestionario>  preguntaCuestionarios){
        this.titulo = titulo;
        this.nombreCurso = nombreCurso;
        this.imagen = imagen;
        preguntas = preguntaCuestionarios;
    }



    public Map<String, Boolean> getCalificacionPorUsuario() {
        return calificacionPorUsuario;
    }

    public void setCalificacionPorUsuario(Map<String, Boolean> calificacionPorUsuario) {
        this.calificacionPorUsuario = calificacionPorUsuario;
    }
    public Integer getNoGusta() {
        return noGusta;
    }

    public void setNoGusta(Integer noGusta) {
        this.noGusta = noGusta;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios){
        this.comentarios = comentarios;
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

    public List<String> getArchivos() {
        return archivos;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setArchivos(List<String> archivos) {
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

    public String getContenido() {
        return contenido;
    }

    public String getVideoUrl() {
        return videoUrl;
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

    public Timestamp getFechaCreacionf() {
        return fechaCreacionf;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacionf = fechaCreacion;
    }

    public Integer getIdClase() {
        return idClase;
    }

    public void setIdClase(Integer idClase) {
        this.idClase = idClase;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public List<PreguntaCuestionario> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<PreguntaCuestionario> preguntas) {
        this.preguntas = preguntas;
    }

    public void setFechaCreacionf(Timestamp fechaCreacionf) {
        this.fechaCreacionf = fechaCreacionf;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }


    public Integer getMeGusta() {
        return meGusta;
    }

    public void setMeGusta(Integer meGusta) {
        this.meGusta = meGusta;
    }

    public Timestamp getFechaAcceso() { return fechaAcceso; }

    public void setFechaAcceso( Timestamp fechaAcceso) { this.fechaAcceso = fechaAcceso; }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
    public void setCreadorUid(String creadorUid) { this.creadorUid = creadorUid; }
    public void setCreadorCorreo(String creadorCorreo) { this.creadorCorreo = creadorCorreo; }
    public String getCreadorUid() {
        return creadorUid;
    }
    public String getCreadorCorreo() {
        return creadorCorreo;
    }

}
