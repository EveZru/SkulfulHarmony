package com.example.skulfulharmony.server.SubirArchivos;

import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import com.example.skulfulharmony.server.zip.DescomprimirZip;
import com.example.skulfulharmony.server.config.ConfiguracionFTP;

public class RecibirArchivo {

    // Método para recibir un archivo desde el servidor FTP y descomprimirlo
    public void recibirArchivo(String archivoRemoto, String rutaDestino, ConfiguracionFTP config) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(config.getServidorFTP(), config.getPuerto());
        ftpClient.login(config.getNombreUsuario(), config.getContrasena());

        // Lógica para determinar la carpeta de destino en el servidor
        String carpetaDestino = obtenerCarpetaPorTipo(archivoRemoto);

        // Cambiar al directorio remoto donde está el archivo
        ftpClient.changeWorkingDirectory(carpetaDestino);

        // Descargar el archivo comprimido
        try (FileOutputStream fos = new FileOutputStream(new File(rutaDestino + ".zip"))) {
            ftpClient.retrieveFile(archivoRemoto, fos);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error al recibir el archivo comprimido");
        }

        // Descomprimir el archivo recibido
        File zipFile = new File(rutaDestino + ".zip");
        File destDir = new File(rutaDestino);
        DescomprimirZip decompressor = new DescomprimirZip();
        decompressor.decompressFile(zipFile, destDir);  // Llamada a la función de descompresión

        ftpClient.logout();
        ftpClient.disconnect();
        System.out.println("Archivo recibido y descomprimido exitosamente desde la carpeta: " + carpetaDestino);
    }

    // Método para determinar la carpeta en función del tipo de archivo
    private String obtenerCarpetaPorTipo(String archivoRemoto) {
        archivoRemoto = archivoRemoto.toLowerCase();

        if (archivoRemoto.endsWith(".jpg") || archivoRemoto.endsWith(".png")) {
            return "ftp://192.168.100.117/fotos/";  // Carpeta para fotos
        } else if (archivoRemoto.endsWith(".mp4")) {
            return "ftp://192.168.100.117/videos/";  // Carpeta para videos
        } else if (archivoRemoto.endsWith(".mp3")) {
            return "ftp://192.168.100.117/audios/";  // Carpeta para audios
        } else {
            return "ftp://192.168.100.117/otros/";  // Carpeta para otros tipos de archivos
        }
    }
}
