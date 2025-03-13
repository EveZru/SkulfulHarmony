package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;

public class PreguntaCuestionario extends Pregunta {

    Integer respuestaCorrecta;

    public PreguntaCuestionario(String pregunta, ArrayList<String> respuestas, Integer respuestaCorrecta) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
    }

}
