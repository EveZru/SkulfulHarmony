package com.example.skulfulharmony.javaobjects.users;

import com.example.skulfulharmony.javaobjects.clasifications.Dificultad;

import java.util.Map;

public class ProgresoUsuario {
    private float indiceProgreso;
    private int respuestasCorrectas;
    private int respuestasIncorrectas;
    private float aciertosPorTiempo;
    private Map<Dificultad, Integer> dificultadesIngresadasPorTiempo;
    private float promedioMejora;
}
