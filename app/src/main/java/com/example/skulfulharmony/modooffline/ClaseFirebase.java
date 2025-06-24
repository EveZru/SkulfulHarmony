package com.example.skulfulharmony.modooffline;

import java.io.Serializable;
import java.util.List;

public class ClaseFirebase implements Serializable {
    private String titulo;
    private List<String> archivosUrl;
    private String imagenUrl;
    private String videoUrl;

    public ClaseFirebase() {}

    public ClaseFirebase(String titulo, List<String> archivos, String imagenUrl, String videoUrl) {
        this.titulo = titulo;
        this.archivosUrl = archivos;
        this.imagenUrl = imagenUrl;
        this.videoUrl = videoUrl;
    }

    public String getTitulo() { return titulo; }
    public List<String> getArchivosUrl() { return archivosUrl; }
    public String getImagenUrl() { return imagenUrl; }
    public String getVideoUrl() { return videoUrl; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setArchivosUrl(List<String> archivosUrl) { this.archivosUrl = archivosUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
