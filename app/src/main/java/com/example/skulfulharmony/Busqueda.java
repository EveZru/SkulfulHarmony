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
        db.collection("cursos").get().addOnSuccessListener(queryCursos -> {
            cursos.clear();
            for (DocumentSnapshot doc : queryCursos) {
                try {
                    Curso curso = new Curso();
                    curso.setFirestoreId(doc.getId());
                    curso.setIdCurso(doc.getLong("idCurso") != null ? doc.getLong("idCurso").intValue() : null);
                    curso.setImagen(doc.getString("imagen"));
                    curso.setTitulo(doc.getString("titulo"));
                    //curso.setDescripcion(doc.getString("descripcion"));

                    String correoCreador = null;
                    Object creadorRaw = doc.get("creador");
                    if (creadorRaw instanceof String) {
                        correoCreador = (String) creadorRaw;
                    } else if (creadorRaw instanceof Map) {
                        Map<String, Object> creadorMap = (Map<String, Object>) creadorRaw;
                        if (creadorMap.containsKey("email")) {
                            correoCreador = (String) creadorMap.get("email");
                        } else if (creadorMap.containsKey("uid")) {
                            curso.setCreador((String) creadorMap.get("uid"));
                        }
                    }

                    if (correoCreador != null) {
                        String finalCorreoCreador = correoCreador;
                        db.collection("usuarios")
                                .whereEqualTo("correo", correoCreador)
                                .get()
                                .addOnSuccessListener(userQuery -> {
                                    if (!userQuery.isEmpty()) {
                                        String nombre = userQuery.getDocuments().get(0).getString("nombre");
                                        curso.setCreador(nombre != null ? nombre : finalCorreoCreador);
                                    } else {
                                        curso.setCreador(finalCorreoCreador);
                                    }
                                    cursos.add(curso);
                                    Log.d("Busqueda", "✅ Curso leído: " + curso.getTitulo());
                                })
                                .addOnFailureListener(e -> {
                                    curso.setCreador(finalCorreoCreador);
                                    cursos.add(curso);
                                    Log.w("Busqueda", "⚠️ No se pudo obtener nombre de creador: " + e.getMessage());
                                });
                    } else {
                        curso.setCreador("Creador Desconocido");
                        cursos.add(curso);
                        Log.d("Busqueda", "⚠️ Curso sin correo de creador definido");
                    }

                    if (doc.get("instrumento") instanceof Map)
                        curso.setInstrumento((Map<String, Integer>) doc.get("instrumento"));
                    if (doc.get("genero") instanceof Map)
                        curso.setGenero((Map<String, Integer>) doc.get("genero"));
                    if (doc.get("dificultad") instanceof Map)
                        curso.setDificultad((Map<String, Integer>) doc.get("dificultad"));

                } catch (Exception e) {
                    Log.e("Busqueda", "❌ Error leyendo curso: " + e.getMessage());
                }
            }

            db.collection("usuarios").get().addOnSuccessListener(queryUsuarios -> {
                usuarios.clear();
                for (DocumentSnapshot doc : queryUsuarios) {
                    Usuario usuario = doc.toObject(Usuario.class);
                    if (usuario != null && !"admin".equalsIgnoreCase(usuario.getRol())) {
                        usuario.setId(doc.getId());
                        usuarios.add(usuario);
                        Log.d("Busqueda", "✅ Usuario leído: " + usuario.getNombre());
                    }
                }

                db.collection("clases").get().addOnSuccessListener(queryClases -> {
                    clases.clear();
                    for (DocumentSnapshot doc : queryClases) {
                        try {
                            Clase clase = new Clase();

                            Object idClaseRaw = doc.get("idClase");
                            if (idClaseRaw instanceof Long) {
                                clase.setIdClase(((Long) idClaseRaw).intValue());
                            } else if (idClaseRaw instanceof Integer) {
                                clase.setIdClase((Integer) idClaseRaw);
                            } else {
                                Log.w("Busqueda", "⚠️ Clase sin campo válido 'idClase': " + doc.getId());
                                continue;
                            }

                            String tituloClase = null;
                            if (doc.contains("titulo")) {
                                tituloClase = doc.getString("titulo");
                            }
                            if ((tituloClase == null || tituloClase.trim().isEmpty()) && doc.contains("Titulo")) {
                                tituloClase = doc.getString("Titulo");
                            }
                            if (tituloClase == null) {
                                Log.w("Busqueda", "⚠️ Clase sin campo 'titulo' ni 'Titulo': " + doc.getId());
                                tituloClase = "";
                            }
                            clase.setTitulo(tituloClase);

                            clase.setNombreCurso(doc.getString("nombreCurso"));

                            Object idCursoRaw = doc.get("idCurso");
                            if (idCursoRaw instanceof Long) {
                                clase.setIdCurso(((Long) idCursoRaw).intValue());
                            } else if (idCursoRaw instanceof Integer) {
                                clase.setIdCurso((Integer) idCursoRaw);
                            }

                            clases.add(clase);
                            Log.d("Busqueda", "✅ Clase leída: " + clase.getTitulo());
                        } catch (Exception e) {
                            Log.e("Busqueda", "❌ Error al leer clase: " + e.getMessage());
                        }
                    }

                    List<Object> resultadosCombinados = buscarConIndiceInvertidoMejorado(textoBusqueda, cursos, usuarios, clases);

                    if (resultadosCombinados.isEmpty()) {
                        Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("DEBUG", "🔍 Resultados combinados: " + resultadosCombinados.size());
                        AdapterBusquedaGeneral adapter = new AdapterBusquedaGeneral(resultadosCombinados, new AdapterBusquedaGeneral.OnItemClickListener() {
                            @Override
                            public void onCursoClick(Curso curso) {
                                Intent intent = new Intent(Busqueda.this, Ver_cursos.class);
                                intent.putExtra("idCurso", curso.getIdCurso() != null ? curso.getIdCurso() : -1);
                                startActivity(intent);
                            }

                            @Override
                            public void onUsuarioClick(Usuario usuario) {
                                Intent intent = new Intent(Busqueda.this, PerfilMostrar.class);
                                intent.putExtra("usuarioId", usuario.getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onClaseClick(Clase clase) {
                                Intent intent = new Intent(Busqueda.this, Ver_clases.class);
                                intent.putExtra("idClase", clase.getIdClase() != null ? clase.getIdClase() : -1);
                                intent.putExtra("idCurso", clase.getIdCurso() != null ? clase.getIdCurso() : -1);
                                startActivity(intent);
                            }
                        });
                        rv_resultados.setAdapter(adapter);
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, "Error al buscar clases", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al buscar cursos", Toast.LENGTH_SHORT).show());
    }

    private List<Object> buscarConIndiceInvertidoMejorado(String textoBusqueda, List<Curso> cursos, List<Usuario> usuarios, List<Clase> clases) {
        Set<Integer> cursosEncontrados = new HashSet<>();
        Set<String> usuariosEncontrados = new HashSet<>();
        Set<Integer> clasesEncontradas = new HashSet<>();

        String textoLower = textoBusqueda.toLowerCase();
        Log.d("BUSQUEDA", "🔍 Buscando: " + textoLower);

        for (Curso c : cursos) {
            String titulo = c.getTitulo() != null ? c.getTitulo().toLowerCase() : "";

            boolean match = titulo.contains(textoLower);
            if (match && c.getIdCurso() != null) {
                cursosEncontrados.add(c.getIdCurso());
                Log.d("BUSQUEDA", "✅ Curso coincide: " + titulo);
            } else {
                Log.d("BUSQUEDA", "❌ Curso no coincide: " + titulo);
            }
        }

        for (Usuario u : usuarios) {
            String nombre = u.getNombre() != null ? u.getNombre().toLowerCase() : "";

            boolean match = nombre.contains(textoLower);
            if (match && u.getId() != null) {
                usuariosEncontrados.add(u.getId());
                Log.d("BUSQUEDA", "✅ Usuario coincide: " + nombre);
            } else {
                Log.d("BUSQUEDA", "❌ Usuario no coincide: " + nombre);
            }
        }

        for (Clase cl : clases) {
            String titulo = cl.getTitulo() != null ? cl.getTitulo().toLowerCase() : "";
            String nombreCurso = cl.getNombreCurso() != null ? cl.getNombreCurso().toLowerCase() : "";

            boolean match = titulo.contains(textoLower) || nombreCurso.contains(textoLower);

            Log.d("BUSQUEDA", "🎬 Revisando clase: \"" + titulo + "\" / curso: \"" + nombreCurso + "\" → match: " + match);

            if (match && cl.getIdClase() != null) {
                clasesEncontradas.add(cl.getIdClase());
                Log.d("BUSQUEDA", "✅ Clase coincide: " + titulo);
            } else {
                Log.d("BUSQUEDA", "❌ Clase no coincide: " + textoLower + " no está en \"" + titulo + "\" ni en \"" + nombreCurso + "\"");
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
                try {
                    Curso curso = doc.toObject(Curso.class);
                    if (curso == null) continue;
                    curso.setFirestoreId(doc.getId());

                    boolean cumple = true;

                    if (filtroGenero != null) {
                        Map<String, Integer> genero = curso.getGenero();
                        cumple &= genero != null && genero.containsKey(filtroGenero);
                    }

                    if (filtroInstrumento != null) {
                        Map<String, Integer> instrumento = curso.getInstrumento();
                        cumple &= instrumento != null && instrumento.containsKey(filtroInstrumento);
                    }

                    if (filtroDificultad != null) {
                        Map<String, Integer> dificultad = curso.getDificultad();
                        cumple &= dificultad != null && dificultad.containsKey(filtroDificultad);
                    }

                    if (cumple) {
                        resultados.add(curso);
                    }

                } catch (Exception e) {
                    Log.e("FiltroCursos", "❌ Error leyendo curso con filtros: " + e.getMessage());
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

}