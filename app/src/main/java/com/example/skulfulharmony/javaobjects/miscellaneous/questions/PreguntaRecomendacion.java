package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;

public class PreguntaRecomendacion extends Pregunta{

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


}
