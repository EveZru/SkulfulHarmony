package com.example.skulfulharmony;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ver_mi_perfil extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_No_Cursos;
    private Button btnEditarPerfil, btnCerrarSesion, btnEliminarCuenta;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_mi_perfil);

        // Referencias a los elementos de UI
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Evento para cambiar la foto de perfil
        ivProfilePicture.setOnClickListener(v -> seleccionarImagen());

        // Evento para editar el perfil
        btnEditarPerfil.setOnClickListener(v -> mostrarDialogoEditarPerfil());

        // Evento para cerrar sesión (redirige a cerrar_sesion.java)
        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(ver_mi_perfil.this, cerrar_sesion.class);
            startActivity(intent);
        });

        // Evento para eliminar cuenta (redirige a eliminar_cuenta.java)
        btnEliminarCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(ver_mi_perfil.this, eliminar_cuenta.class);
            startActivity(intent);
        });

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void cargarDatosUsuario() {
        // Cargar nombre guardado
        String nombreUsuario = sharedPreferences.getString("nombre", "Nombre de Usuario");
        tv_NombreUsuario.setText(nombreUsuario);

        // Cargar número de cursos
        int cursos = sharedPreferences.getInt("cursos", 0);
        tv_No_Cursos.setText("Cursos creados: " + cursos);

        // Cargar imagen de perfil local
        String imagePath = sharedPreferences.getString("profileImage", null);
        if (imagePath != null) {
            ivProfilePicture.setImageURI(Uri.fromFile(new File(imagePath)));
        }
    }

    private void mostrarDialogoEditarPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Perfil");

        EditText input = new EditText(this);
        input.setHint("Nuevo nombre de usuario");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                actualizarNombre(nuevoNombre);
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void actualizarNombre(String nuevoNombre) {
        // Guardar nombre en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nombre", nuevoNombre);
        editor.apply();

        // Actualizar en la interfaz
        tv_NombreUsuario.setText(nuevoNombre);
        Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
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
            guardarImagenLocalmente(imageUri);
        }
    }

    private void guardarImagenLocalmente(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            File file = new File(getFilesDir(), "profile_picture.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            // Guardar la ruta en SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profileImage", file.getAbsolutePath());
            editor.apply();

            Toast.makeText(this, "Imagen guardada localmente", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}