package com.example.skulfulharmony.javaobjects.clustering;

import java.util.HashMap;
import java.util.Map;

public class PreferenciasUsuario {
    private Map<String, Integer> instrumentos = new HashMap<>() ;
    private Map<String, Integer> generos = new HashMap<>();
    private Map<String, Integer> dificultades = new HashMap<>();

    public PreferenciasUsuario() {}

    public void incrementarInstrumento(String instrumento) {
        instrumentos.put(instrumento, instrumentos.getOrDefault(instrumento, 0) + 1);
    }

    public void incrementarGenero(String genero) {
        generos.put(genero, generos.getOrDefault(genero, 0) + 1);
    }

    public void incrementarDificultad(String dificultad) {
        dificultades.put(dificultad, dificultades.getOrDefault(dificultad, 0) + 1);
    }

    public void decrementarInstrumento(String instrumento) {
        if (instrumentos.containsKey(instrumento)) {
            int valor = instrumentos.get(instrumento) - 1;
            if (valor <= 0) instrumentos.remove(instrumento);
            else instrumentos.put(instrumento, valor);
        }
    }

    public void decrementarGenero(String genero) {
        if (generos.containsKey(genero)) {
            int valor = generos.get(genero) - 1;
            if (valor <= 0) generos.remove(genero);
            else generos.put(genero, valor);
        }
    }

    public void decrementarDificultad(String dificultad) {
        if (dificultades.containsKey(dificultad)) {
            int valor = dificultades.get(dificultad) - 1;
            if (valor <= 0) dificultades.remove(dificultad);
            else dificultades.put(dificultad, valor);
        }
    }

    // Getters & Setters
    public Map<String, Integer> getInstrumentos() { return instrumentos; }
    public Map<String, Integer> getGeneros() { return generos; }
    public Map<String, Integer> getDificultades() { return dificultades; }
    public void setInstrumentos(Map<String, Integer> instrumentos) { this.instrumentos = instrumentos; }
    public void setGeneros(Map<String, Integer> generos) { this.generos = generos; }
    public void setDificultades(Map<String, Integer> dificultades) { this.dificultades = dificultades; }
}
