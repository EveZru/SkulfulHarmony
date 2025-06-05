package com.example.skulfulharmony.modooffline;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DropboxDownloader {

    public interface Callback {
        void onSuccess(File archivoLocal);
        void onError(Exception e);
    }

    public static void descargarArchivo(Context context, String url, String nombreArchivo, Callback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Error en la descarga: " + response);
                }

                InputStream inputStream = response.body().byteStream();
                File destino = new File(context.getFilesDir(), nombreArchivo);

                try (FileOutputStream fos = new FileOutputStream(destino)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                callback.onSuccess(destino);

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }
}