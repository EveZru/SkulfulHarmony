package com.example.skulfulharmony.javaobjects.miscellaneous.questions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreguntaCuestionario extends Pregunta implements Serializable {

    Integer respuestaCorrecta;
    private Integer respuestaUsuario;

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



    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }
    public Integer getRespuestaUsuario() { return respuestaUsuario; }
    public void setRespuestaUsuario(Integer respuestaUsuario) { this.respuestaUsuario = respuestaUsuario; }
}
