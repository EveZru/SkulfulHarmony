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

import androidx.appcompat.app.AppCompatActivity;

import com.example.skulfulharmony.home;
import com.example.skulfulharmony.RecuperarContr;
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

public class login extends AppCompatActivity {

    private EditText etCorreoOUser, etContraseña_Iniciar;
    private Button btnIniciar, btnGoToCrear;
    private ImageButton btnGoogleIniciar;
    private ImageView ivTogglePassword;
    private TextView tvRecuperarContraseña;  // Nuevo botón de recuperación

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
        tvRecuperarContraseña = findViewById(R.id.tvRecuperarContraseña);  // Referencia al nuevo botón

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Alternar visibilidad de la contraseña
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

        // Iniciar sesión con correo y contraseña
        btnIniciar.setOnClickListener(v -> {
            String email = etCorreoOUser.getText().toString().trim();
            String password = etContraseña_Iniciar.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(login.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();

                                // Redirigir a HomeActivity
                                Intent intent = new Intent(login.this, home.class);
                                startActivity(intent);
                                finish(); // Evita que el usuario vuelva a la pantalla de login
                            } else {
                                Toast.makeText(login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Botón para iniciar sesión con Google
        btnGoogleIniciar.setOnClickListener(v -> signInWithGoogle());

        // Botón para ir a la pantalla de creación de cuenta
        btnGoToCrear.setOnClickListener(v -> startActivity(new Intent(login.this, MainActivity.class)));

        // Botón para ir a la recuperación de contraseña
        tvRecuperarContraseña.setOnClickListener(v -> startActivity(new Intent(login.this, RecuperarContr.class)));
    }

    // Iniciar sesión con Google
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
                        Toast.makeText(login.this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();

                        // Redirigir a HomeActivity
                        Intent intent = new Intent(login.this, home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(login.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}