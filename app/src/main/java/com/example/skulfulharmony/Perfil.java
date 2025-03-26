package com.example.skulfulharmony;

import com.example.skulfulharmony.server.zip.DescomprimirZip;
import com.example.skulfulharmony.server.zip.ComprimirZip;
import com.example.skulfulharmony.server.config.ConfiguracionFTP;
import com.example.skulfulharmony.server.SubirArchivos.SubirArchivo;
import com.example.skulfulharmony.server.SubirArchivos.RecibirArchivo;
import android.database.Cursor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_No_Cursos, tv_DescripcionUsuario;
    private Button btnEditarPerfil, btnCerrarSesion, btnEliminarCuenta;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId; // Para identificar el usuario actual
    private static final String FTP_SERVER = "192.168.100.117";
    private static final String FTP_USERNAME = "ftpuser";
    private static final String FTP_PASSWORD = "Skillfull2025$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

                // Subir imagen comprimida al FTP
                SubirArchivo subirArchivo = new SubirArchivo();
                ConfiguracionFTP configFTP = new ConfiguracionFTP(FTP_SERVER, FTP_USERNAME, FTP_PASSWORD, 21);
                subirArchivo.subirArchivo(archivoComprimido, configFTP);

                // Mostrar mensaje de éxito
                Toast.makeText(this, "Imagen de perfil subida al servidor", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
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