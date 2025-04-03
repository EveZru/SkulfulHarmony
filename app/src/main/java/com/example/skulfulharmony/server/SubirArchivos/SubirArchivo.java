package com.example.skulfulharmony.server.SubirArchivos;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.DbxException;
import com.example.skulfulharmony.server.zip.ComprimirZip;
import com.example.skulfulharmony.server.config.DropboxConfig;  // Importa la clase DropboxConfig
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SubirArchivo {

    // Método para subir un archivo a Dropbox
    public void subirArchivo(File archivo, String accessToken) {
        // Crear la instancia de DropboxConfig con el token de acceso
        DropboxConfig dropboxConfig = new DropboxConfig(accessToken);
        DbxClientV2 client = dropboxConfig.getClient(); // Obtener el cliente Dropbox

        // Comprimir el archivo antes de subirlo
        File archivoComprimido = new File(archivo.getAbsolutePath() + ".zip");
        ComprimirZip compressor = new ComprimirZip();
        try {
            compressor.compressFile(archivo, archivoComprimido); // Llamada a la función de compresión
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al comprimir el archivo: " + e.getMessage());
            return; // Salir del método si no se pudo comprimir el archivo
        }

        // Lógica para determinar la carpeta de destino en Dropbox
        String carpetaDestino = obtenerCarpetaPorTipo(archivoComprimido);

        try (FileInputStream fis = new FileInputStream(archivoComprimido)) {
            // Subir archivo comprimido a Dropbox
            FileMetadata metadata = client.files().uploadBuilder(carpetaDestino + "/" + archivoComprimido.getName())
                    .uploadAndFinish(fis);
            System.out.println("Archivo subido exitosamente: " + metadata.getName());
        } catch (UploadErrorException e) {
            e.printStackTrace();
            System.out.println("Error al subir el archivo comprimido a Dropbox: " + e.getMessage());
        } catch (DbxException | IOException e) {  // Capturar la excepción DbxException y IOException
            e.printStackTrace();
            System.out.println("Error al interactuar con Dropbox: " + e.getMessage());
        }
    }

    // Método para determinar la carpeta en función del tipo de archivo
    private String obtenerCarpetaPorTipo(File archivo) {
        String nombreArchivo = archivo.getName().toLowerCase();

        if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".png")) {
            return "/home/Aplicaciones/skillfullharmony/Fotos";  // Carpeta para fotos
        } else if (nombreArchivo.endsWith(".mp4")) {
            return "/home/Aplicaciones/skillfullharmony/Videos";  // Carpeta para videos
        } else if (nombreArchivo.endsWith(".mp3")) {
            return "/home/Aplicaciones/skillfullharmony/Audios";  // Carpeta para audios
        } else {
            return "/home/Aplicaciones/skillfullharmony/Archivos";  // Carpeta para otros tipos de archivos
        }
    }
}