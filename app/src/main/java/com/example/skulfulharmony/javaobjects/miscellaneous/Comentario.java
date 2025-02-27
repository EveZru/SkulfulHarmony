package com.example.skulfulharmony.javaobjects.miscellaneous;

import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.ArrayList;
import java.util.Date;

public class Comentario {
    private String texto;
    private Usuario usuario;
    private ArrayList<Boolean> reacciones = new ArrayList<>();
    private Date fecha;

    public Comentario(Usuario usuario, String texto) {
        this.usuario = usuario;
        this.texto = texto;
        this.fecha = new Date();
    }
}
