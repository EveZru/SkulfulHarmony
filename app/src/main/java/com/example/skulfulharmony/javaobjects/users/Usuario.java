package com.example.skulfulharmony.javaobjects.users;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.Pregunta;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaInicio;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Usuario {

    //ATRIBUTOS DE IDENTIFICACION
    private String imagen;
    private String nombre;
    private String user;
    private String correo;
    private Instrumento instrumento;

    //ATRIBUTOS DINAMICOS
    private List<Comentario> comentarios;
    private List<Curso> cursosSeguidos;
    private List<Curso> historialCursos;
    private List<Clase> historialClases;
    private List<PreguntaInicio> preguntasInicio;
    private RecomendacionDeUsuario recomendacionesUsuario;
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

    public void actualizarPreguntasInicio(List<PreguntaInicio> todasLasPreguntas) {
        Date haceDosSemanas = new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000);
        this.preguntasInicio = new ArrayList<>();
        for (PreguntaInicio pregunta : todasLasPreguntas) {
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

    //GETTERS AND SETTERS
            public String getImagen () {
            return imagen;
        }

            public void setImagen (String imagen){
            this.imagen = imagen;
        }

            public String getNombre () {
            return nombre;
        }

            public void setNombre (String nombre){
            this.nombre = nombre;
        }

            public String getUser () {
            return user;
        }

            public void setUser (String user){
            this.user = user;
        }

            public String getCorreo () {
            return correo;
        }

            public void setCorreo (String correo){
            this.correo = correo;
        }

            public Instrumento getInstrumento () {
            return instrumento;
        }

            public void setInstrumento (Instrumento instrumento){
            this.instrumento = instrumento;
        }


}
