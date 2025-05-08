package com.example.skulfulharmony.javaobjects.miscellaneous;

import java.util.Date;

public class Demanda {
    private String usuario;
    private String tipo_denuncia;
    private String denuncia;
    private Date fecha_denuncia;
    private Integer idCurso;
    private Integer idClase;

    public Demanda(String usuario, String tipo_denuncia, String denuncia,Integer idCurso) {
        this.usuario = usuario;
        this.tipo_denuncia = tipo_denuncia;
        this.denuncia = denuncia;
        this.fecha_denuncia = new Date();
        this.idCurso=idCurso;
    }
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTipo_denuncia() {
        return tipo_denuncia;
    }

    public void setTipo_denuncia(String tipo_denuncia) {
        this.tipo_denuncia = tipo_denuncia;
    }

    public String getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(String denuncia) {
        this.denuncia = denuncia;
    }

    public Date getFecha_denuncia() {
        return fecha_denuncia;
    }

    public void setFecha_denuncia(Date fecha_denuncia) {
        this.fecha_denuncia = fecha_denuncia;
    }

}
