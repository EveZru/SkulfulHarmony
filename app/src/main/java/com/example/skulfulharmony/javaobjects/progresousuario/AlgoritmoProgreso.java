package com.example.skulfulharmony.javaobjects.progresousuario;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.questions.PreguntaCuestionario;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.*;
import java.util.stream.Collectors;

public class AlgoritmoProgreso {

    private static final double ALPHA = 0.4; // peso calificación
    private static final double BETA = 0.3;  // peso frecuencia
    private static final double GAMMA = 0.3; // peso avance dificultad
    private static final int MAX_NIVEL = 3;  // Principiante=1, Intermedio=2, Avanzado=3

    public static double calcularProgreso(Usuario usuario, List<Curso> todosLosCursos) {

        double promedioCalif = calcularPromedioCalificacion(usuario.getHistorialClases());
        double frecuencia = calcularFrecuencia(usuario.getFechasAcceso());
        double avanceDificultad = calcularAvanceDificultad(usuario.getHistorialClases(), todosLosCursos);

        return ALPHA * promedioCalif + BETA * frecuencia + GAMMA * avanceDificultad;
    }

    private static double calcularPromedioCalificacion(List<Clase> clases) {
        if (clases == null || clases.isEmpty()) return 0;

        int totalIntentos = 0;
        int totalErrores = 0;

        for (Clase clase : clases) {
            String nombreCurso = clase.getNombreCurso(); // o usa clase.getIdCurso() si es más confiable
            SharedPreferences prefs = clase.getContext().getSharedPreferences("estadisticas_" + nombreCurso, Context.MODE_PRIVATE);

            int errores = prefs.getInt("errores_totales", 0);
            int intentos = prefs.getInt("intentos_totales", 0);

            totalErrores += errores;
            totalIntentos += intentos;
        }

        if (totalIntentos == 0) return 1.0; // asume perfecto si no hay errores registrados aún

        double promedio = 1.0 - ((double) totalErrores / (totalIntentos * 3)); // 3 preguntas por intento
        return Math.max(0, Math.min(promedio, 1.0));
    }


    private static double calcularFrecuencia(List<Date> fechasAcceso) {
        if (fechasAcceso == null || fechasAcceso.isEmpty()) return 0;

        Set<String> diasUnicos = new HashSet<>();
        Date hoy = new Date();
        Calendar calLimite = Calendar.getInstance();
        calLimite.setTime(hoy);
        calLimite.add(Calendar.DAY_OF_YEAR, -30);
        Date limite = calLimite.getTime();

        for (Date fecha : fechasAcceso) {
            if (fecha.after(limite)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fecha);
                String dia = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                diasUnicos.add(dia);
            }
        }

        double frecuencia = (double) diasUnicos.size() / 30.0;
        return Math.min(frecuencia, 1.0);
    }

    private static double calcularAvanceDificultad(List<Clase> clases, List<Curso> todosLosCursos) {
        if (clases == null || clases.isEmpty()) return 0;

        // Ordena clases por fecha de acceso ascendente
        clases.sort(Comparator.comparing(c -> c.getFechaAcceso().toDate()));

        int nivelInicial = Integer.MAX_VALUE;
        int nivelActual = Integer.MIN_VALUE;

        for (Clase clase : clases) {
            Curso curso = buscarCursoPorId(clase.getIdCurso(), todosLosCursos);
            if (curso != null) {
                int nivelCurso = curso.getNivelDificultad();
                if (nivelCurso < nivelInicial) nivelInicial = nivelCurso;
                if (nivelCurso > nivelActual) nivelActual = nivelCurso;
            }
        }

        if (nivelInicial == Integer.MAX_VALUE || nivelActual == Integer.MIN_VALUE) return 0;

        if (nivelActual <= nivelInicial) return 0;

        return (double)(nivelActual - nivelInicial) / (MAX_NIVEL - nivelInicial);
    }

    private static Curso buscarCursoPorId(int idCurso, List<Curso> cursos) {
        if (cursos == null) return null;
        for (Curso c : cursos) {
            if (c.getIdCurso() != null && c.getIdCurso() == idCurso) {
                return c;
            }
        }
        return null;
    }
}