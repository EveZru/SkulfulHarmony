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
import com.example.skulfulharmony.javaobjects.clasifications.Dificultad;
import com.example.skulfulharmony.javaobjects.clasifications.Genero;
import com.example.skulfulharmony.javaobjects.clasifications.Instrumento;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.server.SubirArchivos.SubirArchivo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;  // Para BottomNavigationView
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;


public class CrearCurso extends AppCompatActivity {
    private EditText etNombreNuevoCurso;
    private Spinner spInstrumento, spNivel, spGenero;
    private Button btnSubirCurso;
    private String NombreNuevoCurso;
    private DbUser dbUser = new DbUser(this);
    private ImageView imageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri im = Uri.EMPTY;
    private static final String ACCESS_TOKEN = "tsl.u.AFot3oRZvqPIpyuc0dF55DFNpAJPV4PvWKX1qXx0wo7f-NOkvykzr9bDiUVHPKz08iZDoRAXqV01dsKBIG-EKG-Rt-amCBsBgTaZOSHFWRov0KKUyPFJPLc32__6EhCUymW7eeKu0dpFqyT3uLxMr-up8wEpIEw1Tb3n3mLqYGGh4HdRtHiR-BXLGWnXB_iDAIscdqxQpixX1U1HDpcHJmNgF1zdoXo4841CUj5ttJZPsnG4OpVY0tjBuvf-7o25YeJLHh3v1P4XmlTJm__mEAb5HNZgyrfz-z6is6dvD-AFrnn1dVRVicrVJVhZ_JPp6OOt6PYO2eoxvOhlkitdzxtaR0I1bVFXkEUASGc7Mw24LEGxsF6FLy94W5qfmDI2n-efSZY-7YtvYzQAh7Ng3DEFFulvwMUtKtswKtcKeLsdqFRPP4Cfyj06wVmbWFawMkl6cSfjIemMjRjUWIEjJbtl6OkL74Y5myx0RIfK-CfsDb98neoIecV_kX9xtJd3mS3DTM_FOWgugjGLjBRp_FXYiS1s8S16VHP4h0_OHaOC3jmjNyE52s0FOXvTqxOv1_9IW2ZXZH8JodpAg4uO5Mx1tnkHfQX4JNr5ymmLmF1U-Vdb5_WOrAez47kHhSeU_N7rcWz1XQL1UtfVRL1ypWJPVsCRZ9kwMGpFoHhVPS7kyg4Ovz8k4-4uCDIElGmj8XHsq7FFXogZMH2owb7QynJlg6IHpqbtLrNJtvwXfhBf5veuMeWILLP9GFVl-MKLvoZHY0fCpvMhtsDm01HtfNA6WYTajkQIN2VkrKVn6qJkaJ_vdxyBxyaa79g3SSKcfizci6i-_Yu4rFC3p1Q89Q1y6Z6LpgL_nTvhcichSKUg5G3gy6kXICuk6jEwCJWbh1G13t8V5IyrvRLQYy5YEpp4pJ-_0AkBAfo4DP9bW7phkmUwwOh3pc3Rf4w8TIpDl6f88wutQiRmwX3RieU0vEBQK5v6-lX_sC8v7g2mBzv1zMgKFoXsBWaA5XjM0A4u-ZV4jlmQBWWny9rfPdEltR2hPhQPC5NSCl8kk0PaUleiGaEei25sg8hTYEgzGp7tDMduqROqlLYAP2h5r3FptdGGqgMXFhZkiep0ZIeg2_2EApiVcZjFQEAJ7fd19L9FhqKP08X9LSNs51HJbF76MVvYmcI1If4JJ-VEnpzXODYoGrj8CFs_Hk5mrUo9dPt4ZaHFwj73WIboL6SkrkRAbRiEKJi4qtdlF1J3Z8nnMMAPa0WTnHpkvrVALpT97HlfM1Lwg_UIioe5ZWjVPUDmlS5ieMIeVOL_oGBULk7VdhO6y2TeDHHEGZetwSQ9TaMelnlAkfM0lZJl1nF85rbEC7e7iP7DzrJeqk2euSdHR9e7HjU94CXleq0gyZt_4Oe_ExFnNimW3K0lVqUaRRZ_dHliSr7nwXipgDh_qeVWOzfS4Q";  // Sustituir con el access token de Dropbox


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


        String[] instrumentos = new String[]{"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería",
                "Piano", "Ukelele", "Violin", "Canto","Otro"};
        String[] niveles = new String[]{"Principiante", "Intermedio","Avanzado"};
        String[] generos = new String[]{"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues",
                "Reggaeton", "Reggae", "Clasica", "Coutry", "Metal", "Folk", "Independiente"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, instrumentos);
        spInstrumento.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, generos);
        spGenero.setAdapter(adapter2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, niveles);
         spNivel.setAdapter(adapter3);


        //Tomar imagen de galeria
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        im = imageUri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        imageView.setOnClickListener(v -> openGallery());

        //  subir curso
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
                // Cambiar la actividad para crear el curso
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
        Instrumento instrumento = new Instrumento(spInstrumento.getSelectedItem().toString());
        Genero genero = new Genero(spGenero.getSelectedItem().toString());
        Dificultad dificultad = new Dificultad(spNivel.getSelectedItem().toString());
        Calendar c = Calendar.getInstance();
        Timestamp fechaCreacion = new Timestamp(c.getTime());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference cursosRef = db.collection("cursos").document();

        if (im == null || im == Uri.EMPTY) {
            Toast.makeText(this, "Selecciona una imagen para el curso", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la ruta real del archivo seleccionado
        String imagePath = getRealPathFromURI(im);
        if (imagePath == null) {
            Toast.makeText(this, "Error obteniendo la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar el nombre de la imagen: correo_tituloCurso_nombreimagen.jpg
        String nombreImagen = correo + "_" + titulo.replaceAll("\\s+", "_") + ".jpg";
        File imagenFile = new File(imagePath);

        SubirArchivo subirArchivo = new SubirArchivo();
        subirArchivo.subirArchivo(imagenFile, ACCESS_TOKEN,
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String urlImagen) {
                        // Una vez subida la imagen, guardar los datos del curso en Firestore
                        Curso curso = new Curso(titulo, correo, instrumento, genero, dificultad, urlImagen, fechaCreacion);
                        cursosRef.set(curso).addOnSuccessListener(aVoid -> {
                            Toast.makeText(CrearCurso.this, "Curso creado exitosamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(CrearCurso.this, "Error al subir el curso", Toast.LENGTH_SHORT).show());
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CrearCurso.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    /**
     * Convierte una Uri en una ruta de archivo real
     */
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