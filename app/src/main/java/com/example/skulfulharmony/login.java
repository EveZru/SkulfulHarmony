package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity {

    private EditText etCorreoOUser, etContraseña_Iniciar;
    private Button btnIniciar, btnGoToCrear;
    private ImageButton btnGoogleIniciar;
    private ImageView ivTogglePassword;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias de UI
        etCorreoOUser = findViewById(R.id.Et_correoOuser);
        etContraseña_Iniciar = findViewById(R.id.Et_contraseña_iniciar);
        btnIniciar = findViewById(R.id.btnIniciarsecion);
        btnGoogleIniciar = findViewById(R.id.btn_google_iniciar);
        btnGoToCrear = findViewById(R.id.btn_gotocrearcuenta);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Alternar visibilidad de la contraseña
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etContraseña_Iniciar.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etContraseña_Iniciar.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            etContraseña_Iniciar.setSelection(etContraseña_Iniciar.length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Listener para el botón de inicio de sesión
        btnIniciar.setOnClickListener(v -> {
            String email = etCorreoOUser.getText().toString().trim();
            String password = etContraseña_Iniciar.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                String hashedPassword = hashPassword(password);
                mAuth.signInWithEmailAndPassword(email, hashedPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(login.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }
}
