package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ClusterCursos {
    private String id;

    public Instrumento getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(Instrumento instrumento) {
        this.instrumento = instrumento;
    }

    private Instrumento instrumento;

    public PuntoCentroide getCentroide() {
        return centroide;
    }

    public void setCentroide(PuntoCentroide centroide) {
        this.centroide = centroide;
    }

    private PuntoCentroide centroide;
    private List<Curso> cursos;

    public ClusterCursos(Instrumento instrumento, PuntoCentroide centroide) {
        this.instrumento = instrumento;
        this.centroide = centroide;
        this.cursos = new ArrayList<>();
    }

    public void actualizarCentroide(List<Curso> todosLosCursos) {
        this.centroide = KMeans.calcularNuevoCentroide(todosLosCursos, this);
    }

    public List<Curso> obtenerCursosRecomendados(Usuario usuario) {
        // Se puede usar el centroide para encontrar los cursos más cercanos a las preferencias del usuario
        List<Curso> cursosRecomendados = new ArrayList<>();
        for (Curso curso : cursos) {
            if (curso.getInstrumento().equals(usuario.getInstrumento())) {
                if (this.centroide.equals(curso.getCluster().getCentroide())) {
                    cursosRecomendados.add(curso);
                }
            }
        }
        return cursosRecomendados;
    }
}
