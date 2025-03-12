package com.example.skulfulharmony.javaobjects.users;

import android.media.Image;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.Pregunta;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Usuario {
    private Image imagen;
    private String nombre;
    private String user;
    private String correo;
    private List<Comentario> comentarios;
    private List<Curso> cursosSeguidos;
    private List<Clase> historialClases;
    private List<Pregunta> preguntasInicio;
    private RecomendacionDeUsuario recomendacionesUsuario;
    private Instrumento instrumento;
    private List<Date> horasEntrada;
    private LocalTime tiempoDeNotificacion;

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

    public void actualizarPreguntasInicio(List<Pregunta> todasLasPreguntas) {
        Date haceDosSemanas = new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000);
        this.preguntasInicio = new ArrayList<>();
        for (Pregunta pregunta : todasLasPreguntas) {
            if (pregunta.getFecha().after(haceDosSemanas)) {
                this.preguntasInicio.add(pregunta);
            }
        }
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
            this.tiempoDeNotificacion = LocalTime.of(promedioHora, promedioMinuto).plusMinutes(20);
        } else {
            this.tiempoDeNotificacion = null; // No hay datos recientes
        }
    }
}
