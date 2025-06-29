package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView iv_buscar;
    private RecyclerView rv_resultados;
    private Button btnGenero, btnInstrumento, btnDificultad;

    private final List<Curso> cursos = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Clase> clases = new ArrayList<>();

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
        iv_buscar = findViewById(R.id.iv_buscar);

        iv_buscar.setOnClickListener(v -> {
            String query = et_buscar.getText().toString().trim();
            if (!query.isEmpty()) {
                buscarEnFirebase(query);
            }
        });

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
                .setItems(opciones, (dialog, which) -> listener.onSeleccion(opciones[which]))
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
                try {
                    Curso curso = doc.toObject(Curso.class);
                    if (curso == null) continue;

                    // 🔥 Aseguramos ID del curso desde Firestore
                    try {
                        curso.setIdCurso(Integer.parseInt(doc.getId()));
                    } catch (NumberFormatException e) {
                        Log.e("Busqueda", "ID inválido (no numérico) para curso: " + doc.getId());
                        continue; // Ignora el curso si el ID no es un número
                    }

                    // 🛡️ Manejo seguro del campo 'creador'
                    Object creadorRaw = doc.get("creador");
                    if (creadorRaw instanceof String) {
                        curso.setCreador((String) creadorRaw);
                    } else if (creadorRaw instanceof Map) {
                        Map<String, Object> creadorMap = (Map<String, Object>) creadorRaw;
                        if (creadorMap.containsKey("email")) {
                            curso.setCreador((String) creadorMap.get("email"));
                        } else if (creadorMap.containsKey("uid")) {
                            curso.setCreador((String) creadorMap.get("uid"));
                        } else {
                            curso.setCreador("Creador desconocido");
                        }
                    } else {
                        curso.setCreador("Creador inválido");
                    }

                    cursos.add(curso);
                } catch (Exception e) {
                    Log.e("Busqueda", "❌ Error al procesar curso: " + e.getMessage(), e);
                }
            }

            db.collection("usuarios").get().addOnSuccessListener(queryUsuarios -> {
                usuarios.clear();
                for (DocumentSnapshot doc : queryUsuarios) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    if (usuario != null && !"admin".equalsIgnoreCase(usuario.getRol())) {
                        usuario.setId(doc.getId());
                        usuarios.add(usuario);
                    }
                }

                db.collection("clases").get().addOnSuccessListener(queryClases -> {
                    clases.clear();
                    for (DocumentSnapshot doc : queryClases) {
                        try {
                            Clase clase = doc.toObject(Clase.class);

                            if (clase != null) {
                                try {
                                    clase.setIdClase(Integer.parseInt(doc.getId()));
                                } catch (NumberFormatException e) {
                                    Log.e("Busqueda", "ID inválido para clase: " + doc.getId());
                                    continue;
                                }
                                clases.add(clase);
                            }
                        } catch (Exception e) {
                            Log.e("Busqueda", "❌ Error al procesar clase: " + e.getMessage(), e);
                        }
                    }

                    Log.d("DEBUG", "📚 Cursos cargados: " + cursos.size());
                    Log.d("DEBUG", "👤 Usuarios cargados: " + usuarios.size());
                    Log.d("DEBUG", "🎬 Clases cargadas: " + clases.size());

                    List<Object> resultadosCombinados = buscarConIndiceInvertidoMejorado(textoBusqueda, cursos, usuarios, clases);

                    if (resultadosCombinados.isEmpty()) {
                        Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("DEBUG", "🔍 Resultados combinados: " + resultadosCombinados.size());

                        AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultadosCombinados, new AdapterBusquedaGeneral.OnItemClickListener() {
                            @Override
                            public void onCursoClick(Curso curso) {
                                if (curso.getIdCurso() != null) {
                                    Intent intent = new Intent(Busqueda.this, Ver_cursos.class);
                                    intent.putExtra("idCurso", curso.getIdCurso());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onUsuarioClick(Usuario usuario) {
                                if (usuario.getId() != null) {
                                    Intent intent = new Intent(Busqueda.this, PerfilMostrar.class);
                                    intent.putExtra("usuarioId", usuario.getId());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onClaseClick(Clase clase) {
                                if (clase.getIdClase() != null && clase.getIdCurso() != null) {
                                    Intent intent = new Intent(Busqueda.this, Ver_clases.class);
                                    intent.putExtra("idClase", clase.getIdClase());
                                    intent.putExtra("idCurso", clase.getIdCurso());
                                    startActivity(intent);
                                }
                            }
                        });

                        rv_resultados.setAdapter(adapter);
                    }

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar clases", Toast.LENGTH_SHORT).show();
                    Log.e("Busqueda", "❌ Error al obtener clases", e);
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
                Log.e("Busqueda", "❌ Error al obtener usuarios", e);
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al buscar cursos", Toast.LENGTH_SHORT).show();
            Log.e("Busqueda", "❌ Error al obtener cursos", e);
        });
    }


    private List<Object> buscarConIndiceInvertidoMejorado(String textoBusqueda, List<Curso> cursos, List<Usuario> usuarios, List<Clase> clases) {
        Set<Integer> cursosEncontrados = new HashSet<>();
        Set<String> usuariosEncontrados = new HashSet<>();
        Set<Integer> clasesEncontradas = new HashSet<>();

        String textoLower = textoBusqueda.toLowerCase();
        Log.d("BUSQUEDA", "🔍 Buscando: " + textoLower);

        // Buscar en cursos
        for (Curso c : cursos) {
            String titulo = c.getTitulo() != null ? c.getTitulo().toLowerCase() : "";
            String descripcion = c.getDescripcion() != null ? c.getDescripcion().toLowerCase() : "";

            boolean match = titulo.contains(textoLower) || descripcion.contains(textoLower);
            if (match && c.getIdCurso() != null) {
                cursosEncontrados.add(c.getIdCurso());
                Log.d("BUSQUEDA", "✅ Curso coincide: " + titulo);
            }
        }

        // Buscar en usuarios
        for (Usuario u : usuarios) {
            String nombre = u.getNombre() != null ? u.getNombre().toLowerCase() : "";
            String correo = u.getCorreo() != null ? u.getCorreo().toLowerCase() : "";

            if ((nombre.contains(textoLower) || correo.contains(textoLower)) && u.getId() != null) {
                usuariosEncontrados.add(u.getId());
                Log.d("BUSQUEDA", "✅ Usuario coincide: " + nombre);
            }
        }

        // Buscar en clases
        for (Clase cl : clases) {
            String titulo = cl.getTitulo() != null ? cl.getTitulo().toLowerCase() : "";
            String nombreCurso = cl.getNombreCurso() != null ? cl.getNombreCurso().toLowerCase() : "";

            if ((titulo.contains(textoLower) || nombreCurso.contains(textoLower)) && cl.getIdClase() != null) {
                clasesEncontradas.add(cl.getIdClase());
                Log.d("BUSQUEDA", "✅ Clase coincide: " + titulo);
            }
        }

        List<Object> resultados = new ArrayList<>();

        for (Curso c : cursos) {
            if (c.getIdCurso() != null && cursosEncontrados.contains(c.getIdCurso())) {
                resultados.add(c);
            }
        }

        for (Usuario u : usuarios) {
            if (u.getId() != null && usuariosEncontrados.contains(u.getId())) {
                resultados.add(u);
            }
        }

        for (Clase cl : clases) {
            if (cl.getIdClase() != null && clasesEncontradas.contains(cl.getIdClase())) {
                resultados.add(cl);
            }
        }

        Log.d("BUSQUEDA", "📦 Total resultados encontrados: " + resultados.size());
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
        Object valor = doc.get(campo);
        if (valor instanceof Map) {
            Map<String, Object> mapa = (Map<String, Object>) valor;
            for (String k : mapa.keySet()) {
                if (k.equalsIgnoreCase(clave)) return true;
            }
        } else if (valor instanceof List) {
            List<?> lista = (List<?>) valor;
            for (Object obj : lista) {
                if (obj != null && obj.toString().equalsIgnoreCase(clave)) return true;
            }
        } else if (valor instanceof String) {
            return ((String) valor).equalsIgnoreCase(clave);
        }
        return false;
    }


}