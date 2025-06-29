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
                    curso.setDescripcion(doc.getString("descripcion"));
                    curso.setVisitas(doc.getLong("visitas") != null ? doc.getLong("visitas").intValue() : null);
                    curso.setCantidadDescargas(doc.getLong("cantidadDescargas") != null ? doc.getLong("cantidadDescargas").intValue() : null);
                    curso.setPromedioCalificacion(doc.getDouble("promedioCalificacion"));
                    curso.setPopularidad(doc.getDouble("popularidad"));
                    curso.setCluster(doc.getLong("cluster") != null ? doc.getLong("cluster").intValue() : null);
                    curso.setStrike1(doc.getBoolean("strike1"));
                    curso.setStrike2(doc.getBoolean("strike2"));
                    curso.setStrike3(doc.getBoolean("strike3"));
                    curso.setFechaCreacionf(doc.getTimestamp("fechaCreacionf"));
                    curso.setFechaActualizacion(doc.getTimestamp("fechaActualizacion"));
                    curso.setFechaAcceso(doc.getTimestamp("fechaAcceso"));

                    // Creador con validación
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
                            curso.setCreador("Creador Desconocido");
                        }
                    }

                    // Mapas
                    if (doc.get("instrumento") instanceof Map)
                        curso.setInstrumento((Map<String, Integer>) doc.get("instrumento"));
                    if (doc.get("genero") instanceof Map)
                        curso.setGenero((Map<String, Integer>) doc.get("genero"));
                    if (doc.get("dificultad") instanceof Map)
                        curso.setDificultad((Map<String, Integer>) doc.get("dificultad"));
                    if (doc.get("calificacionesPorUsuario") instanceof Map)
                        curso.setCalificacionesPorUsuario((Map<String, Integer>) doc.get("calificacionesPorUsuario"));

                    cursos.add(curso);
                    Log.d("Busqueda", "✅ Curso leído: " + curso.getTitulo());
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

                            clase.setTitulo(doc.contains("titulo") ? doc.getString("titulo") : doc.getString("Titulo"));
                            clase.setNombreCurso(doc.getString("nombreCurso"));

                            // ID de clase desde doc.getId()
                            try {
                                clase.setIdClase(Integer.parseInt(doc.getId()));
                            } catch (NumberFormatException e) {
                                Log.w("Busqueda", "⚠️ ID clase no convertible: " + doc.getId());
                                continue;
                            }

                            // idCurso con validación
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

        // Buscar en cursos
        for (Curso c : cursos) {
            String titulo = c.getTitulo() != null ? c.getTitulo().toLowerCase() : "";
            String descripcion = c.getDescripcion() != null ? c.getDescripcion().toLowerCase() : "";

            Log.d("BUSQUEDA", "🔎 Curso revisado -> titulo: '" + titulo + "', descripcion: '" + descripcion + "', idCurso: " + c.getIdCurso());

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

            Log.d("BUSQUEDA", "🔎 Usuario revisado -> nombre: '" + nombre + "', correo: '" + correo + "', id: " + u.getId());

            if ((nombre.contains(textoLower) || correo.contains(textoLower)) && u.getId() != null) {
                usuariosEncontrados.add(u.getId());
                Log.d("BUSQUEDA", "✅ Usuario coincide: " + nombre);
            }
        }

        // Buscar en clases
        for (Clase cl : clases) {
            String titulo = cl.getTitulo() != null ? cl.getTitulo().toLowerCase() : "";
            String nombreCurso = cl.getNombreCurso() != null ? cl.getNombreCurso().toLowerCase() : "";

            Log.d("BUSQUEDA", "🔎 Clase revisada -> titulo: '" + titulo + "', nombreCurso: '" + nombreCurso + "', idClase: " + cl.getIdClase());

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