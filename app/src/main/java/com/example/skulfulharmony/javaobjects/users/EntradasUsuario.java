package com.example.skulfulharmony.javaobjects.users;

import com.google.firebase.Timestamp;
import java.util.List;

public class EntradasUsuario {

    private int idSemana;
    private List<Timestamp> vecesEntrada;

    // Constructor vacío requerido por Firebase
    public EntradasUsuario() {}

    public EntradasUsuario(int idSemana, List<Timestamp> vecesEntrada) {
        this.idSemana = idSemana;
        this.vecesEntrada = vecesEntrada;
    }

    public int getIdSemana() {
        return idSemana;
    }

    public void setIdSemana(int idSemana) {
        this.idSemana = idSemana;
    }

    public List<Timestamp> getVecesEntrada() {
        return vecesEntrada;
    }

    public void setVecesEntrada(List<Timestamp> vecesEntrada) {
        this.vecesEntrada = vecesEntrada;
    }
}