package com.example.skulfulharmony.javaobjects.miscellaneous;

import java.util.ArrayList;
import java.util.Date;

public class Pregunta {
    private String pregunta;
    private Date fecha;
    ArrayList<String> respuestas = new ArrayList<>();
    Integer respuestaCorrecta;

    public Pregunta(String pregunta, ArrayList<String> respuestas, Integer respuestaCorrecta) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
        this.fecha = new Date();
    }

    public Pregunta(String pregunta, ArrayList<String> respuestas) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.fecha = new Date();
        this.respuestaCorrecta = null;
    }


    public String getPregunta() {
        return pregunta;
    }

    public Date getFecha() {
        return fecha;
    }

}
