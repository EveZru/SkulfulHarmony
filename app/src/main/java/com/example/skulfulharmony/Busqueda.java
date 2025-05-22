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

        db.collection("cursos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            cursos.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Curso curso = doc.toObject(Curso.class);
                if (curso != null) cursos.add(curso);
            }

            db.collection("usuarios").get().addOnSuccessListener(qdsUsuarios -> {
                usuarios.clear();
                for (DocumentSnapshot doc : qdsUsuarios) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    if (usuario != null) {
                        usuario.setId(doc.getId());  // Asignar el ID del documento Firestore
                        usuarios.add(usuario);
                    }
                }

                List<Object> resultadosCombinados = buscarConIndiceInvertidoMejorado(textoBusqueda, cursos, usuarios);

                if (resultadosCombinados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                } else {
                    AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultadosCombinados, new AdapterBusquedaGeneral.OnItemClickListener() {
                        @Override
                        public void onCursoClick(Curso curso) {
                            Toast.makeText(Busqueda.this, "Curso: " + curso.getTitulo(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onUsuarioClick(Usuario usuario) {
                            Intent intent = new Intent(Busqueda.this, PerfilMostrar.class);
                            intent.putExtra("usuarioId", usuario.getId());  // Solo enviamos el ID
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
        Set<String> cursosEncontrados = new HashSet<>();
        Set<String> usuariosEncontrados = new HashSet<>();

        String textoLower = textoBusqueda.toLowerCase();

        for (Curso c : cursos) {
            if (c.getTitulo().toLowerCase().contains(textoLower)) {
                cursosEncontrados.add(String.valueOf(c.getId()));
            }
        }

        for (Usuario u : usuarios) {
            if (u.getNombre().toLowerCase().contains(textoLower)) {
                usuariosEncontrados.add(u.getNombre().toLowerCase());
            }
        }

        List<Object> resultados = new ArrayList<>();

        for (Curso c : cursos) {
            if (cursosEncontrados.contains(String.valueOf(c.getId()))) {
                resultados.add(c);
            }
        }

        for (Usuario u : usuarios) {
            if (usuariosEncontrados.contains(u.getNombre().toLowerCase())) {
                resultados.add(u);
            }
        }

        return resultados;
    }
}