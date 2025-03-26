package com.example.skulfulharmony.server.config;

public class ConfiguracionFTP {

    private String servidorFTP;
    private String nombreUsuario;
    private String contrasena;
    private int puerto; // El puerto predeterminado de FTP es 21

    // Constructor
    public ConfiguracionFTP(String servidorFTP, String nombreUsuario, String contrasena, int puerto) {
        this.servidorFTP = "192.168.100.117";
        this.nombreUsuario = "ftpuser";
        this.contrasena = "Skillfull2025$";
        this.puerto = 21;
    }

    // Métodos getter y setter
    public String getServidorFTP() {
        return servidorFTP;
    }

    public void setServidorFTP(String servidorFTP) {
        this.servidorFTP = servidorFTP;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    // Método para obtener todos los detalles de la conexión
    public String obtenerDetallesConexion() {
        return "Servidor: " + servidorFTP + ", Usuario: " + nombreUsuario + ", Puerto: " + puerto;
    }
}
