package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Biblioteca extends AppCompatActivity {

    private DbUser dbUser = new DbUser(this);
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);  // Llamar a setContentView solo una vez

        bottomNavigationView = findViewById(R.id.barra_navegacion);  // Inicializa el BottomNavigationView

        EdgeToEdge.enable(this);
        // Asegúrate de que el ID 'main' exista en el XML
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (bottomNavigationView != null) {
            // Configurar el listener para los ítems seleccionados
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    // Navegar a la actividad de home
                    startActivity(new Intent(Biblioteca.this, Home.class));
                    return true;
                } else if (itemId == R.id.it_new) {
                    // Navegar a la actividad para crear un curso
                    startActivity(new Intent(Biblioteca.this, CrearCurso.class));
                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    // Estamos en biblioteca
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    // Navegar a la actividad para buscar perfiles
                    startActivity(new Intent(Biblioteca.this, Perfil.class));
                    return true;
                }
                return false;
            });

            // Establecer el ítem seleccionado al inicio
            bottomNavigationView.setSelectedItemId(R.id.it_seguidos);  // Establecer el ítem de "seguidos" como seleccionado al inicio
        } else {
            Log.e("Error", "La vista BottomNavigationView no se ha encontrado");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No es necesario hacer nada aquí si ya se ha configurado el ítem seleccionado en onCreate
    }
}
