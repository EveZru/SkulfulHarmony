package com.example.skulfulharmony;

import android.content.Intent;
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
import com.example.skulfulharmony.adapters.AdapterBusquedaGeneral;
import com.example.skulfulharmony.javaobjects.users.Usuario;
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
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

public class Busqueda extends AppCompatActivity {

    private EditText et_buscar;
    private RecyclerView rv_resultados;
    private List<Curso> cursos = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busqueda);

        et_buscar = findViewById(R.id.et_parabuscar);
        rv_resultados = findViewById(R.id.rv_resultadosbusqueda);
        rv_resultados.setLayoutManager(new LinearLayoutManager(this));

        et_buscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String query = et_buscar.getText().toString().trim();
                if (!query.isEmpty()) {
                    buscarEnFirebase(query);
                }
                return true;
            }
            return false;
        });
    }

    private void buscarEnFirebase(final String textoBusqueda) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Primero carga cursos
        db.collection("cursos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            cursos.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Curso curso = doc.toObject(Curso.class);
                if (curso != null) cursos.add(curso);
            }

            // Luego usuarios
            db.collection("usuarios").get().addOnSuccessListener(qdsUsuarios -> {
                usuarios.clear();
                for (DocumentSnapshot doc : qdsUsuarios) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    if (usuario != null) usuarios.add(usuario);
                }

                // Ahora busca usando índice invertido mejorado
                List<Object> resultadosCombinados = buscarConIndiceInvertidoMejorado(textoBusqueda, cursos, usuarios);

                if (resultadosCombinados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                } else {
                    AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultadosCombinados, new AdapterBusquedaGeneral.OnItemClickListener() {
                        @Override
                        public void onCursoClick(Curso curso) {
                            Toast.makeText(Busqueda.this, "Curso: " + curso.getTitulo(), Toast.LENGTH_SHORT).show();
                            // Aquí puedes abrir detalle curso
                        }

                        @Override
                        public void onUsuarioClick(Usuario usuario) {
                            Intent intent = new Intent(Busqueda.this, perfil_mostrar.class);
                            intent.putExtra("usuario", usuario);  // Usuario debe implementar Serializable
                            startActivity(intent);
                        }
                    });
                    rv_resultados.setAdapter(adapter);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al buscar cursos", Toast.LENGTH_SHORT).show();
        });
    }

    private List<Object> buscarConIndiceInvertidoMejorado(String textoBusqueda, List<Curso> cursos, List<Usuario> usuarios) {
        // Construimos índice invertido más flexible (mapa palabra->lista IDs)
        Map<String, List<String>> indiceCursos = new HashMap<>();
        Map<String, List<String>> indiceUsuarios = new HashMap<>();

        // Indexar cursos (palabra -> id del curso)
        for (Curso c : cursos) {
            if (c != null && c.getTitulo() != null) {
                String[] palabras = c.getTitulo().toLowerCase().split("\\s+");
                for (String palabra : palabras) {
                    indiceCursos.computeIfAbsent(palabra, k -> new ArrayList<>()).add(String.valueOf(c.getId()));
                }
            }
        }

        // Indexar usuarios (palabra -> nombre usuario en minúsculas)
        for (Usuario u : usuarios) {
            if (u != null && u.getNombre() != null) {
                String[] palabras = u.getNombre().toLowerCase().split("\\s+");
                for (String palabra : palabras) {
                    indiceUsuarios.computeIfAbsent(palabra, k -> new ArrayList<>()).add(u.getNombre().toLowerCase());
                }
            }
        }

        String busquedaLower = textoBusqueda.toLowerCase();

        // Buscar cursos que contengan la palabra (parcial)
        Set<String> cursosEncontrados = new HashSet<>();
        for (String key : indiceCursos.keySet()) {
            if (key.contains(busquedaLower) || busquedaLower.contains(key)) {
                cursosEncontrados.addAll(indiceCursos.get(key));
            }
        }

        // Buscar usuarios que contengan la palabra (parcial)
        Set<String> usuariosEncontrados = new HashSet<>();
        for (String key : indiceUsuarios.keySet()) {
            if (key.contains(busquedaLower) || busquedaLower.contains(key)) {
                usuariosEncontrados.addAll(indiceUsuarios.get(key));
            }
        }

        List<Object> resultados = new ArrayList<>();

        // Agregar cursos que coinciden
        for (Curso c : cursos) {
            if (cursosEncontrados.contains(String.valueOf(c.getId()))) {
                resultados.add(c);
            }
        }

        // Agregar usuarios que coinciden
        for (Usuario u : usuarios) {
            if (usuariosEncontrados.contains(u.getNombre().toLowerCase())) {
                resultados.add(u);
            }
        }

        return resultados;
    }
}