package com.example.skulfulharmony.server.zip;

import java.io.*;
import java.util.zip.*;

public class ComprimirZip {

    //Método para comprimir un archivo en formato ZIP
    public void compressFile(File inputFile, File outputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Crea una entrada ZIP para el archivo
            ZipEntry zipEntry = new ZipEntry(inputFile.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            // Cierra la entrada y el flujo ZIP
            zos.closeEntry();
        }
    }
}
