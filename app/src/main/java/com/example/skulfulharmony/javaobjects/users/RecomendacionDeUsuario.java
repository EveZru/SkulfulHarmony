//package com.example.skulfulharmony.javaobjects.users;
//
//import com.example.skulfulharmony.javaobjects.courses.Curso;
//
//import java.util.List;
//
//public class RecomendacionDeUsuario {
//
//    private List<ClusterCursos> clusterClases;
//    private RanqueadorCluster ranqueador;
//    private GestionClustering gestionClustering;  // Gestión de los clústeres
//
//
//
//    // Constructor actualizado para incluir GestionClustering
//    public RecomendacionDeUsuario(RanqueadorCluster ranqueador, GestionClustering gestionClustering) {
//        this.ranqueador = ranqueador;
//        this.gestionClustering = gestionClustering;
//    }
//
//    public List<Curso> generarRecomendaciones(Usuario usuario) {
//        // Calcular el instrumento principal del usuario
//        ranqueador.calcularInstrumentoPrincipal(usuario.getHistorialCursos());
//
//        // Obtener los cursos recomendados
//        for (ClusterCursos cluster : gestionClustering.getClusters()) {
//            if (cluster.getInstrumento().equals(ranqueador.getInstrumentoPrincipal())) {
//                return cluster.obtenerCursosRecomendados(usuario);
//            }
//        }
//        return null;  // Si no hay recomendaciones disponibles
//    }
//}
