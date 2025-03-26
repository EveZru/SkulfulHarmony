package com.example.skulfulharmony.server.zip;

import java.io.*;
import java.util.zip.*;

public class DescomprimirZip {

    // Método para descomprimir un archivo ZIP
    public void decompressFile(File zipFile, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());

                // Crear los directorios si no existen
                new File(newFile.getParent()).mkdirs();

                // Crear un archivo nuevo para cada entrada del ZIP
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) >= 0) {
                        fos.write(buffer, 0, length);
                    }
                }
                zis.closeEntry();
            }
        }
    }
}