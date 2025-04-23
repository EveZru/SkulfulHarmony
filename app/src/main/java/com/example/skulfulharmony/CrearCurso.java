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

    private static final String ACCESS_TOKEN = "sl.u.AFoT4f6Y43DWZfTU2OLuxwLw7d3-PrwZtOt8ET6rDTxU65Z-JJiNK1c92NuY0qdMN35uInQ6rcmPqyxmg1LpRpT2Or26omR-QzoLgNU3T5HYTjJNM0fVfw-h9ClM11UG54NClNpbxm05ck90ft1G6nAC4gINAZ6iFFGE7lHeSsm74I8SWlPbSZ-18T2Q-3x95IoxA_hJ5V0hppj_8VHJMVzk9trDL6Ebz1MW21QFp4KLhcHTibTZpNUP-614DrTVKuDrPHy6EN85iHcWWCRBwwgHh7xx4W6LhbLqcg8_ukgV94wgTfs6oGu5qv7Wfav24T_4ZuGWC6JpZhhFCvIB4ixWwDYVs_6mKTwk8PnhC_rEBKCFtQxmXFs-xk07KDwd_Sge34roi1xHtbSdT4bFFRRsBGLaVrs-yRtUa0I8RNW9ui5Xz-tqnKRO5nRxeu0b-7MUAnQehQOheCvRg3o84LVg1RmhJcQ35O944OKAx_ceCcHuGMBLP4A2HKhwUVeT6XlAf-fGQsG3746uHndeImyJvueG9Rbmg8iN_DwGdORtOdcT6ouIUGS3FJbH6I_Lbl4P2W58bKTM_xUcOdRv3-BY5LrtYzfsafwWPxFPvfZYB2uYWQTOhAeQrEQLRikmpyBlUqkoocHh_-YlpC29nPE07s0hOUHba981-3Q10OvFsUNyC_GKFdxDH8-i_M4kKQ_S1jso_6CYSrXQChUeb94xVbyseVyug-cCtsadxDO_tbqyF8SeBlhMHyWxXFVVraEV6Rc6JDLW7ps6Zyf6JrZOuJT3yNp7nmEs-4cJjdHe6L149gSYjPb-QTWHCavrBQ16Gn4wcZYBy1aiXAtMpFfPV3XluWLga843zgLD052hC-MpnKr2qfomsg8UaVfu80AfCHH9qBvWGo5Ayqs9ocnhjsu_0JIhwquJb0qTSmfupY8KzPrcVuvknlkfMXrBd5swRNkFt56nHheS63jPLibpdUqJpjZ9xz9BBoqN942gS6tKQkY-N2P2JLPdj-DvMHPOlOuxbwCKyIL0_nhO_dZH_qbxiNrPywHxSChQVH0M36IOMehOgZTcKtdn2-ws8WyB9K_jv93Ptpy0sQZCh4hEWJAMLyqku3-YNfzYZSnNz3WPBLuVNmeDlJr3PqJ5LWIROfK2rCsKKKHmhOV0WxEA5xvSkk-DtTcs46AgRjGpeFRdeS3h-AnXH8e_E05wajEyoNmShX_BQSyo7o95103cwP8n5MCDmehVLsHHbh5BmjCGeueWBXZRnniVGUDtVaScUYYV1aRuYQNH4yjABW5QZpq_DPDkG7LSzn2UOWxdp9uyeQMKBvQjqjpvLVHXWkVMRMn1PaDxZiRJPJUcD1RiLHGTlQR2quunTKjQlxm8YkEKjF5OfcGMFNW7fV1Bv1CANnKsFq9EzeIqpBW8cSaX8OZyPnPL66dtTvoasXCC-Q";

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
