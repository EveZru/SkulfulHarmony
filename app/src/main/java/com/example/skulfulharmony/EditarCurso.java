package com.example.skulfulharmony;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.example.skulfulharmony.server.config.DropboxConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditarCurso extends AppCompatActivity {
    private TextView  tv_nombrecurso,tv_descripcioncurso;
    private Button btn_cambiar, btn_cancelar ;
    private EditText et_nueva_descripcion;
    private FirebaseFirestore firestore;

    private int idCurso;
    private ImageView iv_fotocurso;
    private String nombreCursoActual;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final String ACCESS_TOKEN = DropboxConfig.ACCESS_TOKEN;
    private Uri im = Uri.EMPTY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_curso);

        iv_fotocurso=findViewById(R.id.iv_fotocurso);
        btn_cambiar = findViewById(R.id.btn_updatecurso);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        tv_nombrecurso = findViewById(R.id.tv_nombrecurso);
        tv_descripcioncurso = findViewById(R.id.tv_descripantreior);
        et_nueva_descripcion = findViewById(R.id.et_descripnueva);


        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = getIntent();
            idCurso = intent.getIntExtra("idCurso", -1);
            if(idCurso != -1){
                cargarDatosCurso(idCurso);
                Toast.makeText(this, "idCurso: " + idCurso, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al obtener la información del curso", Toast.LENGTH_SHORT).show();
            }
        });

        btn_cancelar.setOnClickListener(v -> {

            finish();
        });

        btn_cambiar.setOnClickListener(v -> {
            String nuevaDescripcion = et_nueva_descripcion.getText().toString();
            if(im!=Uri.EMPTY){
                subirNuevaImagen();
            }else{
                Toast.makeText(EditarCurso.this,"No se realizaron cambios en la imagen del curso ",Toast.LENGTH_SHORT).show();
            }
            firestore.collection("cursos").whereEqualTo("idCurso", idCurso)
            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentReference docRef=queryDocumentSnapshots.getDocuments().get(0).getReference();
                   // proseso para editar la descrpcion
                    if(nuevaDescripcion!=null  && !nuevaDescripcion.isBlank()) {
                        Toast.makeText(EditarCurso.this,"Cambiando descripcion",Toast.LENGTH_SHORT).show();
                        docRef.update("descripcion", nuevaDescripcion, "fechaActualizacion", Timestamp.now());
                    }else{
                        Toast.makeText(EditarCurso.this,"No se realizaron cambios en la descripcion del curso ",Toast.LENGTH_SHORT).show();
                    }


                }

            });
            finish();

        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                im = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), im);
                    iv_fotocurso.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        iv_fotocurso.setOnClickListener(v -> {
            openGalery();
        });
    }

    private void cargarDatosCurso(int id) {
        firestore.collection("cursos").whereEqualTo("idCurso", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        String nombre = queryDocumentSnapshots.getDocuments().get(0).getString("titulo");
                        String descripcion = queryDocumentSnapshots.getDocuments().get(0).getString("descripcion");

                        if (nombre != null) {
                            nombreCursoActual = nombre;
                            tv_nombrecurso.setText("_Editarás los datos de: " + nombre +" _");
                        }
                        if (descripcion != null) {
                            tv_descripcioncurso.setText("Descripción anterior: " + descripcion);
                        }
                    } else {
                        Toast.makeText(EditarCurso.this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarCurso.this, "Error al cargar el curso: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EditarCurso", "Error al cargar el curso", e);
                    finish();
                });
    }
    private void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    private File copiarUriAArchivoTemporal(Uri uri) {
        try {
            File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
            try (InputStream in = getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            }
            return tempFile;
        } catch (IOException e) {
            return null;
        }
    }
    private String convertirLinkADirecto(String url) {
        return url.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "");
    }
    private void subirNuevaImagen() {
        if (im == Uri.EMPTY) {
            Toast.makeText(this, "No se seleccionó nueva imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        File archivo = copiarUriAArchivoTemporal(im);
        if (archivo == null) {
            Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                DbxClientV2 client = new DropboxConfig(ACCESS_TOKEN).getClient();
                FileInputStream fis = new FileInputStream(archivo);

                // Subir con nombre único basado en timestamp
                String nombreArchivo = "curso_" + idCurso + "_" + System.currentTimeMillis() + ".jpg";
                FileMetadata metadata = client.files()
                        .uploadBuilder("/Aplicaciones/SkillfulHarmonyCursos/" + nombreArchivo)
                        .uploadAndFinish(fis);

                SharedLinkMetadata linkMetadata = client.sharing()
                        .createSharedLinkWithSettings(metadata.getPathLower());

                String urlImagen = convertirLinkADirecto(linkMetadata.getUrl());

                // Actualizar en Firestore
                handler.post(() -> actualizarImagenEnFirestore(urlImagen));
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void actualizarImagenEnFirestore(String nuevaUrlImagen) {
        firestore.collection("cursos").whereEqualTo("idCurso", idCurso)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        docRef.update("imagen", nuevaUrlImagen, "fechaActualizacion", Timestamp.now())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditarCurso.this, "Imagen actualizada correctamente", Toast.LENGTH_SHORT).show();
                                    // Opcional: Actualizar la imagen en la interfaz
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), im);
                                        iv_fotocurso.setImageBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EditarCurso.this, "Error al actualizar imagen", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }



}