package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.ArrayList;
import java.util.List;

public class ClusterClases {
    private String id;
    private Instrumento instrumento;
    private PuntoCentroide centroide;
    private List<Curso> cursos;

    public ClusterClases(Instrumento instrumento, PuntoCentroide centroide) {
        this.instrumento = instrumento;
        this.centroide = centroide;
        this.cursos = new ArrayList<>();
    }

    public void actualizarCentroide(List<Curso> todosLosCursos) {
        this.centroide = KMeans.calcularNuevoCentroide(todosLosCursos, this);
    }
}
