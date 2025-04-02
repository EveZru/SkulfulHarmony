package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ApiException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private EditText etNameUser, etCorreo, etContraseña;
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
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias de UI
        etNameUser = findViewById(R.id.Et_nameuser);
        etCorreo = findViewById(R.id.Et_correo);
        etContraseña = findViewById(R.id.Et_contraseña);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnGoogleCrear = findViewById(R.id.btn_googlecrear);
        btnGoToIniciar = findViewById(R.id.btnGotoiniciar);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Listeners
        btnGoogleCrear.setOnClickListener(v -> signInWithGoogle());
        btnCrearCuenta.setOnClickListener(v -> registerUser());
        btnGoToIniciar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, login.class)));

        // Configurar campos para borrar texto predeterminado al hacer clic
        setupEditTextClearOnClick(etNameUser);
        setupEditTextClearOnClick(etCorreo);
        setupEditTextClearOnClick(etContraseña);

        // Configurar campo de contraseña para ocultar/mostrar texto
        setupPasswordToggle();
    }

    private void registerUser() {
        String email = etCorreo.getText().toString().trim();
        String password = etContraseña.getText().toString().trim();

        if (!validarCampos()) {
            Toast.makeText(MainActivity.this, "Llena todos los campos por favor", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(password);

        mAuth.createUserWithEmailAndPassword(email, hashedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Redirigir al usuario o manejar el registro exitoso
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Bienvenido " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        // Redirigir al usuario o manejar el inicio de sesión exitoso
                    } else {
                        Toast.makeText(MainActivity.this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validarCampos() {
        String nombreUsuario = etNameUser.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contraseña = etContraseña.getText().toString().trim();

        // Verificar que los campos no estén vacíos y no contengan los textos predeterminados
        return !nombreUsuario.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty()
                && !nombreUsuario.equalsIgnoreCase("Nombre de usuario")
                && !correo.equalsIgnoreCase("Correo")
                && !contraseña.equalsIgnoreCase("Contraseña");
    }

    private void setupEditTextClearOnClick(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setText("");
            }
        });
    }

    private void setupPasswordToggle() {
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etContraseña.setInputType(129); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etContraseña.setInputType(144); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            etContraseña.setSelection(etContraseña.length());
            isPasswordVisible = !isPasswordVisible;
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
