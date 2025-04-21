package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class admi_populares extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admi_populares);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia al BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacion1);

        // Seleccionar el ítem actual
        bottomNavigationView.setSelectedItemId(R.id.menu_estadisticas);

        // Listener para cambiar de actividad
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_denuncias) {
                startActivity(new Intent(this, admi_denuncias.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.menu_estadisticas) {

                return true;
            } else if (itemId == R.id.menu_perfil) {
                startActivity(new Intent(this, perfil_admin.class));
                overridePendingTransition(0, 0);
                return true; // Ya estamos aquí
            }

            return false;
        });
    }
}
