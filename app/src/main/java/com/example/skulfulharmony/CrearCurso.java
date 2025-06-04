package com.example.skulfulharmony;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.clustering.DataPoint;
import com.example.skulfulharmony.javaobjects.clustering.KMeans;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.server.config.DropboxConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// Importaciones omitidas por brevedad (iguales a las que ya tienes)
public class CrearCurso extends AppCompatActivity {

    private EditText etNombreNuevoCurso, etDescripcionNuevoCurso;
    private Spinner spInstrumento, spNivel, spGenero;
    private Button btnSubirCurso;
    private ImageView imageView;
    private Uri im = Uri.EMPTY;

    private static final String ACCESS_TOKEN = DropboxConfig.ACCESS_TOKEN;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private final Map<String, Integer> mapaInstrumento = new HashMap<>() {{
        put("Guitarra", 0); put("Bajo", 1); put("Flauta", 2); put("Trompeta", 3); put("Batería", 4);
        put("Piano", 5); put("Ukelele", 6); put("Violin", 7); put("Canto", 8); put("Otro", 9);
    }};
    private final Map<String, Integer> mapaDificultad = new HashMap<>() {{
        put("Principiante", 0); put("Intermedio", 1); put("Avanzado", 2);
    }};
    private final Map<String, Integer> mapaGenero = new HashMap<>() {{
        put("Pop", 0); put("Rock", 1); put("Hiphop/Rap", 2); put("Electronica", 3); put("Jazz", 4);
        put("Blues", 5); put("Reggaeton", 6); put("Reggae", 7); put("Clasica", 8);
        put("Country", 9); put("Metal", 10); put("Folk", 11); put("Independiente", 12);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearcurso);

        etNombreNuevoCurso = findViewById(R.id.et_nombre_nuevo_curso);
        etDescripcionNuevoCurso = findViewById(R.id.et_descripcion_nuevo_curso);
        spInstrumento = findViewById(R.id.sp_Instrumento);
        spNivel = findViewById(R.id.sp_Nivel);
        spGenero = findViewById(R.id.sp_Genero);
        btnSubirCurso = findViewById(R.id.btn_subir_curso);
        imageView = findViewById(R.id.iv_fotocurso);

        spInstrumento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(mapaInstrumento.keySet())));
        spNivel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(mapaDificultad.keySet())));
        spGenero.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(mapaGenero.keySet())));

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
        btnSubirCurso.setOnClickListener(v -> subirCurso());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void subirCurso() {
        String nombre = etNombreNuevoCurso.getText().toString().trim();
        String descripcion = etDescripcionNuevoCurso.getText().toString().trim();
        String instrumento = spInstrumento.getSelectedItem().toString();
        String dificultad = spNivel.getSelectedItem().toString();
        String genero = spGenero.getSelectedItem().toString();

        if (nombre.isEmpty() || descripcion.isEmpty() || im == Uri.EMPTY) {
            Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        int dificultadNum = mapaDificultad.getOrDefault(dificultad, -1);
        int generoNum = mapaGenero.getOrDefault(genero, -1);
        int instrumentoNum = mapaInstrumento.getOrDefault(instrumento, -1);

        if (dificultadNum == -1 || generoNum == -1 || instrumentoNum == -1) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
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

                FileMetadata metadata = client.files()
                        .uploadBuilder("/Aplicaciones/SkillfulHarmonyCursos/" + archivo.getName())
                        .uploadAndFinish(fis);

                SharedLinkMetadata linkMetadata = client.sharing()
                        .createSharedLinkWithSettings(metadata.getPathLower());

                String urlImagen = convertirLinkADirecto(linkMetadata.getUrl());

                handler.post(() -> procesarKMeansYGuardarCurso(
                        nombre, descripcion, instrumento, dificultad, genero,
                        instrumentoNum, dificultadNum, generoNum, urlImagen
                ));
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void procesarKMeansYGuardarCurso(String nombre, String descripcion,
                                             String instrumento, String dificultad, String genero,
                                             int instrumentoNum, int dificultadNum, int generoNum,
                                             String urlImagen) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cursos")
                .whereEqualTo("instrumento", instrumento)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DataPoint> puntos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Integer d = mapaDificultad.get(doc.getString("dificultad"));
                        Integer g = mapaGenero.get(doc.getString("genero"));
                        if (d != null && g != null) {
                            puntos.add(new DataPoint(new double[]{d, g}));
                        }
                    }

                    DataPoint nuevo = new DataPoint(new double[]{dificultadNum, generoNum});
                    puntos.add(nuevo);
                    int k = Math.min(4, puntos.size());
                    KMeans kmeans = new KMeans(k, 100);
                    kmeans.fit(puntos);
                    int cluster = kmeans.predict(nuevo);
                    db.collection("cursos")
                            .orderBy("idCurso", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(maxIdSnap -> {
                                int nuevoId = 1;
                                if (!maxIdSnap.isEmpty()) {
                                    Long last = maxIdSnap.getDocuments().get(0).getLong("idCurso");
                                    if (last != null) nuevoId = last.intValue() + 1;
                                }

                                // Crear los mapas que se subirán como Map<String, Integer>
                                Map<String, Integer> mapInstrumento = new HashMap<>();
                                mapInstrumento.put(instrumento, instrumentoNum);

                                Map<String, Integer> mapGenero = new HashMap<>();
                                mapGenero.put(genero, generoNum);

                                Map<String, Integer> mapDificultad = new HashMap<>();
                                mapDificultad.put(dificultad, dificultadNum);

                                // Crear objeto Curso y asignar los valores
                                Curso curso = new Curso();
                                curso.setIdCurso(nuevoId);
                                curso.setCreador(nombre);
                                curso.setDescripcion(descripcion);
                                curso.setInstrumento(mapInstrumento);
                                curso.setGenero(mapGenero);
                                curso.setDificultad(mapDificultad);
                                curso.setImagen(urlImagen);
                                curso.setCluster(cluster);
                                curso.setFechaCreacionf(Timestamp.now());
                                curso.setVisitas(0);
                                curso.setCantidadDescargas(0);
                                curso.setPopularidad(0.0);

                                db.collection("cursos")
                                        .add(curso)
                                        .addOnSuccessListener(docRef -> {
                                            Toast.makeText(this, "Curso creado correctamente", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error al guardar curso", Toast.LENGTH_SHORT).show();
                                        });
                            });

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener cursos", Toast.LENGTH_SHORT).show();
                });
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
}
