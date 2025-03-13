package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;

public class PreguntaRecomendacion extends Pregunta{

    Integer respuestaElegida;

    public PreguntaRecomendacion(String pregunta, ArrayList<String> respuestas) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
    }

    public Integer getRespuestaElegida() {
        return respuestaElegida;
    }

    public void setRespuestaElegida(Integer respuestaElegida) {
        this.respuestaElegida = respuestaElegida;
    }


}
