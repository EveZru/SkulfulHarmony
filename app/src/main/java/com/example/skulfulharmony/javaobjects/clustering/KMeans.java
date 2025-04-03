package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

public class KMeans {
    public static PuntoCentroide calcularNuevoCentroide(List<Curso> cursos, ClusterClases cluster) {
        return new PuntoCentroide(0.5f, 0.5f);
    }

}
