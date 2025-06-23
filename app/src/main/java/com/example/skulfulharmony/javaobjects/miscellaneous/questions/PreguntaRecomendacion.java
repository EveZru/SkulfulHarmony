package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;
import java.util.Map;

public class PreguntaRecomendacion extends Pregunta{

    Map<String, Integer> instrumento;
    Map<String, Integer> dificultad;
    Map<String, Integer> genero;

    Integer respuestaElegida = null;

    public PreguntaRecomendacion(String pregunta, ArrayList<String> respuestas) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
    }

    public PreguntaRecomendacion(String pregunta, ArrayList<String> respuestas, Integer respuestaElegida) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaElegida = respuestaElegida;
    }


    public Integer getRespuestaElegida() {
        return respuestaElegida;
    }

    public void setRespuestaElegida(Integer respuestaElegida) {
        this.respuestaElegida = respuestaElegida;
    }

    public Map<String, Integer> getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(Map<String, Integer> instrumento) {
        this.instrumento = instrumento;
    }

    public Map<String, Integer> getDificultad() {
        return dificultad;
    }

    public void setDificultad(Map<String, Integer> dificultad) {
        this.dificultad = dificultad;
    }

    public Map<String, Integer> getGenero() {
        return genero;
    }

    public void setGenero(Map<String, Integer> genero) {
        this.genero = genero;
    }

}
