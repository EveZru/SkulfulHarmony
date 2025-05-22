package com.example.skulfulharmony.javaobjects.miscellaneous;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Denuncia {
    private String usuario;
    private String tipo;
    private String texto;
    private Timestamp fecha;
    private Integer idCurso;
    private Integer idClase;

    // Constructor sin argumentos
    public Denuncia() {
    }

    public Denuncia(String usuario, String tipo_denuncia, String denuncia, Integer idCurso) {
        this.usuario = usuario;
        this.tipo = tipo_denuncia;
        this.texto = denuncia;
        this.fecha = Timestamp.now();
        this.idCurso=idCurso;
    }
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTipo_denuncia() {
        return tipo;
    }

    public void setTipo_denuncia(String tipo_denuncia) {
        this.tipo = tipo_denuncia;
    }

    public String getDenuncia() {
        return texto;
    }

    public void setDenuncia(String denuncia) {
        this.texto = denuncia;
    }

    public Timestamp getFecha_denuncia() {
        return fecha;
    }

    public void setFecha_denuncia(Timestamp fecha_denuncia) {
        this.fecha = fecha_denuncia;
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
