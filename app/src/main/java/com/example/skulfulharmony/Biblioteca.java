package com.example.skulfulharmony;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Biblioteca extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);
        // @SuppressLint("RestrictedApi") BottomNavigationItemView bottomNavigationItemView=findViewById(R.id.barra_navegacion);


      /*  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguidos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();


            if (itemId == R.id.it_homme) {
                // Navegar a la actividad para Home
                startActivity(new Intent(seguidos.this, home.class));
                return true;
            } else if (itemId == R.id.it_new) {
                // Navegar a la actividad para crear clase
                startActivity(new Intent(seguidos.this, seguidos.class));
                return true;
            } else if (itemId == R.id.it_seguidos) {
                // Accion para Ver los seguidos
                return true;
            } else if (itemId == R.id.it_perfil) {
                // Navegar a la actividad para buscar perfiles
                startActivity(new Intent(seguidos.this, ver_mi_perfil.class));
                return true;
            }

            return false;

        });

    }
}