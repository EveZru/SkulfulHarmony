package com.example.skulfulharmony;
import com.example.skulfulharmony.R;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;  // Para BottomNavigationView

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class crear_curso extends AppCompatActivity {
    private EditText etNombreNuevoCurso;
    private Spinner spInstrumento, spNivel, spGenero;
    private Button btnSubirCurso;
    private String NombreNuevoCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_curso);
        etNombreNuevoCurso = findViewById(R.id.et_nombre_nuevo_curso);
        spInstrumento = findViewById(R.id.sp_Instrumento);
        spNivel = findViewById(R.id.sp_Nivel);
        spGenero = findViewById(R.id.sp_Genero);
        btnSubirCurso = findViewById(R.id.btn_subir_curso);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Opciones para los Spinners
        String[] instrumentos = {"Flauta", "Piano", "Guitarra", "Otro"};
        String[] niveles = {"Fácil", "Difícil", "Normal"};
        String[] generos = {"Rock", "Clásica", "Pop", "Otro"};

        setSpinnerAdapter(spInstrumento, instrumentos);
        setSpinnerAdapter(spNivel, niveles);
        setSpinnerAdapter(spGenero, generos);

        //  subir curso
        btnSubirCurso.setOnClickListener(view -> {
            NombreNuevoCurso = etNombreNuevoCurso.getText().toString().trim();

            if (NombreNuevoCurso.isEmpty()) {
                Toast.makeText(crear_curso.this, "Ingresa un nombre para el curso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(crear_curso.this, "Creando clase...", Toast.LENGTH_SHORT).show();

                // Aquí va el código para subir el curso a la base de datos
                // (Este es el lugar donde puedes hacer una petición a Firebase o a tu backend)
            }
        });
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/


        setSpinnerAdapter(spInstrumento, instrumentos);
        setSpinnerAdapter(spNivel, niveles);
        setSpinnerAdapter(spGenero, generos);



        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.it_homme) {
                startActivity(new Intent(crear_curso.this, home.class));
                return true;
            } else if (itemId == R.id.it_new) {
                // Cambiar la actividad para crear el curso
                return true;
            } else if (itemId == R.id.it_seguidos) {
                startActivity(new Intent(crear_curso.this, seguidos.class));
                return true;
            } else if (itemId == R.id.it_perfil) {
                startActivity(new Intent(crear_curso.this, busqueda.class));
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.it_new);


    }
    private void setSpinnerAdapter(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }
}