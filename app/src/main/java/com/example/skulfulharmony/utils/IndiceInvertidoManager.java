package com.example.skulfulharmony.utils;

import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class IndiceInvertidoManager {
    // Índice invertido que mapea cada palabra a una lista de ids de cursos.
    private Map<String, List<String>> invertedIndex = new HashMap<>();

    // Método para construir el índice invertido a partir de una lista de cursos.
    public void buildIndex(List<Curso> cursos) {
        invertedIndex.clear();
        for (Curso curso : cursos) {
            if (curso != null && curso.getTitulo() != null) {
                String[] words = curso.getTitulo().toLowerCase().split("\\s+"); // Dividir el título en palabras
                for (String word : words) {
                    // Convertir el ID a String antes de agregarlo al índice
                    invertedIndex.computeIfAbsent(word, k -> new ArrayList<>())
                            .add(String.valueOf(curso.getId())); // Convertir Integer a String
                }
            }
        }
    }
    // Método para buscar cursos por una palabra clave.
    public List<String> search(String query) {
        return invertedIndex.getOrDefault(query.toLowerCase(), new ArrayList<>());
    }
}
