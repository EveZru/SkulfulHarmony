package com.example.skulfulharmony.javaobjects.miscellaneous;

import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Comentario implements Serializable {
    private String texto;
    private Usuario usuario;
    private ArrayList<Boolean> reacciones = new ArrayList<>();
    private Date fecha;

    public Comentario() {
    }

    void DarMeGusta(Boolean x){
        reacciones.add(x);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        //this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public ArrayList<Boolean> getReacciones() {
        return reacciones;
    }

    public void setReacciones(ArrayList<Boolean> reacciones) {
        this.reacciones = reacciones;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
