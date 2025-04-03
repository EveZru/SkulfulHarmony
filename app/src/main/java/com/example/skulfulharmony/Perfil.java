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

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_No_Cursos, tv_DescripcionUsuario;
    private Button btnEditarPerfil, btnCerrarSesion, btnEliminarCuenta;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId; // Para identificar el usuario actual
    private static final String ACCESS_TOKEN = "tsl.u.AFot3oRZvqPIpyuc0dF55DFNpAJPV4PvWKX1qXx0wo7f-NOkvykzr9bDiUVHPKz08iZDoRAXqV01dsKBIG-EKG-Rt-amCBsBgTaZOSHFWRov0KKUyPFJPLc32__6EhCUymW7eeKu0dpFqyT3uLxMr-up8wEpIEw1Tb3n3mLqYGGh4HdRtHiR-BXLGWnXB_iDAIscdqxQpixX1U1HDpcHJmNgF1zdoXo4841CUj5ttJZPsnG4OpVY0tjBuvf-7o25YeJLHh3v1P4XmlTJm__mEAb5HNZgyrfz-z6is6dvD-AFrnn1dVRVicrVJVhZ_JPp6OOt6PYO2eoxvOhlkitdzxtaR0I1bVFXkEUASGc7Mw24LEGxsF6FLy94W5qfmDI2n-efSZY-7YtvYzQAh7Ng3DEFFulvwMUtKtswKtcKeLsdqFRPP4Cfyj06wVmbWFawMkl6cSfjIemMjRjUWIEjJbtl6OkL74Y5myx0RIfK-CfsDb98neoIecV_kX9xtJd3mS3DTM_FOWgugjGLjBRp_FXYiS1s8S16VHP4h0_OHaOC3jmjNyE52s0FOXvTqxOv1_9IW2ZXZH8JodpAg4uO5Mx1tnkHfQX4JNr5ymmLmF1U-Vdb5_WOrAez47kHhSeU_N7rcWz1XQL1UtfVRL1ypWJPVsCRZ9kwMGpFoHhVPS7kyg4Ovz8k4-4uCDIElGmj8XHsq7FFXogZMH2owb7QynJlg6IHpqbtLrNJtvwXfhBf5veuMeWILLP9GFVl-MKLvoZHY0fCpvMhtsDm01HtfNA6WYTajkQIN2VkrKVn6qJkaJ_vdxyBxyaa79g3SSKcfizci6i-_Yu4rFC3p1Q89Q1y6Z6LpgL_nTvhcichSKUg5G3gy6kXICuk6jEwCJWbh1G13t8V5IyrvRLQYy5YEpp4pJ-_0AkBAfo4DP9bW7phkmUwwOh3pc3Rf4w8TIpDl6f88wutQiRmwX3RieU0vEBQK5v6-lX_sC8v7g2mBzv1zMgKFoXsBWaA5XjM0A4u-ZV4jlmQBWWny9rfPdEltR2hPhQPC5NSCl8kk0PaUleiGaEei25sg8hTYEgzGp7tDMduqROqlLYAP2h5r3FptdGGqgMXFhZkiep0ZIeg2_2EApiVcZjFQEAJ7fd19L9FhqKP08X9LSNs51HJbF76MVvYmcI1If4JJ-VEnpzXODYoGrj8CFs_Hk5mrUo9dPt4ZaHFwj73WIboL6SkrkRAbRiEKJi4qtdlF1J3Z8nnMMAPa0WTnHpkvrVALpT97HlfM1Lwg_UIioe5ZWjVPUDmlS5ieMIeVOL_oGBULk7VdhO6y2TeDHHEGZetwSQ9TaMelnlAkfM0lZJl1nF85rbEC7e7iP7DzrJeqk2euSdHR9e7HjU94CXleq0gyZt_4Oe_ExFnNimW3K0lVqUaRRZ_dHliSr7nwXipgDh_qeVWOzfS4Q";  // Sustituir con el access token de Dropbox

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
            // Si no hay usuario autenticado, redirigir a IniciarSesion
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
            return;
        }

        // Referencias a los elementos de UI
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        tv_DescripcionUsuario = findViewById(R.id.tv_DescripcionUsuario);

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Evento para cambiar la foto de perfil
        ivProfilePicture.setOnClickListener(v -> seleccionarImagen());

        // Evento para editar el perfil
        btnEditarPerfil.setOnClickListener(v -> {
            // Cambiar a la actividad de editar perfil
            Intent intent = new Intent(Perfil.this, EditarPerfil.class);
            startActivity(intent);
        });

        // Evento para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            startActivity(new Intent(Perfil.this, CerrarSesion.class));
        });

        // Evento para eliminar cuenta
        btnEliminarCuenta.setOnClickListener(v -> {
            startActivity(new Intent(Perfil.this, EliminarCuenta.class));
        });
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
                // Si no hay datos, se crean por primera vez
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

            // Convertir el URI a archivo
            File archivo = new File(getRealPathFromURI(imageUri));
            // Comprimir la imagen antes de subirla
            File archivoComprimido = new File(archivo.getAbsolutePath() + ".zip");
            try {
                // Comprimir imagen
                ComprimirZip compressor = new ComprimirZip();
                compressor.compressFile(archivo, archivoComprimido); // Comprimir

                // Crear la configuración de Dropbox
                DropboxConfig dropboxConfig = new DropboxConfig(ACCESS_TOKEN);  // Usa el access token
                DbxClientV2 client = dropboxConfig.getClient();  // Obtiene el cliente de Dropbox

                // Subir imagen comprimida a Dropbox
                try (FileInputStream fis = new FileInputStream(archivoComprimido)) {
                    // Verifica si la ruta es correcta
                    System.out.println("Subiendo archivo a Dropbox en /Fotos/" + archivoComprimido.getName());
                    FileMetadata metadata = client.files().uploadBuilder("/Aplicaciones/skillfullharmony/Fotos" + archivoComprimido.getName())
                            .uploadAndFinish(fis);
                    Toast.makeText(this, "Imagen de perfil subida a Dropbox: " + metadata.getName(), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al subir la imagen a Dropbox: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para obtener la ruta real del URI de la imagen seleccionada
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        try (Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null)) {
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        }
        return null;
    }
}