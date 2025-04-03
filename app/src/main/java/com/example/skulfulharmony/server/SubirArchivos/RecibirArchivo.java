package com.example.skulfulharmony.server.SubirArchivos;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.DownloadBuilder;
import com.dropbox.core.DbxException;
import com.example.skulfulharmony.server.zip.DescomprimirZip;
import com.example.skulfulharmony.server.config.DropboxConfig;  // Importa la clase DropboxConfig
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecibirArchivo {

    // Método para recibir un archivo desde Dropbox y descomprimirlo
    public void recibirArchivo(String archivoRemoto, String rutaDestino, String accessToken) throws IOException {
        // Crear la instancia de DropboxConfig con el token de acceso
        DropboxConfig dropboxConfig = new DropboxConfig(accessToken);
        DbxClientV2 client = dropboxConfig.getClient(); // Obtener el cliente Dropbox

        // Lógica para determinar la carpeta de destino en Dropbox
        String carpetaDestino = obtenerCarpetaPorTipo(archivoRemoto);

        // Descargar el archivo comprimido desde Dropbox
        try (FileOutputStream fos = new FileOutputStream(new File(rutaDestino + ".zip"))) {
            DownloadBuilder downloader = client.files().downloadBuilder(carpetaDestino + "/" + archivoRemoto);
            downloader.download(fos);  // Descargar el archivo
            System.out.println("Archivo recibido exitosamente desde Dropbox");
        } catch (DownloadErrorException e) {
            e.printStackTrace();
            throw new IOException("Error al recibir el archivo desde Dropbox: " + e.getMessage());
        } catch (DbxException | IOException e) {  // Capturar las excepciones DbxException y IOException
            e.printStackTrace();
            throw new IOException("Error al interactuar con Dropbox: " + e.getMessage());
        }

        // Descomprimir el archivo recibido
        File zipFile = new File(rutaDestino + ".zip");
        File destDir = new File(rutaDestino);
        DescomprimirZip decompressor = new DescomprimirZip();
        decompressor.decompressFile(zipFile, destDir);  // Llamada a la función de descompresión
    }

    // Método para determinar la carpeta en función del tipo de archivo
    private String obtenerCarpetaPorTipo(String archivoRemoto) {
        archivoRemoto = archivoRemoto.toLowerCase();

        if (archivoRemoto.endsWith(".jpg") || archivoRemoto.endsWith(".png")) {
            return "/home/Aplicaciones/skillfullharmony/Fotos";  // Carpeta para fotos
        } else if (archivoRemoto.endsWith(".mp4")) {
            return "/home/Aplicaciones/skillfullharmony/Videos";  // Carpeta para videos
        } else if (archivoRemoto.endsWith(".mp3")) {
            return "/home/Aplicaciones/skillfullharmony/Audios";  // Carpeta para audios
        } else {
            return "/home/Aplicaciones/skillfullharmony/Archivos";  // Carpeta para otros tipos de archivos
        }
    }
}