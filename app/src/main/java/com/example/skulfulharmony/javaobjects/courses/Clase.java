package com.example.skulfulharmony.javaobjects.courses;

import android.media.Image;
import android.net.Uri;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.Pregunta;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class Clase {
    String titulo;
    Image imagen;
    ArrayList<Comentario> listaComentarios = new ArrayList<>();
    ArrayList<String> listaTextos = new ArrayList<>();
    ArrayList<File> listaArchivos = new ArrayList<>();
    Uri video;
    ArrayList<Pregunta> listaPreguntas = new ArrayList<>();
    ArrayList<Boolean> meGustas = new ArrayList<>();
    Double vistas;
    Calendar creacion;
    Calendar modificacion;

    public Clase(String titulo, Image imagen, ArrayList<String> listaTextos,
                 ArrayList<File> listaArchivos, Uri video,
                 ArrayList<Pregunta> listaPreguntas) {

        this.titulo = titulo;
        this.imagen = imagen;
        this.listaTextos = listaTextos;
        this.listaArchivos = listaArchivos;
        this.video = video;
        this.listaPreguntas = listaPreguntas;
        this.creacion = Calendar.getInstance();
        this.vistas = (double) 0;

    }
}
