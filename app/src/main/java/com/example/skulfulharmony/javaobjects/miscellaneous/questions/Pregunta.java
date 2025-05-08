package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pregunta {

    String pregunta;
    List<String> respuestas = new ArrayList<>();

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public List<String> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }

    public String getPregunta() {
        return pregunta;
    }



}
