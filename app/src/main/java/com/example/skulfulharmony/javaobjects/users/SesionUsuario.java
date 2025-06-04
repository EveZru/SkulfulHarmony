package com.example.skulfulharmony.javaobjects.users;

import com.google.firebase.Timestamp;

public class SesionUsuario {

    private String fecha;
    private Timestamp inicio;
    private long duracion; // en segundos o minutos, tú decides cómo manejarlo

    // Constructor vacío (necesario para Firebase)
    public SesionUsuario() {}

    // Constructor completo
    public SesionUsuario(String fecha, Timestamp inicio, long duracion) {
        this.fecha = fecha;
        this.inicio = inicio;
        this.duracion = duracion;
    }

    // Getters y Setters

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Timestamp getInicio() {
        return inicio;
    }

    public void setInicio(Timestamp inicio) {
        this.inicio = inicio;
    }

    public long getDuracion() {
        return duracion;
    }

    public void setDuracion(long duracion) {
        this.duracion = duracion;
    }

    @Override
    public String toString() {
        return "SesionUsuario{" +
                "fecha='" + fecha + '\'' +
                ", inicio=" + inicio +
                ", duracion=" + duracion +
                '}';
    }
}