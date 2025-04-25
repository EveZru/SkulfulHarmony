package com.example.skulfulharmony;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.server.config.DropboxConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrearCurso extends AppCompatActivity {

    private EditText etNombreNuevoCurso, etDescripcionNuevoCurso;
    private Spinner spInstrumento, spNivel, spGenero;
    private Button btnSubirCurso;
    private ImageView imageView;
    private Uri im = Uri.EMPTY;
    private DbUser dbUser;
    private static final String ACCESS_TOKEN = DropboxConfig.ACCESS_TOKEN;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crearcurso);

        dbUser = new DbUser(this);
        imageView = findViewById(R.id.iv_fotocurso);
        etNombreNuevoCurso = findViewById(R.id.et_nombre_nuevo_curso);
        etDescripcionNuevoCurso = findViewById(R.id.et_descripcion_nuevo_curso);
        spInstrumento = findViewById(R.id.sp_Instrumento);
        spNivel = findViewById(R.id.sp_Nivel);
        spGenero = findViewById(R.id.sp_Genero);
        btnSubirCurso = findViewById(R.id.btn_subir_curso);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.it_new);

        String[] instrumentos = {"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería", "Piano", "Ukelele", "Violin", "Canto", "Otro"};
        String[] niveles = {"Principiante", "Intermedio", "Avanzado"};
        String[] generos = {"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues", "Reggaeton", "Reggae", "Clasica", "Country", "Metal", "Folk", "Independiente"};

        spInstrumento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, instrumentos));
        spNivel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, niveles));
        spGenero.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, generos));

        etDescripcionNuevoCurso.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Esto le dice al ScrollView (o layout padre) que no intercepte el toque
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                im = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), im);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        imageView.setOnClickListener(v -> openGallery());

        btnSubirCurso.setOnClickListener(v -> subir());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private File copiarUriAArchivoTemporal(Uri uri) {
        try {
            File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertirLinkADirecto(String dropboxUrl) {
        return dropboxUrl.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "");
    }

    private void subir() {
        if (im == null || im == Uri.EMPTY) {
            Toast.makeText(this, "Selecciona una imagen para el curso", Toast.LENGTH_SHORT).show();
            return;
        }

        File archivo = copiarUriAArchivoTemporal(im);
        if (archivo == null) {
            Toast.makeText(this, "Error creando archivo temporal", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                DropboxConfig dropboxConfig = new DropboxConfig(ACCESS_TOKEN);
                DbxClientV2 client = dropboxConfig.getClient();

                try (FileInputStream fis = new FileInputStream(archivo)) {
                    FileMetadata metadata = client.files()
                            .uploadBuilder("/Aplicaciones/SkillfulHarmonyCursos/" + archivo.getName())
                            .uploadAndFinish(fis);

                    SharedLinkMetadata linkMetadata = client.sharing()
                            .createSharedLinkWithSettings(metadata.getPathLower());

                    String urlImagen = convertirLinkADirecto(linkMetadata.getUrl());

                    handler.post(() -> guardarCursoEnFirestore(urlImagen));
                }
            } catch (Exception e) {
                handler.post(() -> {
                    Toast.makeText(CrearCurso.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
            }
        });
    }

    private void guardarCursoEnFirestore(String urlImagen) {
        String correo = dbUser.getCorreoUser();
        String titulo = etNombreNuevoCurso.getText().toString();
        String descripcion = etDescripcionNuevoCurso.getText().toString();

        Map<String, String> instrumento = new HashMap<>();
        instrumento.put(spInstrumento.getSelectedItem().toString(), null);

        Map<String, String> genero = new HashMap<>();
        genero.put(spGenero.getSelectedItem().toString(), null);

        Map<String, String> dificultad = new HashMap<>();
        dificultad.put(spNivel.getSelectedItem().toString(), null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cursos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int maxId = 0;
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Long id = doc.getLong("idCurso");
                if (id != null && id > maxId) {
                    maxId = id.intValue();
                }
            }

            int nuevoId = maxId + 1;
            Curso curso = new Curso(titulo, correo, instrumento, genero, dificultad, urlImagen, new Timestamp(new Date()));
            curso.setIdCurso(nuevoId);
            curso.setCreador(correo);
            curso.setDescripcion(descripcion);
            curso.setFechaCreacionf(new Timestamp(new Date()));

            db.collection("cursos").add(curso).addOnSuccessListener(documentReference -> {
                Toast.makeText(CrearCurso.this, "Curso creado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(CrearCurso.this, "Error al subir el curso", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
