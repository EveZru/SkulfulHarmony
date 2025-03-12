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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class CrearCuenta extends AppCompatActivity {

    private EditText etCorreo, etContraseña;
    private Button btnCrearCuenta, btnGoToIniciar;
    private ImageButton btnGoogleCrear;
    private ImageView ivTogglePassword;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearcuenta);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias de UI
        etCorreo = findViewById(R.id.Et_correo);
        etContraseña = findViewById(R.id.Et_contraseña);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnGoogleCrear = findViewById(R.id.btn_googlecrear);
        btnGoToIniciar = findViewById(R.id.btnGotoiniciar);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Listeners
        btnGoogleCrear.setOnClickListener(v -> signInWithGoogle());
        btnCrearCuenta.setOnClickListener(v -> registerUser());
        btnGoToIniciar.setOnClickListener(v -> startActivity(new Intent(CrearCuenta.this, IniciarSesion.class)));

        // Toggle de visibilidad de la contraseña
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

    private void registerUser() {
        String email = etCorreo.getText().toString().trim();
        String password = etContraseña.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(CrearCuenta.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CrearCuenta.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CrearCuenta.this, Home.class);
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
                        Toast.makeText(CrearCuenta.this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CrearCuenta.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CrearCuenta.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}