package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class CrearCuenta extends AppCompatActivity {

    private EditText etCorreo, etContraseña, etUser;
    private Button btnCrearCuenta, btnGoToIniciar;
    private ImageButton btnGoogleCrear;
    private ImageView ivTogglePassword;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private boolean isPasswordVisible = false;

    private static final int REQUEST_PERMISSIONS_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearcuenta);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        requestPermissions();

        etUser = findViewById(R.id.Et_nameuser);
        etCorreo = findViewById(R.id.Et_correo);
        etContraseña = findViewById(R.id.Et_contraseña);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnGoogleCrear = findViewById(R.id.btn_googlecrear);
        btnGoToIniciar = findViewById(R.id.btnGotoiniciar);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        btnGoogleCrear.setOnClickListener(v -> signInWithGoogle());
        btnCrearCuenta.setOnClickListener(v -> registerUser());
        btnGoToIniciar.setOnClickListener(v -> startActivity(new Intent(CrearCuenta.this, IniciarSesion.class)));

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etContraseña.setTransformationMethod(new PasswordTransformationMethod());
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etContraseña.setTransformationMethod(null);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
        });
    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.POST_NOTIFICATIONS
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Se requieren permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser() {
        String email = etCorreo.getText().toString().trim();
        String password = etContraseña.getText().toString().trim();
        String name = etUser.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(CrearCuenta.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        String photoUrl = "https://dl.dropboxusercontent.com/scl/fi/n5hcisqjtnj52rhr54k2l/lol.png?rlkey=nxygu2omun5ktztg0mr23gcr2"; // URL predeterminada, cámbiala según sea necesario

                        // 🔥 Guardar en Firestore con rol usuario
                        FirebaseFirestore.getInstance().collection("usuarios")
                                .document(uid)
                                .set(new HashMap<String, Object>() {{
                                    put("nombre", name);
                                    put("correo", email);
                                    put("rol", "usuario");
                                    put("fotoPerfil", photoUrl);  // Agrega la URL de la foto de perfil
                                    put("seguidores", 0);  // Inicializamos en 0
                                    put("seguidos", 0);    // Inicializamos en 0
                                    put("cursos", 0);
                                    put("acceptedTerms", false); // Asegúrate de agregar este campo para verificar si el usuario aceptó los términos
                                }});

                        DbUser dbUser = new DbUser(this);
                        if (!dbUser.existUser(email)) {
                            Usuario usuario = new Usuario(name, email, null, new Date(System.currentTimeMillis()));
                            if (dbUser.insertUser(usuario) == 0) {
                                Toast.makeText(this, "Error: Problemas al acceder a la base de datos local", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(CrearCuenta.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        // Redirigir al usuario a la pantalla de Términos y Condiciones
                        Intent intent = new Intent(CrearCuenta.this, TerminosCondiciones.class);
                        intent.putExtra("cuentaRecienCreada", true);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(CrearCuenta.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        // 🔥 Guardar en Firestore con rol usuario si no existe
                        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get()
                                .addOnSuccessListener(document -> {
                                    if (!document.exists()) {
                                        FirebaseFirestore.getInstance().collection("usuarios")
                                                .document(uid)
                                                .set(new HashMap<String, Object>() {{
                                                    put("nombre", acct.getDisplayName());
                                                    put("correo", acct.getEmail());
                                                    put("foto", acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "");
                                                    put("rol", "usuario");
                                                }});
                                    }
                                });

                        DbUser dbUser = new DbUser(this);
                        if (!dbUser.existUser(acct.getEmail())) {
                            Date d = new Date();
                            d.setTime(System.currentTimeMillis());
                            Usuario usuario = new Usuario(acct.getDisplayName(), acct.getEmail(), acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "", d);
                            if (dbUser.insertUser(usuario) == 0) {
                                Toast.makeText(this, "Error: Problemas al acceder a la base de datos local", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(CrearCuenta.this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CrearCuenta.this, TerminosCondiciones.class);
                        intent.putExtra("cuentaRecienCreada", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CrearCuenta.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}