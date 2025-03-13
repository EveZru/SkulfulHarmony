package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;
import java.util.Date;

public class PreguntaInicio extends Pregunta{

    Integer respuestaCorrecta;

    Date fecha;

    public PreguntaInicio(String pregunta, ArrayList<String> respuestas, Integer respuestaCorrecta, Date fecha) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
        this.fecha = fecha;
    }

    public Date getFecha() {
        return fecha;
    }

}
