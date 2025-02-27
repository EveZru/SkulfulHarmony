package com.example.skulfulharmony.javaobjects.users;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.Pregunta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Usuario {
    private Image imagen;
    private String nombre;
    private String user;
    private String contrasena;
    private String correo;
    private List<Comentario> comentarios;
    private List<Curso> cursosSeguidos;
    private List<Clase> historialClases;
    private List<Pregunta> preguntasInicio;
    private RecomendacionDeUsuario recomendacionesUsuario;
    private Instrumento instrumento;

    public Usuario(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public void actualizarPreguntasInicio(List<Pregunta> todasLasPreguntas) {
        Date haceDosSemanas = new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000);
        this.preguntasInicio = new ArrayList<>();
        for (Pregunta pregunta : todasLasPreguntas) {
            if (pregunta.getFecha().after(haceDosSemanas)) {
                this.preguntasInicio.add(pregunta);
            }
        }
    }
}
