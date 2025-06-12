package com.example.skulfulharmony.modooffline;

import java.io.Serializable;

public class ClaseFirebase implements Serializable {
    private String titulo;
    private String documentoUrl;
    private String imagenUrl;
    private String videoUrl;

    public ClaseFirebase() {}

    public ClaseFirebase(String titulo, String documento, String imagenUrl, String videoUrl) {
        this.titulo = titulo;
        this.documentoUrl = documento;
        this.imagenUrl = imagenUrl;
        this.videoUrl = videoUrl;
    }

    public String getTitulo() { return titulo; }
    public String getDocumentoUrl() { return documentoUrl; }
    public String getImagenUrl() { return imagenUrl; }
    public String getVideoUrl() { return videoUrl; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDocumentoUrl(String documentoUrl) { this.documentoUrl = documentoUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}