package com.example.skulfulharmony.javaobjects.miscellaneous;

import java.util.ArrayList;

public class Pregunta {
    String cuestionamiento;
    Integer idRespuestaCorrecta;
    ArrayList<String> listaDeRespuestas;

    public Pregunta(String cuestionamiento, ArrayList<String> listaDeRespuestas, Integer idRespuestaCorrecta) {
        this.cuestionamiento = cuestionamiento;
        this.listaDeRespuestas = listaDeRespuestas;
        this.idRespuestaCorrecta = idRespuestaCorrecta;
    }

    public Pregunta(String cuestionamiento, ArrayList<String> listaDeRespuestas) {
        this.cuestionamiento = cuestionamiento;
        this.listaDeRespuestas = listaDeRespuestas;
        this.idRespuestaCorrecta = null;
    }
}
