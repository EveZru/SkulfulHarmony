package com.example.skulfulharmony.server.SubirArchivos;

import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import com.example.skulfulharmony.server.zip.ComprimirZip;
import com.example.skulfulharmony.server.config.ConfiguracionFTP;

public class SubirArchivo {

    // Método para subir un archivo al servidor FTP usando ConfiguraciónFTP
    public void subirArchivo(File archivo, ConfiguracionFTP config) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(config.getServidorFTP(), config.getPuerto());
        ftpClient.login(config.getNombreUsuario(), config.getContrasena());

        // Comprimir el archivo antes de subirlo
        File archivoComprimido = new File(archivo.getAbsolutePath() + ".zip");
        ComprimirZip compressor = new ComprimirZip();
        compressor.compressFile(archivo, archivoComprimido); // Llamada a la función de compresión

        // Lógica para determinar la carpeta de destino en el servidor
        String carpetaDestino = obtenerCarpetaPorTipo(archivoComprimido);

        // Cambiar al directorio remoto donde quieres subir el archivo comprimido
        ftpClient.changeWorkingDirectory(carpetaDestino);

        // Subir el archivo comprimido
        try (FileInputStream fis = new FileInputStream(archivoComprimido)) {
            ftpClient.storeFile(archivoComprimido.getName(), fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error al subir el archivo comprimido");
        }

        ftpClient.logout();
        ftpClient.disconnect();
        System.out.println("Archivo subido exitosamente a la carpeta: " + carpetaDestino);
    }

    // Método para determinar la carpeta en función del tipo de archivo
    private String obtenerCarpetaPorTipo(File archivo) {
        String nombreArchivo = archivo.getName().toLowerCase();

        if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".png")) {
            return "ftp://192.168.100.117/fotos/";  // Carpeta para fotos
        } else if (nombreArchivo.endsWith(".mp4")) {
            return "ftp://192.168.100.117/videos/";  // Carpeta para videos
        } else if (nombreArchivo.endsWith(".mp3")) {
            return "ftp://192.168.100.117/audios/";  // Carpeta para audios
        } else {
            return "ftp://192.168.100.117/otros/";  // Carpeta para otros tipos de archivos
        }
    }
}