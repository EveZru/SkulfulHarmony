package com.example.skulfulharmony.javaobjects.miscellaneous;

import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Comentario {
    private String texto;
    private String usuario;
    private ArrayList<String> reacciones = new ArrayList<>();
    private Timestamp fecha;
    private Integer idCurso;
    private Integer idClase;
    private Integer idComentario;


    public Comentario() {
    }


    public Integer getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(Integer idComentario) {
        this.idComentario = idComentario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public ArrayList<String> getReacciones() {
        return reacciones;
    }

    public void setReacciones(ArrayList<String> reacciones) {
        this.reacciones = reacciones;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public Integer getIdClase() {
        return idClase;
    }

    public void setIdClase(Integer idClase) {
        this.idClase = idClase;
    }
}
