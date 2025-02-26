package com.example.skulfulharmony.javaobjects.miscellaneous;

import java.util.ArrayList;
import java.util.Calendar;

public class Comentario {

    private String texto;
    private String idUsuario;
    private ArrayList<Boolean> meGustas = new ArrayList<>();
    private Calendar fecha;

    public Comentario(String texto, String idUsuario, ArrayList<Boolean> meGustas) {
        this.texto = texto;
        this.idUsuario = idUsuario;
        this.meGustas = meGustas;
        fecha = Calendar.getInstance();
    }
}
