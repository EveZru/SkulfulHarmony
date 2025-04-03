package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RanqueadorCluster {
    private Instrumento instrumentoPrincipal;
    private PuntoCentroide posicionUsuario;

    public void calcularInstrumentoPrincipal(List<Curso> historialCursos) {
        Map<Instrumento, Integer> conteoInstrumentos = new HashMap<>();
        for (Curso curso : historialCursos) {
            conteoInstrumentos.put(curso.instrumento, conteoInstrumentos.getOrDefault(curso.instrumento, 0) + 1);
        }
        this.instrumentoPrincipal = Collections.max(conteoInstrumentos.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public void calcularPosicionUsuario(List<Curso> historialCursos) {
        float sumaGenero = 0, sumaDificultad = 0;
        int count = 0;
        for (Curso curso : historialCursos) {
            if (curso.instrumento.equals(this.instrumentoPrincipal)) {
                sumaGenero += curso.genero.hashCode();
                sumaDificultad += curso.dificultad.hashCode();
                count++;
            }
        }
        this.posicionUsuario = new PuntoCentroide(sumaGenero / count, sumaDificultad / count);
    }
}
