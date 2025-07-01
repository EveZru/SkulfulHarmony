package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

public class TerminosCondiciones extends AppCompatActivity {

    private CheckBox checkboxAceptar;
    private Button btnAceptar;
    private TextView tvTerms;
    private Boolean cuentaRecienCreada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_condiciones);

        checkboxAceptar = findViewById(R.id.checkboxAceptar);
        btnAceptar = findViewById(R.id.botonAceptar);

        btnAceptar.setEnabled(false);

        checkboxAceptar.setOnCheckedChangeListener((buttonView, isChecked) -> {

            btnAceptar.setEnabled(isChecked);
        });

        cuentaRecienCreada = getIntent().getBooleanExtra("cuentaRecienCreada", false);

        btnAceptar.setOnClickListener(v -> {
            if (checkboxAceptar.isChecked()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    db.collection("usuarios").document(user.getUid())
                            .update("acceptedTerms", true)
                            .addOnSuccessListener(aVoid -> {

                                Toast.makeText(this, "Términos aceptados", Toast.LENGTH_SHORT).show();

                                if (cuentaRecienCreada) {
                                    Intent intent = new Intent(TerminosCondiciones.this, PreguntasRecomendacion.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Intent intent = new Intent(TerminosCondiciones.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(this, "Error al guardar la aceptación", Toast.LENGTH_SHORT).show();
                            });
                }

            } else {

                Toast.makeText(this, "Debes aceptar los términos para continuar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Redirigir al login si no se han aceptado los términos
        Intent intent = new Intent(TerminosCondiciones.this, IniciarSesion.class);
        startActivity(intent);
        finishAffinity(); // Cierra todas las actividades previas
        super.onBackPressed();
    }
}