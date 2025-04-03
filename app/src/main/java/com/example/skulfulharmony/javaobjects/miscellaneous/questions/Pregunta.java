package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;
import java.util.Date;

public class Pregunta {

    String pregunta;
    ArrayList<String> respuestas = new ArrayList<>();

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public ArrayList<String> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(ArrayList<String> respuestas) {
        this.respuestas = respuestas;
    }

    public String getPregunta() {
        return pregunta;
    }



}
