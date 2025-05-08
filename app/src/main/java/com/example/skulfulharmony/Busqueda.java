package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.HistorialManager;
import com.example.skulfulharmony.utils.IndiceInvertidoManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Busqueda extends AppCompatActivity {
    private EditText et_buscar;
    private Button btn_genero, btn_instrumrnto, btn_dificultad;
    private TextView tv_historial;
    private RecyclerView rv_resultados;
    private DbUser dbUser = new DbUser(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busqueda);

        btn_genero = findViewById(R.id.btn_bgenero);
        btn_dificultad = findViewById(R.id.btn_bdificultad);
        btn_instrumrnto = findViewById(R.id.btn_binstumento);
        et_buscar = findViewById(R.id.et_parabuscar);
        tv_historial = findViewById(R.id.tv_historial);
        rv_resultados = findViewById(R.id.rv_resultadosbusqueda);
        rv_resultados.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().getBooleanExtra("focus", false)) {
            et_buscar.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        // Buscar al presionar "Enter"
        et_buscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String query = et_buscar.getText().toString().trim();
                if (!query.isEmpty()) {
                    buscarCursosEnFirebase(query);
                }
                return true;
            }
            return false;
        });

        mostrarHistorial();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void mostrarHistorial() {
        Set<String> historial = HistorialManager.obtenerHistorial(this);

        if (historial.isEmpty()) {
            tv_historial.setText("Aún no has interactuado con ningún contenido.");
        } else {
            StringBuilder sb = new StringBuilder("Historial reciente:\n");
            for (String item : historial) {
                sb.append("- ").append(item).append("\n");
            }
            tv_historial.setText(sb.toString());
        }
    }

    private void buscarCursosEnFirebase(final String textoBusqueda) {  // Agregar 'final' aquí
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cursosRef = db.collection("cursos");

        cursosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Curso> resultados = new ArrayList<>();
            List<Curso> cursos = new ArrayList<>(); // Lista para almacenar todos los cursos

            // Obtener todos los cursos
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Curso curso = doc.toObject(Curso.class);
                if (curso != null) {
                    cursos.add(curso);
                }
            }

            // Crear el índice invertido
            IndiceInvertidoManager indiceInvertidoManager = new IndiceInvertidoManager();
            indiceInvertidoManager.buildIndex(cursos); // Construir el índice invertido con los cursos

            // Convertir la búsqueda en minúsculas para una comparación sin distinción de mayúsculas/minúsculas
            String textoBusquedaLower = textoBusqueda.toLowerCase();  // Asignamos a una nueva variable local

            // Buscar los cursos que contienen la palabra buscada en el título
            for (Curso curso : cursos) {
                // Comparamos el texto de búsqueda con el título del curso (permitiendo coincidencias parciales)
                if (curso.getTitulo().toLowerCase().contains(textoBusquedaLower)) {
                    resultados.add(curso);  // Agregar el curso que coincide
                }
            }

            // Mostrar los resultados
            if (resultados.isEmpty()) {
                Toast.makeText(this, "No se encontraron cursos.", Toast.LENGTH_SHORT).show();
            } else {
                AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(resultados, this);
                rv_resultados.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al buscar cursos", Toast.LENGTH_SHORT).show();
        });
    }
}