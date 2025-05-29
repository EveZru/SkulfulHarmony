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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_condiciones);  // Asegúrate de tener el layout adecuado

        // Inicializar las vistas
        checkboxAceptar = findViewById(R.id.checkboxAceptar);
        btnAceptar = findViewById(R.id.botonAceptar);

        // Deshabilitar el botón por defecto
        btnAceptar.setEnabled(false);

        // Configurar el listener para el CheckBox
        checkboxAceptar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Habilitar o deshabilitar el botón según el estado del CheckBox
            btnAceptar.setEnabled(isChecked);
        });

        // Configurar la acción del botón para aceptar los términos
        btnAceptar.setOnClickListener(v -> {
            if (checkboxAceptar.isChecked()) {
                // Guardar la aceptación de los términos en Firebase
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    db.collection("usuarios").document(user.getUid())
                            .update("acceptedTerms", true)
                            .addOnSuccessListener(aVoid -> {
                                // Mostrar un mensaje confirmando la aceptación
                                Toast.makeText(this, "Términos aceptados", Toast.LENGTH_SHORT).show();
                                // Redirigir al Home
                                Intent intent = new Intent(TerminosCondiciones.this, Home.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Si ocurre un error al guardar, mostrar un mensaje
                                Toast.makeText(this, "Error al guardar la aceptación", Toast.LENGTH_SHORT).show();
                            });
                }

            } else {
                // Si el usuario no acepta los términos, mostrar un mensaje de advertencia
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