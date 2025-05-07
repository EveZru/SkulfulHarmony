package com.example.skulfulharmony.utils;

import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;

import java.util.*;

public class IndiceInvertidoManager {

    private final Map<String, List<Object>> indice = new HashMap<>();

    public void agregarCurso(Curso curso) {
        if (curso == null) return;
        indexarPalabras(curso.getTitulo(), curso);
        indexarPalabras(curso.getDescripcion(), curso);
        indexarPalabras(curso.getCreador(), curso);
    }

    public void agregarClase(Clase clase) {
        if (clase == null) return;
        indexarPalabras(clase.getTitulo(), clase);
        indexarPalabras(clase.getNombreCurso(), clase);
    }

    public void agregarUsuario(Usuario usuario) {
        if (usuario == null) return;
        indexarPalabras(usuario.getNombre(), usuario);
        indexarPalabras(usuario.getCorreo(), usuario);
    }

    private void indexarPalabras(String texto, Object objeto) {
        if (texto == null || texto.isEmpty()) return;

        String[] palabras = texto.toLowerCase().split("[\\s.,;:!?()\"']+");
        for (String palabra : palabras) {
            if (palabra.trim().isEmpty()) continue;
            indice.computeIfAbsent(palabra, k -> new ArrayList<>()).add(objeto);
        }
    }

    public List<Object> buscar(String textoBusqueda) {
        Set<Object> resultados = new HashSet<>();
        String[] palabras = textoBusqueda.toLowerCase().split("[\\s.,;:!?()\"']+");

        for (String palabra : palabras) {
            List<Object> encontrados = indice.get(palabra);
            if (encontrados != null) {
                resultados.addAll(encontrados);
            }
        }

        return new ArrayList<>(resultados);
    }

    public void limpiarIndice() {
        indice.clear();
    }
}