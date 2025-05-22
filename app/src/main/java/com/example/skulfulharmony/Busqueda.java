package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Set;

public class Busqueda extends AppCompatActivity {

    private EditText et_buscar;
    private RecyclerView rv_resultados;
    private List<Curso> cursos = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Clase> clases = new ArrayList<>();

    private Button btnGenero, btnInstrumento, btnDificultad;

    private final String[] instrumentos = {"Guitarra", "Bajo", "Flauta", "Trompeta", "Batería", "Piano", "Ukelele", "Violin", "Canto", "Otro"};
    private final String[] niveles = {"Principiante", "Intermedio", "Avanzado"};
    private final String[] generos = {"Pop", "Rock", "Hiphop/Rap", "Electronica", "Jazz", "Blues", "Reggaeton", "Reggae", "Clasica", "Country", "Metal", "Folk", "Independiente"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_busqueda);

        et_buscar = findViewById(R.id.et_parabuscar);
        rv_resultados = findViewById(R.id.rv_resultadosbusqueda);
        rv_resultados.setLayoutManager(new LinearLayoutManager(this));

        btnGenero = findViewById(R.id.btn_bgenero);
        btnInstrumento = findViewById(R.id.btn_binstumento);
        btnDificultad = findViewById(R.id.btn_bdificultad);

        btnGenero.setOnClickListener(v -> mostrarDialogoSeleccion("Selecciona un género", generos, opcion -> {
            Toast.makeText(this, "Género seleccionado: " + opcion, Toast.LENGTH_SHORT).show();
            // Aquí puedes guardar o aplicar filtro con opción
        }));

        btnInstrumento.setOnClickListener(v -> mostrarDialogoSeleccion("Selecciona un instrumento", instrumentos, opcion -> {
            Toast.makeText(this, "Instrumento seleccionado: " + opcion, Toast.LENGTH_SHORT).show();
            // Aquí puedes guardar o aplicar filtro con opción
        }));

        btnDificultad.setOnClickListener(v -> mostrarDialogoSeleccion("Selecciona dificultad", niveles, opcion -> {
            Toast.makeText(this, "Dificultad seleccionada: " + opcion, Toast.LENGTH_SHORT).show();
            // Aquí puedes guardar o aplicar filtro con opción
        }));

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

    private void mostrarDialogoSeleccion(String titulo, String[] opciones, OnOpcionSeleccionadaListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(opciones, (dialog, which) -> {
                    String seleccion = opciones[which];
                    listener.onSeleccion(seleccion);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    interface OnOpcionSeleccionadaListener {
        void onSeleccion(String opcion);
    }

    private void buscarEnFirebase(final String textoBusqueda) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cursos").get().addOnSuccessListener(queryCursos -> {
            cursos.clear();
            for (DocumentSnapshot doc : queryCursos) {
                Curso curso = doc.toObject(Curso.class);
                if (curso != null) cursos.add(curso);
            }

            db.collection("usuarios").get().addOnSuccessListener(queryUsuarios -> {
                usuarios.clear();
                for (DocumentSnapshot doc : queryUsuarios) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    if (usuario != null) {
                        usuario.setId(doc.getId());
                        usuarios.add(usuario);
                    }
                }

                db.collection("clases").get().addOnSuccessListener(queryClases -> {
                    clases.clear();
                    for (DocumentSnapshot doc : queryClases) {
                        try {
                            Clase clase = doc.toObject(Clase.class);

                            // Manejo especial para "preguntas"
                            Object preguntasObj = doc.get("preguntas");
                            if (preguntasObj instanceof List) {
                                List<?> preguntasRaw = (List<?>) preguntasObj;
                                // Puedes procesar preguntasRaw si tienes setter
                            } else if (preguntasObj instanceof String) {
                                // Opcional procesar JSON string si quieres
                            }

                            if (clase != null) {
                                clases.add(clase);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    List<Object> resultadosCombinados = buscarConIndiceInvertidoMejorado(textoBusqueda, cursos, usuarios, clases);

                    if (resultadosCombinados.isEmpty()) {
                        Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                    } else {
                        AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultadosCombinados, new AdapterBusquedaGeneral.OnItemClickListener() {
                            @Override
                            public void onCursoClick(Curso curso) {
                                // TODO: manejar click en curso
                            }

                            @Override
                            public void onUsuarioClick(Usuario usuario) {
                                Intent intent = new Intent(Busqueda.this, PerfilMostrar.class);
                                intent.putExtra("usuarioId", usuario.getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onClaseClick(Clase clase) {
                                // TODO: manejar click en clase
                            }
                        });
                        rv_resultados.setAdapter(adapter);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar clases", Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al buscar cursos", Toast.LENGTH_SHORT).show();
        });
    }

    private List<Object> buscarConIndiceInvertidoMejorado(String textoBusqueda, List<Curso> cursos, List<Usuario> usuarios, List<Clase> clases) {
        Set<String> cursosEncontrados = new HashSet<>();
        Set<String> usuariosEncontrados = new HashSet<>();
        Set<String> clasesEncontradas = new HashSet<>();

        String textoLower = textoBusqueda.toLowerCase();

        for (Curso c : cursos) {
            String titulo = c.getTitulo();
            if (titulo != null && titulo.toLowerCase().contains(textoLower)) {
                cursosEncontrados.add(String.valueOf(c.getId()));
            }
        }

        for (Usuario u : usuarios) {
            String nombre = u.getNombre();
            if (nombre != null && nombre.toLowerCase().contains(textoLower)) {
                usuariosEncontrados.add(nombre.toLowerCase());
            }
        }

        for (Clase cl : clases) {
            String titulo = cl.getTitulo();
            if (titulo != null && titulo.toLowerCase().contains(textoLower)) {
                clasesEncontradas.add(titulo.toLowerCase());
            }
        }

        List<Object> resultados = new ArrayList<>();

        for (Curso c : cursos) {
            if (cursosEncontrados.contains(String.valueOf(c.getId()))) {
                resultados.add(c);
            }
        }

        for (Usuario u : usuarios) {
            String nombre = u.getNombre();
            if (nombre != null && usuariosEncontrados.contains(nombre.toLowerCase())) {
                resultados.add(u);
            }
        }

        for (Clase cl : clases) {
            String titulo = cl.getTitulo();
            if (titulo != null && clasesEncontradas.contains(titulo.toLowerCase())) {
                resultados.add(cl);
            }
        }

        return resultados;
    }
}