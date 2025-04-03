package com.example.skulfulharmony.server.SubirArchivos;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.DbxException;
import com.example.skulfulharmony.server.zip.ComprimirZip;
import com.example.skulfulharmony.server.config.DropboxConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SubirArchivo {

    // Método para subir un archivo a Dropbox con callbacks
    public void subirArchivo(File archivo, String accessToken,
                             OnSuccessListener<String> onSuccess,
                             OnFailureListener onFailure) {

        new Thread(() -> {
            // Crear la instancia de DropboxConfig con el token de acceso
            DropboxConfig dropboxConfig = new DropboxConfig(accessToken);
            DbxClientV2 client = dropboxConfig.getClient();

            // Comprimir el archivo antes de subirlo
            File archivoComprimido = new File(archivo.getAbsolutePath() + ".zip");
            ComprimirZip compressor = new ComprimirZip();
            try {
                compressor.compressFile(archivo, archivoComprimido);
            } catch (IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> onFailure.onFailure(e));
                return;
            }

            // Determinar la carpeta de destino en Dropbox
            String carpetaDestino = obtenerCarpetaPorTipo(archivoComprimido);

            try (FileInputStream fis = new FileInputStream(archivoComprimido)) {
                // Subir archivo comprimido a Dropbox
                FileMetadata metadata = client.files()
                        .uploadBuilder(carpetaDestino + "/" + archivoComprimido.getName())
                        .uploadAndFinish(fis);

                // Generar URL pública (simulación, debes cambiarlo por la API correcta)
                String urlImagen = "https://www.dropbox.com/home" + carpetaDestino + "/" + archivoComprimido.getName();

                // Devolver la URL en el hilo principal
                new Handler(Looper.getMainLooper()).post(() -> onSuccess.onSuccess(urlImagen));

            } catch (UploadErrorException | DbxException | IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> onFailure.onFailure(e));
            }
        }).start();
    }

    // Método para determinar la carpeta en función del tipo de archivo
    private String obtenerCarpetaPorTipo(File archivo) {
        String nombreArchivo = archivo.getName().toLowerCase();

        if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".png")) {
            return "/home/Aplicaciones/skillfullharmony/Fotos";
        } else if (nombreArchivo.endsWith(".mp4")) {
            return "/home/Aplicaciones/skillfullharmony/Videos";
        } else if (nombreArchivo.endsWith(".mp3")) {
            return "/home/Aplicaciones/skillfullharmony/Audios";
        } else {
            return "/home/Aplicaciones/skillfullharmony/Archivos";
        }
    }
}