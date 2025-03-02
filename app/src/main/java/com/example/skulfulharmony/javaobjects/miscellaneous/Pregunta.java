package com.example.skulfulharmony.javaobjects.miscellaneous;

import java.util.ArrayList;
import java.util.Date;

public class Pregunta {
    private String pregunta;
    private Date fecha;
    ArrayList<String> respuestas = new ArrayList<>();
    Integer respuestaCorrecta;
    Integer respuestaElegida;





    //PARA CREAR UN CUESTIONARIO DE USUARIO
    public Pregunta(String pregunta, ArrayList<String> respuestas, Integer respuestaCorrecta) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
        this.fecha = null;
        this.respuestaElegida = null;
    }

    //   5 PREGUNTAS AL ENTRAR A LA APP
    public Pregunta(String pregunta, ArrayList<String> respuestas, Integer respuestaCorrecta, Date fecha) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
        this.fecha = fecha;
        this.respuestaElegida = null;
    }

    // PREGUNTAS DE RECOMENDACION
    public Pregunta(String pregunta, ArrayList<String> respuestas) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
        this.fecha = null;
        this.respuestaCorrecta = null;
    }


    public String getPregunta() {
        return pregunta;
    }

    public Date getFecha() {
        return fecha;
    }

    public Integer getRespuestaElegida() {
        return respuestaElegida;
    }

    public void setRespuestaElegida(Integer respuestaElegida) {
        this.respuestaElegida = respuestaElegida;
    }
}
