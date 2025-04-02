package com.example.skulfulharmony.javaobjects.clustering;

import com.example.skulfulharmony.javaobjects.courses.Curso;

import java.util.List;

public class GestionClustering {
    private List<ClusterCursos> clusterCursos;

    public GestionClustering(List<ClusterCursos> clusterCursos) {
        this.clusterCursos = clusterCursos;
    }

    // Método para obtener todos los clusters de cursos
    public List<ClusterCursos> getClusters() {
        return this.clusterCursos;
    }

    // Método para agregar un nuevo cluster
    public void agregarCluster(ClusterCursos cluster) {
        this.clusterCursos.add(cluster);
    }
}
