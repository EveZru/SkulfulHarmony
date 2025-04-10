package com.example.skulfulharmony;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.server.SubirArchivos.SubirArchivo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrearCurso extends AppCompatActivity {
    private EditText etNombreNuevoCurso;
    private Spinner spInstrumento, spNivel, spGenero;
    private Button btnSubirCurso;
    private String NombreNuevoCurso;
    private DbUser dbUser = new DbUser(this);
    private ImageView imageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri im = Uri.EMPTY;

    private static final String ACCESS_TOKEN = "tsl.u.AFot3oRZ..."; // Recorta por seguridad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crearcurso);

        imageView = findViewById(R.id.iv_fotocurso);
        etNombreNuevoCurso = findViewById(R.id.et_nombre_nuevo_curso);
        spInstrumento = findViewById(R.id.sp_Instrumento);
        spNivel = findViewById(R.id.sp_Nivel);
        spGenero = findViewById(R.id.sp_Genero);
        btnSubirCurso = findViewById(R.id.btn_subir_curso);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Spinners
        String[] instrumentos = new String[]{"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería", "Piano", "Ukelele", "Violin", "Canto", "Otro"};
        String[] niveles = new String[]{"Principiante", "Intermedio", "Avanzado"};
        String[] generos = new String[]{"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues", "Reggaeton", "Reggae", "Clasica", "Coutry", "Metal", "Folk", "Independiente"};

        spInstrumento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, instrumentos));
        spGenero.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, generos));
        spNivel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, niveles));

        // Galería
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

        btnSubirCurso.setOnClickListener(view -> {
            NombreNuevoCurso = etNombreNuevoCurso.getText().toString().trim();

            if (NombreNuevoCurso.isEmpty()) {
                Toast.makeText(CrearCurso.this, "Ingresa un nombre para el curso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CrearCurso.this, "Creando clase...", Toast.LENGTH_SHORT).show();
                subir();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.it_homme) {
                startActivity(new Intent(CrearCurso.this, Home.class));
                return true;
            } else if (itemId == R.id.it_new) {
                return true;
            } else if (itemId == R.id.it_seguidos) {
                startActivity(new Intent(CrearCurso.this, Biblioteca.class));
                return true;
            } else if (itemId == R.id.it_perfil) {
                startActivity(new Intent(CrearCurso.this, Perfil.class));
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.it_new);
    }

    private void subir() {
        String correo = dbUser.getCorreoUser();
        String titulo = etNombreNuevoCurso.getText().toString();

        if (im == null || im == Uri.EMPTY) {
            Toast.makeText(this, "Selecciona una imagen para el curso", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = getRealPathFromURI(im);
        if (imagePath == null) {
            Toast.makeText(this, "Error obteniendo la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        File imagenFile = new File(imagePath);
        String nombreImagen = correo + "_" + titulo.replaceAll("\\s+", "_") + ".jpg";

        Map<String, String> instrumento = new HashMap<>();
        instrumento.put(spInstrumento.getSelectedItem().toString(), null);
        Map<String, String> genero = new HashMap<>();
        genero.put(spGenero.getSelectedItem().toString(), null);
        Map<String, String> dificultad = new HashMap<>();
        dificultad.put(spNivel.getSelectedItem().toString(), null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener el idCurso autoincremental
        db.collection("cursos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int maxId = 0;
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Long id = doc.getLong("idCurso");
                if (id != null && id > maxId) {
                    maxId = id.intValue();
                }
            }

            int nuevoId = maxId + 1;
            Curso curso = new Curso(titulo, correo, instrumento, genero, dificultad, nombreImagen, new Timestamp(new Date()));
            curso.setIdCurso(nuevoId);
            curso.setCreador(correo);
            curso.setFechaCreacionf(new Timestamp(new Date()));

            db.collection("cursos").add(curso).addOnSuccessListener(documentReference -> {
                Toast.makeText(CrearCurso.this, "Curso creado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(CrearCurso.this, "Error al subir el curso", Toast.LENGTH_SHORT).show();
            });

            // Subir imagen
            SubirArchivo subirArchivo = new SubirArchivo();
            subirArchivo.subirArchivo(imagenFile, ACCESS_TOKEN,
                    urlImagen -> {}, // Ya no es necesario hacer nada aquí
                    e -> Toast.makeText(CrearCurso.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(e -> {
            Toast.makeText(CrearCurso.this, "Error obteniendo ID de curso", Toast.LENGTH_SHORT).show();
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                result = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return result;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
