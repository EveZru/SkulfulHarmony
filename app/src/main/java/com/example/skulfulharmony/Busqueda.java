package com.example.skulfulharmony;

import android.os.Bundle;
import android.util.Log;
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

import com.example.skulfulharmony.adapters.AdapterBusquedaGeneral;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.HistorialManager;
import com.example.skulfulharmony.javaobjects.users.Usuario;
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

    private IndiceInvertidoManager indiceManager = new IndiceInvertidoManager();
    private boolean datosCargados = false;

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

        et_buscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String query = et_buscar.getText().toString().trim();
                Log.d("BUSQUEDA", "Texto ingresado: " + query);
                if (!query.isEmpty()) {
                    buscarConIndice(query);
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

    private void buscarConIndice(String textoBusqueda) {
        if (!datosCargados) {
            Log.d("BUSQUEDA", "Cargando datos desde Firebase...");
            cargarDatosFirebase(() -> mostrarResultados(textoBusqueda));
        } else {
            mostrarResultados(textoBusqueda);
        }
    }

    private void mostrarResultados(String textoBusqueda) {
        List<Object> resultados = indiceManager.buscar(textoBusqueda);
        Log.d("BUSQUEDA", "Resultados encontrados: " + resultados.size());

        TextView titulo = findViewById(R.id.tv_historial);

        if (resultados.isEmpty()) {
            titulo.setText("No se encontraron resultados para: \"" + textoBusqueda + "\"");
        } else {
            titulo.setText("Resultados para: \"" + textoBusqueda + "\"");
        }

        AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultados, this);
        rv_resultados.setLayoutManager(new LinearLayoutManager(this)); // Asegúrate que esté
        rv_resultados.setAdapter(adapter);
    }
    private void cargarDatosFirebase(Runnable onFinish) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cursos").get().addOnSuccessListener(cursosSnapshot -> {
            for (DocumentSnapshot doc : cursosSnapshot.getDocuments()) {
                Curso curso = doc.toObject(Curso.class);
                Log.d("DEBUG_CARGA", "Curso documento: " + doc.getData());
                if (curso != null) {
                    indiceManager.agregarCurso(curso);
                    Log.d("DEBUG_CARGA", "Curso indexado: " + curso.getTitulo());
                }
            }

            db.collection("clases").get().addOnSuccessListener(clasesSnapshot -> {
                for (DocumentSnapshot doc : clasesSnapshot.getDocuments()) {
                    Clase clase = doc.toObject(Clase.class);
                    Log.d("DEBUG_CARGA", "Clase documento: " + doc.getData());
                    if (clase != null) {
                        indiceManager.agregarClase(clase);
                        Log.d("DEBUG_CARGA", "Clase indexada: " + clase.getTitulo());
                    }
                }

                db.collection("usuarios").get().addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot doc : usersSnapshot.getDocuments()) {
                        Usuario usuario = doc.toObject(Usuario.class);
                        Log.d("DEBUG_CARGA", "Usuario documento: " + doc.getData());
                        if (usuario != null) {
                            indiceManager.agregarUsuario(usuario);
                            Log.d("DEBUG_CARGA", "Usuario indexado: " + usuario.getNombre());
                        }
                    }

                    datosCargados = true;
                    Log.d("BUSQUEDA", "Datos cargados correctamente.");
                    onFinish.run();
                });
            });
        }).addOnFailureListener(e -> {
            Log.e("BUSQUEDA", "Error al cargar datos: " + e.getMessage());
            Toast.makeText(this, "Error al cargar datos para búsqueda", Toast.LENGTH_SHORT).show();
        });
    }
}