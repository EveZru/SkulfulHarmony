package com.example.skulfulharmony;

import android.content.Intent;
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

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.it_homme) {
                startActivity(new Intent(Biblioteca.this, Home.class));
                return true;
            } else if (itemId == R.id.it_new) {
                startActivity(new Intent(Biblioteca.this, CrearCurso.class));
                return true;
            } else if (itemId == R.id.it_seguidos) {
                return true;
            } else if (itemId == R.id.it_perfil) {
                startActivity(new Intent(Biblioteca.this, Perfil.class));
                return true;
            }

            return false;
        });

    }
}