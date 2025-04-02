package com.example.skulfulharmony;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.clasifications.lists.Dificultades;
import com.example.skulfulharmony.javaobjects.clasifications.lists.Generos;
import com.example.skulfulharmony.javaobjects.clasifications.lists.Instrumentos;
import com.google.android.material.bottomnavigation.BottomNavigationView;  // Para BottomNavigationView


public class CrearCurso extends AppCompatActivity {
    private EditText etNombreNuevoCurso;
    private Spinner spInstrumento, spNivel, spGenero;
    private Button btnSubirCurso;
    private String NombreNuevoCurso;
    private Dificultades dificultades = new Dificultades();
    private Generos generos = new Generos();
    private Instrumentos instrumentos = new Instrumentos();
    private DbUser dbUser = new DbUser(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crearcurso);
        etNombreNuevoCurso = findViewById(R.id.et_nombre_nuevo_curso);
        spInstrumento = findViewById(R.id.sp_Instrumento);
        spNivel = findViewById(R.id.sp_Nivel);
        spGenero = findViewById(R.id.sp_Genero);
        btnSubirCurso = findViewById(R.id.btn_subir_curso);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
/*

        String[] instrumentos = {"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería",
                "Piano", "Ukelele", "Violin", "Canto","Otro"};
        String[] niveles = {"Principiante", "Intermedio","Avanzado"};
        String[] generos = {"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues",
                "Reggaeton", "Reggae", "Clasica", "Coutry", "Metal", "Folk", "Independiente"};

        setSpinnerAdapter(spInstrumento, instrumentos);
        setSpinnerAdapter(spNivel, niveles);
        setSpinnerAdapter(spGenero, generos);*/

        //  subir curso
        btnSubirCurso.setOnClickListener(view -> {
            NombreNuevoCurso = etNombreNuevoCurso.getText().toString().trim();

            if (NombreNuevoCurso.isEmpty()) {
                Toast.makeText(CrearCurso.this, "Ingresa un nombre para el curso", Toast.LENGTH_SHORT).show();
            } else {


                Toast.makeText(CrearCurso.this, "Creando clase...", Toast.LENGTH_SHORT).show();

                // Aquí va el código para subir el curso a la base de datos
                // (Este es el lugar donde puedes hacer una petición a Firebase o a tu backend)
            }
        });
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

//
//        setSpinnerAdapter(spInstrumento, instrumentos);
//        setSpinnerAdapter(spNivel, niveles);
//        setSpinnerAdapter(spGenero, generos);



        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.it_homme) {
                startActivity(new Intent(CrearCurso.this, Home.class));
                return true;
            } else if (itemId == R.id.it_new) {
                // Cambiar la actividad para crear el curso
                return true;
            } else if (itemId == R.id.it_seguidos) {
                startActivity(new Intent(CrearCurso.this, Biblioteca.class));
                return true;
            } else if (itemId == R.id.it_perfil) {
                startActivity(new Intent(CrearCurso.this, Busqueda.class));
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