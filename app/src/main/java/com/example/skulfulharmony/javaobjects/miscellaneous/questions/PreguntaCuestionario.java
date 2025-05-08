package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.util.ArrayList;
import java.util.List;

public class PreguntaCuestionario extends Pregunta {

    Integer respuestaCorrecta;

    public PreguntaCuestionario(String pregunta, List<String> respuestas, Integer respuestaCorrecta) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public PreguntaCuestionario() {
        this.respuestas = new ArrayList<>(); // Inicializa la lista de respuestas
    }

    public Integer getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(Integer respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
    public List<String> getRespuestas() {
        return respuestas;
    }
}
