package com.example.skulfulharmony;

// 🧠 Clases internas del proyecto
import com.example.skulfulharmony.server.zip.ComprimirZip; // Para comprimir archivos
import com.example.skulfulharmony.server.config.DropboxConfig; // Configuración del cliente de Dropbox

// ☁️ Dropbox SDK
import com.dropbox.core.v2.DbxClientV2; // Cliente principal de Dropbox
import com.dropbox.core.v2.files.FileMetadata; // Metadatos de archivos
import com.dropbox.core.v2.files.UploadErrorException; // Excepción al subir archivos
import com.dropbox.core.v2.sharing.SharedLinkMetadata; // Metadatos del enlace compartido

// 📱 Android básico
import android.content.Intent; // Navegación entre actividades
import android.database.Cursor; // Lectura de bases de datos
import android.net.Uri; // Referencia a recursos (como imágenes)
import android.os.Bundle; // Datos entre actividades
import android.os.Handler; // Ejecutar tareas en el hilo principal
import android.os.Looper; // Obtener el hilo principal
import android.util.Log; // Logcat
import android.widget.Button; // Botón
import android.widget.ImageView; // Imagen
import android.widget.TextView; // Texto
import android.widget.Toast; // Mensajes emergentes

// 🔄 Concurrencia y ejecución en segundo plano
import java.util.concurrent.ExecutorService; // Ejecutor de hilos
import java.util.concurrent.Executors; // Utilidad para crear ejecutores

// 🖼️ Librería externa para imágenes
import com.bumptech.glide.Glide; // Cargar imágenes desde URL

// 🧩 Firebase
import com.google.firebase.auth.FirebaseAuth; // Autenticación
import com.google.firebase.auth.FirebaseUser; // Usuario actual
import com.google.firebase.firestore.DocumentReference; // Referencia a documentos
import com.google.firebase.firestore.FirebaseFirestore; // Base de datos Firestore

// 💾 Manejo de archivos
import java.io.File; // Representación de archivo
import java.io.FileInputStream; // Leer archivos
import java.io.FileOutputStream; // Escribir archivos
import java.io.IOException; // Excepciones de I/O
import java.io.InputStream; // Flujo de entrada

// 🗂️ Colecciones
import java.util.HashMap; // Mapa clave-valor
import java.util.Map; // Interfaz de mapa

// 🧭 UI Material Design
import com.google.android.material.bottomnavigation.BottomNavigationView; // Barra de navegación

