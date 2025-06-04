package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterBusquedaGeneral;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Busqueda extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText et_buscar;
    private RecyclerView rv_resultados;
    private Button btnGenero, btnInstrumento, btnDificultad;

    private String filtroGenero = null;
    private String filtroInstrumento = null;
    private String filtroDificultad = null;

    private final String[] instrumentos = {"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería", "Piano", "Ukelele", "Violin", "Canto", "Otro"};
    private final String[] niveles = {"Principiante", "Intermedio", "Avanzado"};
    private final String[] generos = {"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues", "Reggaeton", "Reggae", "Clasica", "Country", "Metal", "Folk", "Independiente"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busqueda);

        db = FirebaseFirestore.getInstance();

        et_buscar = findViewById(R.id.et_parabuscar);
        rv_resultados = findViewById(R.id.rv_resultadosbusqueda);
        rv_resultados.setLayoutManager(new LinearLayoutManager(this));

        btnGenero = findViewById(R.id.btn_bgenero);
        btnInstrumento = findViewById(R.id.btn_binstumento);
        btnDificultad = findViewById(R.id.btn_bdificultad);

        btnGenero.setOnClickListener(v -> mostrarDialogoSeleccion("Selecciona un género", generos, opcion -> {
            filtroGenero = opcion;
            btnGenero.setText(filtroGenero);
            aplicarFiltros();
        }));

        btnInstrumento.setOnClickListener(v -> mostrarDialogoSeleccion("Selecciona un instrumento", instrumentos, opcion -> {
            filtroInstrumento = opcion;
            btnInstrumento.setText(filtroInstrumento);
            aplicarFiltros();
        }));

        btnDificultad.setOnClickListener(v -> mostrarDialogoSeleccion("Selecciona dificultad", niveles, opcion -> {
            filtroDificultad = opcion;
            btnDificultad.setText(filtroDificultad);
            aplicarFiltros();
        }));

        et_buscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String query = et_buscar.getText().toString().trim();
                if (!query.isEmpty()) {
                    buscarGeneral(query);
                }
                return true;
            }
            return false;
        });
    }

    private void mostrarDialogoSeleccion(String titulo, String[] opciones, OnOpcionSeleccionadaListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(opciones, (dialog, which) -> listener.onSeleccion(opciones[which]))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    interface OnOpcionSeleccionadaListener {
        void onSeleccion(String opcion);
    }

    private void buscarGeneral(String textoBusqueda) {
        List<Curso> cursos = new ArrayList<>();
        List<Usuario> usuarios = new ArrayList<>();
        List<Clase> clases = new ArrayList<>();

        db.collection("cursos").get().addOnSuccessListener(queryCursos -> {
            for (DocumentSnapshot doc : queryCursos) {
                Curso curso = doc.toObject(Curso.class);
                if (curso != null) cursos.add(curso);
            }

            db.collection("usuarios").get().addOnSuccessListener(queryUsuarios -> {
                for (DocumentSnapshot doc : queryUsuarios) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    String rol = doc.getString("rol");
                    if (usuario != null && (rol == null || !rol.equalsIgnoreCase("admin"))) {
                        usuario.setId(doc.getId());
                        usuarios.add(usuario);
                    }
                }

                db.collection("clases").get().addOnSuccessListener(queryClases -> {
                    for (DocumentSnapshot doc : queryClases) {
                        Clase clase = doc.toObject(Clase.class);
                        if (clase != null) clases.add(clase);
                    }

                    List<Object> resultados = buscarEnTodos(textoBusqueda, cursos, usuarios, clases);

                    if (resultados.isEmpty()) {
                        Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                    }

                    AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultados, new AdapterBusquedaGeneral.OnItemClickListener() {
                        @Override
                        public void onCursoClick(Curso curso) {
                            startActivity(new Intent(Busqueda.this, Ver_cursos.class).putExtra("idCurso", curso.getId()));
                        }

                        @Override
                        public void onUsuarioClick(Usuario usuario) {
                            startActivity(new Intent(Busqueda.this, PerfilMostrar.class).putExtra("usuarioId", usuario.getId()));
                        }

                        @Override
                        public void onClaseClick(Clase clase) {
                            Intent intent = new Intent(Busqueda.this, Ver_clases.class);
                            intent.putExtra("idClase", clase.getIdClase());
                            intent.putExtra("idCurso", clase.getIdCurso());
                            startActivity(intent);
                        }
                    });

                    rv_resultados.setAdapter(adapter);

                }).addOnFailureListener(e -> Toast.makeText(this, "Error al buscar clases", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al buscar cursos", Toast.LENGTH_SHORT).show());
    }

    private List<Object> buscarEnTodos(String texto, List<Curso> cursos, List<Usuario> usuarios, List<Clase> clases) {
        String textoLower = texto.toLowerCase();
        List<Object> resultados = new ArrayList<>();

        for (Curso c : cursos) {
            if (c.getTitulo() != null && c.getTitulo().toLowerCase().contains(textoLower)) {
                resultados.add(c);
            }
        }

        for (Usuario u : usuarios) {
            if (u.getNombre() != null && u.getNombre().toLowerCase().contains(textoLower)) {
                resultados.add(u);
            }
        }

        for (Clase cl : clases) {
            if (cl.getTitulo() != null && cl.getTitulo().toLowerCase().contains(textoLower)) {
                resultados.add(cl);
            }
        }

        return resultados;
    }

    private void aplicarFiltros() {
        db.collection("cursos").get().addOnSuccessListener(snapshot -> {
            List<Object> resultados = new ArrayList<>();

            for (DocumentSnapshot doc : snapshot) {
                Curso curso = doc.toObject(Curso.class);
                if (curso == null) continue;

                boolean cumple = true;

                if (filtroGenero != null && !tieneClave(doc, "genero", filtroGenero)) cumple = false;
                if (filtroInstrumento != null && !tieneClave(doc, "instrumento", filtroInstrumento)) cumple = false;
                if (filtroDificultad != null && !tieneClave(doc, "dificultad", filtroDificultad)) cumple = false;

                if (cumple) {
                    curso.setFirestoreId(doc.getId());
                    resultados.add(curso);
                }
            }

            if (resultados.isEmpty()) {
                Toast.makeText(this, "No se encontraron cursos con los filtros seleccionados", Toast.LENGTH_SHORT).show();
            }

            rv_resultados.setAdapter(new AdapterBusquedaGeneral(resultados, new AdapterBusquedaGeneral.OnItemClickListener() {
                @Override
                public void onCursoClick(Curso curso) {
                    startActivity(new Intent(Busqueda.this, Ver_cursos.class).putExtra("idCurso", curso.getId()));
                }

                @Override public void onUsuarioClick(Usuario usuario) {}
                @Override public void onClaseClick(Clase clase) {}
            }));

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al buscar cursos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Busqueda", "Error al aplicar filtros", e);
        });
    }

    private boolean tieneClave(DocumentSnapshot doc, String campo, String clave) {
        Map<String, Object> mapa = (Map<String, Object>) doc.get(campo);
        return mapa != null && mapa.containsKey(clave);
    }
}
