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

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId;
    private static final String ACCESS_TOKEN = "sl.u.AFpnGA2fm042es3fWZfFPyNYQpud9psOECuledq-KQcl2cjunYrZSKeDTedEIPZ_xORYiarMOeL3-gqQaBP5wqQiypVrIGhD8GCy6-wtHt4o97tg0w8VhU5PSUf2DqHtx2G3kKxvByz-9vTbOovULc5cBEZDP3tyMsMxV6tR67h1lZJr-If6ML59Dfo4Louut2GIVOCRvs1pqqlIpmIDUyB1VwznHgFQO8eBp0FMjpbF2stB9ikqofy31VqrKLbbdG-p18xa3PcwVfClrxDpqpLTcYBw7iIWKCmt5BZrL2FJE6kUX3Z81LFCyxy9mQyQ29FXMboeOi_J-B_UJydn9jDJPmlUQpVuAEmQnmL1RLl9I_o7Hmk-232Dl7m29bWgxtSbpB1mUZHjburnenWfS6GSQpYR7Rbqrq7hWDGaA7uKcCVUHjqdUHnopJufZeFlrZgtx0I8_w1F-mq6uldy065EodvGP437f-wyQfwSdUJz0X7ehHyLpomv17_oAr5KwxIurOF2VHsiAy4FoQstOIgH3KRlJdhaZ5-ln6FEfRK3wFK4NGbCArrLaCbeTZd4vZJ2jed6GoTZ4LODCNUgczOsDzu1--ZeDlkh2vY1hu2_dOT4iheoe16S3y3XOTfq5sA74qBxTJvEjWPn8EvMzzxCVRKlP9ugq7JHBWICgL1FF_OrOCqmbioOQqR1P5yptbJaWRvYpa5hKFeq5j7UXwdRzLgkRdyqg7bRQu9RFvdNiuKxJyspY_4_74AeV4SYlG6HqwxlQRuu3gOX3TKR9XYW8IS9QpPLOhM2SE9WgfyEMdm5DeLo3w-YXG3ua_dUyGAJuO2MJdGSfz0VWUoCktH2MaqDFd3IQx5fye_6tDgybJcM20sNseCfBA0wU_j1Pg_qyjpdhXA5AqUs8g3I8xA-Oq8k3_HhFGa6nZYOVWFJ1or258bT7qvSKEQnkWkv00EM7w8Aw7mgAXa5tMjIcsI2c6D5emmUTvRIvnTK0H9t_FBitBJ5CSmRgEJLkJklMQyOtau9fcGotxy8b3TStCZBBYz8oW8V_Udy6e7smWz_aM-kpleUbaW3M8qWYZfYXZS5hPL2mbvkaRPjY2Eivfm1TfQaBD6qQP0tziZkAt-5zflwS1DJxn1GbbUj5V43AVteIMuV68QKqkn9qS4lkm8U4wKD2aH1pTBHzTpopya9bUw80dAOWFr_poV1T3G1I6Z_j0qwEt_tzaW8c-Pthjy8UOhPGNUKLW3l-IaHcbpfA0MtCRfbtcnBx3dQ-oKZcBUj1esW4yN9et1WUSfWsJ4gx-dgQUdNkq4McpQpkVCQeDxDCWqbwiv3vlk3IQ79UHnBROYLxVHWXBNmIR6nO3Qiw5hZIuwmqY19sLU7sstTe0QLBaBtRJN2tQQzd98mIltwNe9NeVPnhxPL501qZlUSJx6AKg6cVDfrWQ1kaMl6PQ";

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