package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RanqueadorCluster {
    private Map<String,String> instrumentoPrincipal;
    private PuntoCentroide posicionUsuario;

    public void calcularInstrumentoPrincipal(List<Curso> historialCursos) {
        Map<Map<String,String>, Integer> conteoInstrumentos = new HashMap<>();
        for (Curso curso : historialCursos) {
            conteoInstrumentos.put(curso.getInstrumento(), conteoInstrumentos.getOrDefault(curso.getInstrumento(), 0) + 1);
        }
        this.instrumentoPrincipal = Collections.max(conteoInstrumentos.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public void calcularPosicionUsuario(List<Curso> historialCursos) {
        float sumaGenero = 0, sumaDificultad = 0;
        int count = 0;
        for (Curso curso : historialCursos) {
            if (curso.getInstrumento().equals(this.instrumentoPrincipal)) {
                sumaGenero += curso.getGenero().hashCode();
                sumaDificultad += curso.getDificultad().hashCode();
                count++;
            }
        }

        if (count > 0) {
            this.posicionUsuario = new PuntoCentroide(sumaGenero / count, sumaDificultad / count);
        }
    }

    public PuntoCentroide getPosicionUsuario() {
        return posicionUsuario;
    }

    public Map<String,String> getInstrumentoPrincipal() {
        return instrumentoPrincipal;
    }
}
