package com.example.skulfulharmony.javaobjects.courses;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;

import java.util.ArrayList;
import java.util.List;

public class CalificacionCurso {
    private List<Comentario> comentarios;
    private List<Integer> estrellas;
    private float indicePopularidad;
    private Integer puntuacion;
    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }




    public CalificacionCurso() {
        this.estrellas = new ArrayList<>();

    }



}
