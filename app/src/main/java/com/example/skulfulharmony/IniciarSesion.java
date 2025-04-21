package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.content.ActivityNotFoundException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.os.Environment;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class IniciarSesion extends AppCompatActivity {

    private EditText etCorreoOUser, etContraseña_Iniciar;
    private Button btnIniciar, btnGoToCrear;
    private ImageButton btnGoogleIniciar;
    private ImageView ivTogglePassword;
    private TextView tvRecuperarContraseña;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private boolean isPasswordVisible = false;

    private DbUser dbUser;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciarsesion);

        DbUser dbUser1 = new DbUser(this);
        if (dbUser1.anyUser()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("usuarios").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String rol = documentSnapshot.getString("rol");
                                Intent intent;
                                if ("admin".equals(rol)) {
                                    intent = new Intent(IniciarSesion.this, admi_populares.class);
                                } else {
                                    intent = new Intent(IniciarSesion.this, Home.class);
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }


        mAuth = FirebaseAuth.getInstance();

        etCorreoOUser = findViewById(R.id.Et_correoOuser);
        etContraseña_Iniciar = findViewById(R.id.Et_contraseña_iniciar);
        btnIniciar = findViewById(R.id.btnIniciarsecion);
        btnGoogleIniciar = findViewById(R.id.btn_google_iniciar);
        btnGoToCrear = findViewById(R.id.btn_gotocrearcuenta);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        tvRecuperarContraseña = findViewById(R.id.tvRecuperarContraseña);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        requestPermissions();

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etContraseña_Iniciar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etContraseña_Iniciar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            etContraseña_Iniciar.setSelection(etContraseña_Iniciar.length());
            isPasswordVisible = !isPasswordVisible;
        });

        btnIniciar.setOnClickListener(v -> {
            String email = etCorreoOUser.getText().toString().trim();
            String password = etContraseña_Iniciar.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(IniciarSesion.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(IniciarSesion.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();

                                DbUser dbUser = new DbUser(this);

                                if(!dbUser.existUser(user.getEmail())){
                                    Usuario usuario = new Usuario(email, password, "Perfil xd", new Date(System.currentTimeMillis()));
                                    if(dbUser.insertUser(usuario) == 0){
                                        Toast.makeText(this, "Error: Problemas al acceder a la base de datos local", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                // 🔥 VERIFICACIÓN DE ROL
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("usuarios").document(user.getUid()).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String rol = documentSnapshot.getString("rol");
                                                Intent intent;
                                                if ("admin".equals(rol)) {
                                                    intent = new Intent(IniciarSesion.this, admi_populares.class);
                                                } else {
                                                    intent = new Intent(IniciarSesion.this, Home.class);
                                                }
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(IniciarSesion.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Toast.makeText(IniciarSesion.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnGoogleIniciar.setOnClickListener(v -> signInWithGoogle());

        btnGoToCrear.setOnClickListener(v -> startActivity(new Intent(IniciarSesion.this, CrearCuenta.class)));

        tvRecuperarContraseña.setOnClickListener(v -> startActivity(new Intent(IniciarSesion.this, RecuperarContrasena.class)));
    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                        Toast.makeText(IniciarSesion.this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("usuarios").document(user.getUid()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String rol = documentSnapshot.getString("rol");
                                        if ("admin".equals(rol)) {
                                            startActivity(new Intent(IniciarSesion.this, admi_populares.class));
                                        } else {
                                            startActivity(new Intent(IniciarSesion.this, Home.class));
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(IniciarSesion.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(IniciarSesion.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}