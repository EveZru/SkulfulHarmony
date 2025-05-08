package com.example.skulfulharmony.utils;

import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndiceInvertidoManager {
    // Índice invertido para cursos
    private Map<String, List<String>> invertedIndexCursos = new HashMap<>();
    // Índice invertido para usuarios
    private Map<String, List<String>> invertedIndexUsuarios = new HashMap<>();

    // Método para construir el índice invertido para cursos
    public void buildIndexCursos(List<Curso> cursos) {
        invertedIndexCursos.clear();
        for (Curso curso : cursos) {
            if (curso != null && curso.getTitulo() != null) {
                String[] words = curso.getTitulo().toLowerCase().split("\\s+"); // Dividir el título en palabras
                for (String word : words) {
                    invertedIndexCursos.computeIfAbsent(word, k -> new ArrayList<>()).add(String.valueOf(curso.getId()));
                }
            }
        }
    }

    // Método para construir el índice invertido para usuarios
    public void buildIndexUsuarios(List<Usuario> usuarios) {
        invertedIndexUsuarios.clear();
        for (Usuario usuario : usuarios) {
            if (usuario != null && usuario.getNombre() != null) {
                String[] words = usuario.getNombre().toLowerCase().split("\\s+"); // Dividir el nombre en palabras
                for (String word : words) {
                    invertedIndexUsuarios.computeIfAbsent(word, k -> new ArrayList<>()).add(usuario.getCorreo());
                }
            }
        }
    }

    // Método para buscar cursos por una palabra clave
    public List<String> searchCursos(String query) {
        return invertedIndexCursos.getOrDefault(query.toLowerCase(), new ArrayList<>());
    }

    // Método para buscar usuarios por una palabra clave
    public List<String> searchUsuarios(String query) {
        return invertedIndexUsuarios.getOrDefault(query.toLowerCase(), new ArrayList<>());
    }
}