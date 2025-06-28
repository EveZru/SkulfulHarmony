package com.example.skulfulharmony;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.skulfulharmony.adapters.EstadisticasPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class admi_populares extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admi_populares);
        // Referencia al BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacionadmi);

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



        TabLayout tabLayout = findViewById(R.id.tabLayoutEstadisticas);
        ViewPager2 viewPager = findViewById(R.id.viewPagerEstadisticas);
        viewPager.setAdapter(new EstadisticasPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Cursos" : "Instrumentos")
        ).attach();
    }
}
