package com.example.skulfulharmony.javaobjects.courses;

import com.example.skulfulharmony.javaobjects.miscellaneous.Comentario;

import java.util.ArrayList;
import java.util.List;

public class CalificacionCurso {
    private List<Comentario> comentarios;
    private List<Integer> estrellas;
    private float indicePopularidad;


    public CalificacionCurso() {
        this.estrellas = new ArrayList<>();
        Integer una=0, dos=0, tres=0, cuatro=0, cinco=0;
        estrellas.add(una);
        estrellas.add(dos);
        estrellas.add(tres);
        estrellas.add(cuatro);
        estrellas.add(cinco);
    }
}
