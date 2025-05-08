package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterBusquedaUsuarios;
import com.example.skulfulharmony.adapters.AdapterBusquedaCursos;
import com.example.skulfulharmony.databaseinfo.DbUser;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
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
    private RecyclerView rv_resultadosbusqueda;  // Cambiar al ID correcto
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
        rv_resultadosbusqueda = findViewById(R.id.rv_resultadosbusqueda);  // Cambiar al ID correcto
        rv_resultadosbusqueda.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().getBooleanExtra("focus", false)) {
            et_buscar.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        // Buscar al presionar "Enter"
        et_buscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String query = et_buscar.getText().toString().trim();
                if (!query.isEmpty()) {
                    buscarUsuariosYCursosEnFirebase(query);
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

    private void buscarUsuariosYCursosEnFirebase(String textoBusqueda) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Búsqueda de cursos
        CollectionReference cursosRef = db.collection("cursos");
        cursosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Curso> resultadosCursos = new ArrayList<>();
            List<Curso> cursos = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Curso curso = doc.toObject(Curso.class);
                if (curso != null) {
                    cursos.add(curso);
                    Log.d("Busqueda", "Curso encontrado: " + curso.getTitulo()); // Verificación
                }
            }

            // Búsqueda de usuarios
            CollectionReference usuariosRef = db.collection("usuarios");
            usuariosRef.get().addOnSuccessListener(queryDocumentSnapshotsUsuarios -> {
                List<Usuario> resultadosUsuarios = new ArrayList<>();
                List<Usuario> usuarios = new ArrayList<>();
                for (DocumentSnapshot docUsuario : queryDocumentSnapshotsUsuarios) {
                    Usuario usuario = docUsuario.toObject(Usuario.class);
                    if (usuario != null) {
                        usuarios.add(usuario);
                        Log.d("Busqueda", "Usuario encontrado: " + usuario.getNombre()); // Verificación
                    }
                }

                // Mostrar los resultados (usuarios y cursos)
                mostrarUsuarios(resultadosUsuarios);
                mostrarCursos(resultadosCursos);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al buscar usuarios o cursos", Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            Toast.makeText(this, "No se encontraron usuarios.", Toast.LENGTH_SHORT).show();
            Log.d("Busqueda", "No se encontraron usuarios");
        } else {
            Log.d("Busqueda", "Usuarios encontrados: " + usuarios.size());
            AdapterBusquedaUsuarios adapterUsuarios = new AdapterBusquedaUsuarios(usuarios, usuario -> {
                Toast.makeText(this, "Perfil de " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
            });
            rv_resultadosbusqueda.setAdapter(adapterUsuarios);
        }
    }

    private void mostrarCursos(List<Curso> cursos) {
        if (cursos.isEmpty()) {
            Toast.makeText(this, "No se encontraron cursos.", Toast.LENGTH_SHORT).show();
            Log.d("Busqueda", "No se encontraron cursos");
        } else {
            Log.d("Busqueda", "Cursos encontrados: " + cursos.size());
            AdapterBusquedaCursos adapterCursos = new AdapterBusquedaCursos(cursos, curso -> {
                Toast.makeText(this, "Curso: " + curso.getTitulo(), Toast.LENGTH_SHORT).show();
            });
            rv_resultadosbusqueda.setAdapter(adapterCursos);
        }
    }

}