// 🎨 Compatibilidad
import androidx.appcompat.app.AppCompatActivity; // Actividad compatible

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
    private static final String ACCESS_TOKEN = "sl.u.AFoT4f6Y43DWZfTU2OLuxwLw7d3-PrwZtOt8ET6rDTxU65Z-JJiNK1c92NuY0qdMN35uInQ6rcmPqyxmg1LpRpT2Or26omR-QzoLgNU3T5HYTjJNM0fVfw-h9ClM11UG54NClNpbxm05ck90ft1G6nAC4gINAZ6iFFGE7lHeSsm74I8SWlPbSZ-18T2Q-3x95IoxA_hJ5V0hppj_8VHJMVzk9trDL6Ebz1MW21QFp4KLhcHTibTZpNUP-614DrTVKuDrPHy6EN85iHcWWCRBwwgHh7xx4W6LhbLqcg8_ukgV94wgTfs6oGu5qv7Wfav24T_4ZuGWC6JpZhhFCvIB4ixWwDYVs_6mKTwk8PnhC_rEBKCFtQxmXFs-xk07KDwd_Sge34roi1xHtbSdT4bFFRRsBGLaVrs-yRtUa0I8RNW9ui5Xz-tqnKRO5nRxeu0b-7MUAnQehQOheCvRg3o84LVg1RmhJcQ35O944OKAx_ceCcHuGMBLP4A2HKhwUVeT6XlAf-fGQsG3746uHndeImyJvueG9Rbmg8iN_DwGdORtOdcT6ouIUGS3FJbH6I_Lbl4P2W58bKTM_xUcOdRv3-BY5LrtYzfsafwWPxFPvfZYB2uYWQTOhAeQrEQLRikmpyBlUqkoocHh_-YlpC29nPE07s0hOUHba981-3Q10OvFsUNyC_GKFdxDH8-i_M4kKQ_S1jso_6CYSrXQChUeb94xVbyseVyug-cCtsadxDO_tbqyF8SeBlhMHyWxXFVVraEV6Rc6JDLW7ps6Zyf6JrZOuJT3yNp7nmEs-4cJjdHe6L149gSYjPb-QTWHCavrBQ16Gn4wcZYBy1aiXAtMpFfPV3XluWLga843zgLD052hC-MpnKr2qfomsg8UaVfu80AfCHH9qBvWGo5Ayqs9ocnhjsu_0JIhwquJb0qTSmfupY8KzPrcVuvknlkfMXrBd5swRNkFt56nHheS63jPLibpdUqJpjZ9xz9BBoqN942gS6tKQkY-N2P2JLPdj-DvMHPOlOuxbwCKyIL0_nhO_dZH_qbxiNrPywHxSChQVH0M36IOMehOgZTcKtdn2-ws8WyB9K_jv93Ptpy0sQZCh4hEWJAMLyqku3-YNfzYZSnNz3WPBLuVNmeDlJr3PqJ5LWIROfK2rCsKKKHmhOV0WxEA5xvSkk-DtTcs46AgRjGpeFRdeS3h-AnXH8e_E05wajEyoNmShX_BQSyo7o95103cwP8n5MCDmehVLsHHbh5BmjCGeueWBXZRnniVGUDtVaScUYYV1aRuYQNH4yjABW5QZpq_DPDkG7LSzn2UOWxdp9uyeQMKBvQjqjpvLVHXWkVMRMn1PaDxZiRJPJUcD1RiLHGTlQR2quunTKjQlxm8YkEKjF5OfcGMFNW7fV1Bv1CANnKsFq9EzeIqpBW8cSaX8OZyPnPL66dtTvoasXCC-Q";
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

                String fotoUrl = documentSnapshot.getString("fotoPerfil");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .into(ivProfilePicture); // 🔁 Se mantiene tras cerrar/abrir app
                }
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
            Log.e("Archivo", "No se pudo crear archivo temporal", e);
            return null;
        }
    }

    private void subirImagenADropbox(File archivo) {
        if (archivo == null) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DropboxConfig dropboxConfig = new DropboxConfig(ACCESS_TOKEN);
            DbxClientV2 client = dropboxConfig.getClient();

            try (FileInputStream fis = new FileInputStream(archivo)) {
                FileMetadata metadata = client.files()
                        .uploadBuilder("/Aplicaciones/skillfullharmony/FotosPerfil/" + archivo.getName())
                        .uploadAndFinish(fis);

                SharedLinkMetadata linkMetadata = client.sharing()
                        .createSharedLinkWithSettings(metadata.getPathLower());

                // Convertir a enlace directo
                String urlImagen = linkMetadata.getUrl()
                        .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                        .replace("?dl=0", "");

                // Guardar en Firestore
                guardarFotoEnFirestore(urlImagen);

                // Actualizar imagen en la vista
                handler.post(() -> {
                    Glide.with(this).load(urlImagen).into(ivProfilePicture);
                    Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                });

            } catch (UploadErrorException e) {
                Log.e("Dropbox", "Error al subir archivo", e);
                handler.post(() -> Toast.makeText(this, "Error al subir imagen (UploadError)", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                Log.e("Dropbox", "Error de lectura", e);
                handler.post(() -> Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e("Dropbox", "Error desconocido", e);
                handler.post(() -> Toast.makeText(this, "Error desconocido al subir imagen", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void guardarFotoEnFirestore(String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .update("fotoPerfil", url)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "URL de foto guardada"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar URL", e));
    }

    private String convertirLinkADirecto(String dropboxUrl) {
        // dropboxUrl: https://www.dropbox.com/scl/fi/xxx/imagen.jpg?rlkey=yyy&dl=0
        // queremos:   https://dl.dropboxusercontent.com/scl/fi/xxx/imagen.jpg?rlkey=yyy
        return dropboxUrl.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            Log.d("DEBUG", "URI recibido: " + imageUri); // 🔍 Log para ver si hay Uri

            try {
                getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
            } catch (Exception e) {
                Log.e("DEBUG", "Error al tomar permisos persistentes", e);
            }

            ivProfilePicture.setImageURI(imageUri);

            File archivo = copiarUriAArchivoTemporal(imageUri);

            Log.d("DEBUG", "Archivo generado: " + (archivo != null ? archivo.getAbsolutePath() : "null"));
            Log.d("DEBUG", "Archivo tamaño: " + (archivo != null ? archivo.length() : 0));

            subirImagenADropbox(archivo);
        }
    }
}