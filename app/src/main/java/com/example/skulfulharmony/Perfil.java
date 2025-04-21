package com.example.skulfulharmony;

import com.example.skulfulharmony.server.zip.ComprimirZip;
import com.example.skulfulharmony.server.config.DropboxConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;

import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.provider.MediaStore;
import android.util.Log;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_No_Cursos, tv_DescripcionUsuario;
    private Button btnEditarPerfil, btnCerrarSesion, btnEliminarCuenta, btnVerTiempoUsuario;
    private ImageView btn_gotoconfiguracion ;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId;
    private static final String ACCESS_TOKEN = "sl.u.AFoxWpe7-InbZDwc9zp-hU3T94D-t0lrPKrxdDNslazWhonuOT2gdVgCdAunPBTK1f9nkWzOame1_YgOl1LISHc89KMUuLho50pd0iRKkllQYBNMp6OTmUHU2jhZ-OfX1FcyH9TDAsV8UpFpeoC6GbNcHOfGPwsnSjodtvATHska58yaLKYzU25ZX3kLKyixUsdnl569jsq7T5YNVkYYSAnYR7oRG8gtmkqARr2yVs2v2WzP_flmRUlff4akQb-LkkAZZjLPllBeX9MBfjIgBZ7G7J0dB4NOGUqr89Ksm6WHedMBY7kGPK_zTZxfJfglD6jNjT9zU7RayoX3MOBIECvN_h2VVdj4RJUTR_xyk0fCZJJKe2Y4fwmA1DobQhvl1cguGED8QaJel-YvhcRK2JkrdoMpX-1c3YGQyTWYf2-2Vr67YZRC1lengjaMPsqY-6biUhoYP5MWQuki9oTV9L326QCEhYKwv4OhArSMnhGhI1OrNle8KA2N9QHyOPt73xyJAB-GAqDCviuzg3WA8k6l6QBLZyTU5T0G4Nj0ohDP1jQd76JCsAdfdkPZZ6pT8_MTVal95wuXqGExx-6g-BI__2x8iq4Jh-Hdk7jz22eP0Gwnxbb-sVqSPQ9J1rXpnfHYRUb9SJxjFuD9B9fOV_tMD0iETuzqnnHy_Lo6gW9N-Nv3LhQoPIw_VIut2pEcYmYSoCqdA5omHbgeQzoEOMbQaHaBAew-aWlMUYvNgDmPZ-mLZ8Qx9deMHyFQRKXPjwJFCq78aavhjAXLMnvzdC58KuSoTRNqlyRyhe5kTa9nYw9ctFVnkIZlmjwhqiWoxhvkJ-4JjRdXgmlzjwCJVN8n-GPpKUfRmOM0W0Lum4DHDJqTPio07ZwA8HIbyG1aexSkF5HD_kOvQ0AMapiBKYaqR1HUxo-IBbQo5ZjMeKzNtWz0xqdej_JLCggl2JTO2dFpSyCWWa7wtB9WpoAFUuqRjZ6Ep58JxvXRulCtfswzzuhQT6RgSsECm8ZNiE1VOmVbV3YtN877DWHi0UuTMX-NKfigVmM2VCFEDuJAJMMhpDp5n79rCswlgbo7CJpc3x0QWfe2AHMdrq-2UlE8pTS10z4bU6zO8FiPWkEjzMHgxKZSMaxwUJ30x6psbsliZaj8izbg9hB-WCfu1NtqDX_0hopwGPAfN8qFyW1nl-Kp9Yau5r6kITuvAm96pNx2WUSJvWhoT8PEg7F7-f1fsEmpRPG_yr9HSugGuiW43L2wPEiFFQ1KDk7ZlGSMN8nqXwW2kEH18fznVp3PXIFrDXLvhabO3FDgaydbJmTNrYSFM3EazilRdQlsrdLRyMz-MmlqArzRtkJhGbUta98MjDqZ8GUYfDf01hWJM6GtNDJdoYYIHVqr2APtmUqRdOQsTJZoUR-DGqBw4kiFn6HaTl_pLKC-EAgxKavpubPkHOQHLg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
            return;
        }

        // Referencias UI
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        tv_DescripcionUsuario = findViewById(R.id.tv_DescripcionUsuario);
        btnVerTiempoUsuario = findViewById(R.id.btnVerTiempoUsuario);

        cargarDatosUsuario();

        ivProfilePicture.setOnClickListener(v -> seleccionarImagen());
        btnEditarPerfil.setOnClickListener(v -> startActivity(new Intent(Perfil.this, EditarPerfil.class)));
        btnCerrarSesion.setOnClickListener(v -> startActivity(new Intent(Perfil.this, CerrarSesion.class)));
        btnEliminarCuenta.setOnClickListener(v -> startActivity(new Intent(Perfil.this, EliminarCuenta.class)));
        btnVerTiempoUsuario.setOnClickListener(v -> startActivity(new Intent(Perfil.this, vertiempousuario.class)));

        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_perfil);


        if (bottomNavigationView1 != null) {
            // Configurar el listener para los ítems seleccionados
            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    // Navegar a la actividad para buscar perfiles
                    startActivity(new Intent(Perfil.this, Home.class));

                    return true;
                } else if (itemId == R.id.it_new) {
                    // Navegar a la actividad para crear un curso
                    startActivity(new Intent(Perfil.this, CrearCurso.class));
                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    // Navegar a la actividad para ver la Biblioteca
                    startActivity(new Intent(Perfil.this, Biblioteca.class));
                    return true;
                } else if (itemId == R.id.it_perfil) {

                    return true;
                }
                return false;
            });
            // Establecer el ítem seleccionado al inicio (si es necesario)
            bottomNavigationView1.setSelectedItemId(R.id.it_perfil);
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }
    }

    private void cargarDatosUsuario() {
        if (userId == null) return;

        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tv_NombreUsuario.setText(documentSnapshot.getString("nombre"));
                tv_No_Cursos.setText("Cursos creados: " + documentSnapshot.getLong("cursos"));
                tv_DescripcionUsuario.setText(documentSnapshot.getString("descripcion"));
            } else {
                Map<String, Object> userData = new HashMap<>();
                userData.put("nombre", "Usuario");
                userData.put("cursos", 0);
                userData.put("descripcion", "Sin descripción");

                db.collection("usuarios").document(userId).set(userData)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Perfil creado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al crear perfil", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());


    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivProfilePicture.setImageURI(imageUri);

            File archivo = getFileFromUri(imageUri);
            if (archivo != null) {
                DropboxConfig dropboxConfig = new DropboxConfig(ACCESS_TOKEN);
                DbxClientV2 client = dropboxConfig.getClient();

                try (FileInputStream fis = new FileInputStream(archivo)) {
                    FileMetadata metadata = client.files().uploadBuilder("/Aplicaciones/skillfullharmony/Fotos/" + archivo.getName())
                            .uploadAndFinish(fis);
                    Toast.makeText(this, "Imagen subida a Dropbox: " + metadata.getName(), Toast.LENGTH_SHORT).show();
                } catch (UploadErrorException e) {
                    Log.e("Dropbox", "Error al subir archivo", e);
                    Toast.makeText(this, "Error al subir imagen a Dropbox.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e("Dropbox", "Error al leer archivo", e);
                    Toast.makeText(this, "Error al leer el archivo.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("Dropbox", "Error desconocido", e);
                    Toast.makeText(this, "Error desconocido al subir imagen.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No se pudo obtener el archivo de la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getFileFromUri(Uri uri) {
        String path = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        }
        return path != null ? new File(path) : null;
    }

}
