package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

public class KMeans {
    public static PuntoCentroide calcularNuevoCentroide(List<Curso> cursos, ClusterCursos cluster) {
        float sumaGenero = 0, sumaDificultad = 0;
        int count = 0;

        for (Curso curso : cursos) {
            if (curso.getInstrumento().equals(cluster.getInstrumento())) {
                sumaGenero += curso.getGenero().hashCode();  // Usar el hashcode como representación numérica del género
                sumaDificultad += curso.getDificultad().hashCode();  // Lo mismo para la dificultad
                count++;
            }
        }

        if (count > 0) {
            return new PuntoCentroide(sumaGenero / count, sumaDificultad / count);
        }

        return new PuntoCentroide(0.5f, 0.5f);  // Retorna un centroide por defecto si no hay cursos
    }
}
