package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int backPressCount = 0; // Contador de veces que se presiona atrás
    private Handler backPressHandler = new Handler();
    private EditText et_buscarhome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        et_buscarhome=findViewById(R.id.et_buscarhome);

        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacionhome);
        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario está autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Si no hay usuario, redirigir a Login
            Intent intent = new Intent(home.this, login.class);
            startActivity(intent);
            finish(); // Evita que el usuario vuelva a Home si no está logueado
        }

        //  Listener para abrir la actividad de búsqueda
        et_buscarhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, busqueda.class);
                intent.putExtra("focus", true); // Enviar extra para enfocar el EditText en la otra actividad
                startActivity(intent);
            }
        });


        // Manejo del botón de retroceso con 3 clics
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressCount++;
                if (backPressCount == 3) {
                    moveTaskToBack(true); // 🔹 Minimiza la aplicación en lugar de cerrarla
                } else {
                    Toast.makeText(home.this, "Presiona atrás " + (3 - backPressCount) + " veces más para salir", Toast.LENGTH_SHORT).show();
                    backPressHandler.postDelayed(() -> backPressCount = 0, 2000); // Reinicia el contador después de 2 segundos
                }
            }
        });

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });





        // Configurar el listener para los ítems seleccionados
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.it_homme) {
                // Acción para Home
                return true;
            } else if (itemId == R.id.it_new) {
                // Navegar a la actividad para crear un curso
                startActivity(new Intent(home.this, crear_curso.class));
                return true;
            } else if (itemId == R.id.it_seguidos) {
                // Navegar a la actividad para ver los seguidos
                startActivity(new Intent(home.this, seguidos.class));
                return true;
            } else if (itemId == R.id.it_perfil) {
                // Navegar a la actividad para buscar perfiles
                startActivity(new Intent(home.this, ver_mi_perfil.class));
                return true;
            }

            return false;

        });
        //bottomNavigationView.setSelectedItemId(R.id.it_homme);
        bottomNavigationView.setSelectedItemId(R.id.it_homme);




    }
    @Override
    protected void onResume() {
        super.onResume();
        // Asegúrate de que el ítem de "Home" esté seleccionado cuando regreses a la actividad
        BottomNavigationView bottomNavigationView = findViewById(R.id.barra_navegacionhome);
        bottomNavigationView.setSelectedItemId(R.id.it_homme);  // Seleccionamos Home
    }
}



/*package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int backPressCount = 0; // Contador de veces que se presiona atrás
    private Handler backPressHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario está autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Si no hay usuario, redirigir a Login
            Intent intent = new Intent(home.this, login.class);
            startActivity(intent);
            finish(); // Evita que el usuario vuelva a Home si no está logueado
        }

        // Manejo del botón de retroceso con 3 clics
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressCount++;
                if (backPressCount == 3) {
                    moveTaskToBack(true); // 🔹 Minimiza la aplicación en lugar de cerrarla
                } else {
                    Toast.makeText(home.this, "Presiona atrás " + (3 - backPressCount) + " veces más para salir", Toast.LENGTH_SHORT).show();
                    backPressHandler.postDelayed(() -> backPressCount = 0, 2000); // Reinicia el contador después de 2 segundos
                }
            }
        });

        // Ajustar el padding si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.it_homme) {
                // Acción para Home
                return true;
            } else if (itemId == R.id.it_new) {
                // Navegar a la actividad para crear un curso
                startActivity(new Intent(home.this, crear_curso.class));
                return true;
            } else if (itemId == R.id.it_seguidos) {
                // Navegar a la actividad para ver los seguidos
                startActivity(new Intent(home.this, seguidos.class));
                return true;
            } else if (itemId == R.id.it_perfil) {
                // Navegar a la actividad para buscar perfiles
                startActivity(new Intent(home.this, busqueda.class));
                return true;
            }

            return false;
        });
    }
}*/