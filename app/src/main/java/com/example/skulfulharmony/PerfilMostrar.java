package com.example.skulfulharmony;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.adapters.AdapterCursosUsuario;
import com.example.skulfulharmony.adapters.AdapterHomeVerCursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilMostrar extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_correo, tv_DescripcionUsuario, tv_No_Cursos, tv_Seguidores, tv_Seguido;
    private LineChart chartPracticaSemanal;
    private BarChart chartProgresoCursos, chartNivelInstrumentos;
    private TextView tvComparacionSemana;
    private LinearLayout layout_nivel_instrumentos;

    ImageView ivProfilePic;
    TextView tvNombreUsuario, tvCorreo, tvDescripcion, tvNoCursos, tvSeguidores, tvSeguidos;
    Button btnSeguirUsuario;
    RecyclerView rvCursosUsuario;
    Usuario usuarioPerfil;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    boolean siguiendo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mostrar);

        db = FirebaseFirestore.getInstance();

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        tv_DescripcionUsuario = findViewById(R.id.tv_DescripcionUsuario);
        tv_correo = findViewById(R.id.tv_correo);
        tv_Seguidores = findViewById(R.id.tv_Seguidores);
        tv_Seguido = findViewById(R.id.tv_Seguido);

        ivProfilePic = ivProfilePicture;
        tvNombreUsuario = tv_NombreUsuario;
        tvCorreo = tv_correo;
        tvDescripcion = tv_DescripcionUsuario;
        tvNoCursos = tv_No_Cursos;
        tvSeguidores = tv_Seguidores;
        tvSeguidos = tv_Seguido;
        btnSeguirUsuario = findViewById(R.id.btnSeguirUsuario);
        rvCursosUsuario = findViewById(R.id.rvCursosUsuario);

        chartPracticaSemanal = findViewById(R.id.chartPracticaSemanal);
        chartProgresoCursos = findViewById(R.id.chartProgresoCursos);
        chartNivelInstrumentos = findViewById(R.id.chartNivelInstrumentos);
        tvComparacionSemana = findViewById(R.id.tvComparacionSemana);
        layout_nivel_instrumentos = findViewById(R.id.layout_nivel_instrumentos);

        usuarioPerfil = (Usuario) getIntent().getSerializableExtra("usuario");
        String usuarioId = getIntent().getStringExtra("usuarioId");

        if (usuarioPerfil != null) {
            mostrarDatosUsuarioCompleto(usuarioPerfil);
        } else if (usuarioId != null) {
            cargarDatosUsuario(usuarioId);
        } else {
            Toast.makeText(this, "Error: usuario no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (usuarioPerfil == null && usuarioId != null) {
            usuarioPerfil = new Usuario();
            usuarioPerfil.setCorreo(usuarioId);
        }


        if (usuarioPerfil != null && usuarioPerfil.getCorreo() != null) {
            rvCursosUsuario.setLayoutManager(new LinearLayoutManager(this));
            cargarCursosUsuario(usuarioPerfil.getCorreo());

            String correoActual = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
            if (correoActual != null) {

                verificarSiSigue(correoActual, usuarioPerfil.getCorreo());

                btnSeguirUsuario.setOnClickListener(v -> {
                    if (!siguiendo) {
                        seguirUsuarioPorNombre(correoActual, usuarioPerfil.getNombre());
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Dejar de seguir")
                                .setMessage("¿Estás seguro que quieres dejar de seguir a " + usuarioPerfil.getNombre() + "?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    dejarDeSeguir(correoActual, usuarioPerfil.getCorreo());
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });
            }
        } else {
            btnSeguirUsuario.setVisibility(Button.GONE);
            rvCursosUsuario.setVisibility(RecyclerView.GONE);
        }

    }

    private void mostrarDatosUsuarioCompleto(Usuario usuario) {
        Glide.with(this)
                .load(usuario.getFotoPerfil())
                .placeholder(R.drawable.default_profile)
                .into(ivProfilePic);

        tvNombreUsuario.setText(usuario.getNombre() != null ? usuario.getNombre() : "Sin Nombre");
        tvCorreo.setText(usuario.getCorreo() != null ? usuario.getCorreo() : "Sin correo");
        tvDescripcion.setText(usuario.getDescripcion() != null ? usuario.getDescripcion() : "Sin descripción");
        tvNoCursos.setText("Cursos Creados: " + usuario.getCursos());
        tvSeguidores.setText("Seguidores: " + usuario.getSeguidores());
        tvSeguidos.setText("Seguidos: " + usuario.getSeguidos());
    }

    private void cargarDatosUsuario(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String nombre = documentSnapshot.getString("nombre");
                String correo = documentSnapshot.getString("correo");
                String descripcion = documentSnapshot.getString("descripcion");
                Long cursos = documentSnapshot.getLong("cursos");
                Long seguidores = documentSnapshot.getLong("seguidores");
                Long seguidos = documentSnapshot.getLong("seguidos");
                String fotoUrl = documentSnapshot.getString("fotoPerfil");

                tvNombreUsuario.setText(nombre);
                tvCorreo.setText(correo);
                tvDescripcion.setText(descripcion);
                tvNoCursos.setText("Cursos Creados: " + (cursos != null ? cursos : 0));
                tvSeguidores.setText("Seguidores: " + (seguidores != null ? seguidores : 0));
                tvSeguidos.setText("Seguidos: " + (seguidos != null ? seguidos : 0));

                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this).load(fotoUrl).into(ivProfilePic);
                }

                usuarioPerfil = new Usuario();
                usuarioPerfil.setNombre(nombre);
                usuarioPerfil.setCorreo(correo);
                usuarioPerfil.setDescripcion(descripcion);
                usuarioPerfil.setCursos(cursos != null ? cursos.intValue() : 0);
                usuarioPerfil.setSeguidores(seguidores != null ? seguidores.intValue() : 0);
                usuarioPerfil.setSeguidos(seguidos != null ? seguidos.intValue() : 0);
                usuarioPerfil.setFotoPerfil(fotoUrl);

                rvCursosUsuario.setLayoutManager(
                        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                );
                cargarCursosUsuario(correo);

                String correoActual = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
                if (correoActual != null) {
                    verificarSiSigue(correoActual, correo);
                }

                btnSeguirUsuario.setOnClickListener(v -> {
                    if (!siguiendo) {
                        seguirUsuarioPorNombre(correoActual, usuarioPerfil.getNombre());
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Dejar de seguir")
                                .setMessage("¿Estás seguro que quieres dejar de seguir a " + usuarioPerfil.getNombre() + "?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    dejarDeSeguir(correoActual, usuarioPerfil.getCorreo());
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

                Map<String, Object> datos = documentSnapshot.getData();
                if (datos != null) {
                    Map<String, Integer> tiemposPorDia = new HashMap<>();
                    for (String key : datos.keySet()) {
                        if (key.startsWith("tiempo_")) {
                            Object val = datos.get(key);
                            if (val instanceof Number) {
                                tiemposPorDia.put(key.substring(7), ((Number) val).intValue());
                            }
                        }
                    }

                    Map<Integer, Integer> tiemposPorSemana = agruparPorSemana(tiemposPorDia);
                    cargarGraficaPractica(tiemposPorSemana);
                }

                Object rawProgreso = documentSnapshot.get("progresoCursos");
                if (rawProgreso instanceof Map) {
                    Map<String, List<Long>> progresoCursos = new HashMap<>();
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) rawProgreso).entrySet()) {
                        String cursoId = entry.getKey();
                        List<Long> clases = (List<Long>) entry.getValue();
                        progresoCursos.put(cursoId, clases);
                    }
                    cargarGraficaProgresoCursos(progresoCursos);
                }

                calcularNivelInstrumentos(db, documentSnapshot.getId(), resultado -> {
                    cargarGraficaNivelInstrumentos(resultado);
                });

            } else {
                Toast.makeText(this, "No se encontró el perfil", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
        });
    }


    private void cargarCursosUsuario(String correoUsuario) {
        Log.d("PerfilMostrar", "Buscando cursos creados por: " + correoUsuario);
        db.collection("cursos")
                .whereEqualTo("creador", correoUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Curso> cursos = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Curso curso = doc.toObject(Curso.class);
                        cursos.add(curso);
                    }
                    Log.d("PerfilMostrar", "Cursos encontrados: " + cursos.size());

                    AdapterHomeVerCursos adapter = new AdapterHomeVerCursos(cursos, this);
                    rvCursosUsuario.setAdapter(adapter);
                    rvCursosUsuario.setVisibility(View.VISIBLE);
                    rvCursosUsuario.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Si quieres horizontal
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar cursos", Toast.LENGTH_SHORT).show();
                    Log.e("PerfilMostrar", "Error al cargar cursos", e);
                });
    }


    private void verificarSiSigue(String correoActual, String correoPerfilBuscado) {
        db.collection("usuarios")
                .whereEqualTo("correo", correoActual)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot docUsuario = querySnapshot.getDocuments().get(0);


                        if (docUsuario.contains("seguidosname")) {
                            List<Map<String, String>> seguidos = (List<Map<String, String>>) docUsuario.get("seguidosname");
                            siguiendo = false;

                            if (seguidos != null) {
                                for (Map<String, String> seguido : seguidos) {
                                    if (correoPerfilBuscado.equals(seguido.get("correo"))) {
                                        siguiendo = true;
                                        break;
                                    }
                                }
                            }
                        }

                        else {
                            docUsuario.getReference()
                                    .collection("seguidosname")
                                    .document(correoPerfilBuscado)
                                    .get()
                                    .addOnSuccessListener(seguidoDoc -> {
                                        siguiendo = seguidoDoc.exists();
                                        actualizarBotonSeguir();
                                    })
                                    .addOnFailureListener(e -> {
                                        siguiendo = false;
                                        actualizarBotonSeguir();
                                    });
                            return;
                        }

                        actualizarBotonSeguir();
                    } else {
                        siguiendo = false;
                        actualizarBotonSeguir();
                    }
                })
                .addOnFailureListener(e -> {
                    siguiendo = false;
                    actualizarBotonSeguir();
                    Log.e("PerfilMostrar", "Error al verificar seguimiento", e);
                });
    }

    private void actualizarBotonSeguir() {
        if (siguiendo) {
            btnSeguirUsuario.setText("Eliminar seguimiento");
            btnSeguirUsuario.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            btnSeguirUsuario.setText("Seguir");
            btnSeguirUsuario.setBackgroundColor(getResources().getColor(R.color.verde)); // Cambia a tu color rojo
        }
    }

    private void seguirUsuarioPorNombre(String correoActual, String nombrePerfilBuscado) {
        Log.d("PerfilMostrar", "Inicia seguirUsuarioPorNombre: correoActual=" + correoActual + ", nombrePerfilBuscado=" + nombrePerfilBuscado);

        db.collection("usuarios")
                .whereEqualTo("nombre", nombrePerfilBuscado)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot docPerfil = querySnapshot.getDocuments().get(0);
                        String correoPerfil = docPerfil.getString("correo");
                        String nombrePerfil = docPerfil.getString("nombre");

                        db.collection("usuarios")
                                .whereEqualTo("correo", correoActual)
                                .get()
                                .addOnSuccessListener(querySnapshotActual -> {
                                    if (querySnapshotActual.isEmpty()) {
                                        Toast.makeText(this, "Tu usuario no existe en Firestore", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    DocumentSnapshot docActual = querySnapshotActual.getDocuments().get(0);
                                    String idDocActual = docActual.getId();
                                    DocumentReference refActual = db.collection("usuarios").document(idDocActual);
                                    DocumentReference refPerfil = db.collection("usuarios").document(docPerfil.getId());

                                    // Datos para agregar en arrays
                                    Map<String, Object> seguidorData = new HashMap<>();
                                    seguidorData.put("nombre", docActual.getString("nombre") != null ? docActual.getString("nombre") : correoActual);
                                    seguidorData.put("correo", correoActual);

                                    Map<String, Object> seguidoData = new HashMap<>();
                                    seguidoData.put("nombre", nombrePerfil);
                                    seguidoData.put("correo", correoPerfil);

                                    // Actualizar arrays con arrayUnion (agrega sin borrar)
                                    refPerfil.update("seguidoresname", FieldValue.arrayUnion(seguidorData));
                                    refActual.update("seguidosname", FieldValue.arrayUnion(seguidoData));

                                    // Actualizar contadores seguidores y seguidos
                                    WriteBatch batch = db.batch();
                                    batch.update(refPerfil, "seguidores", FieldValue.increment(1));
                                    batch.update(refActual, "seguidos", FieldValue.increment(1));
                                    batch.commit()
                                            .addOnSuccessListener(aVoid -> {
                                                siguiendo = true;
                                                actualizarBotonSeguir();
                                                Toast.makeText(this, "Ahora sigues a " + nombrePerfil, Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al seguir usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                Log.e("PerfilMostrar", "Error seguir usuario", e);
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al verificar documento de usuario actual", Toast.LENGTH_LONG).show();
                                    Log.e("PerfilMostrar", "Error buscando usuario actual por correo", e);
                                });

                    } else {
                        Toast.makeText(this, "No se encontró usuario con ese nombre", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("PerfilMostrar", "Error buscando usuario por nombre", e);
                });
    }

    private void dejarDeSeguir(String correoActual, String correoPerfil) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!user.getEmail().equalsIgnoreCase(correoActual)) {
            Toast.makeText(this, "No tienes permisos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Busca documento perfil a dejar de seguir
        db.collection("usuarios")
                .whereEqualTo("correo", correoPerfil)
                .get()
                .addOnSuccessListener(queryPerfil -> {
                    if (queryPerfil.isEmpty()) {
                        Toast.makeText(this, "No se encontró el perfil a dejar de seguir", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DocumentSnapshot docPerfil = queryPerfil.getDocuments().get(0);
                    String idDocPerfil = docPerfil.getId();
                    String nombrePerfil = docPerfil.getString("nombre");


                    db.collection("usuarios")
                            .whereEqualTo("correo", correoActual)
                            .get()
                            .addOnSuccessListener(queryActual -> {
                                if (queryActual.isEmpty()) {
                                    Toast.makeText(this, "No se encontró tu usuario", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DocumentSnapshot docActual = queryActual.getDocuments().get(0);
                                String idDocActual = docActual.getId();
                                String nombreActual = docActual.getString("nombre");

                                Map<String, Object> seguidorData = new HashMap<>();
                                seguidorData.put("nombre", nombreActual != null ? nombreActual : correoActual);
                                seguidorData.put("correo", correoActual);

                                Map<String, Object> seguidoData = new HashMap<>();
                                seguidoData.put("nombre", nombrePerfil != null ? nombrePerfil : correoPerfil);
                                seguidoData.put("correo", correoPerfil);

                                DocumentReference refPerfil = db.collection("usuarios").document(idDocPerfil);
                                DocumentReference refActual = db.collection("usuarios").document(idDocActual);

                                WriteBatch batch = db.batch();

                                batch.update(refPerfil, "seguidoresname", FieldValue.arrayRemove(seguidorData));
                                batch.update(refActual, "seguidosname", FieldValue.arrayRemove(seguidoData));

                                batch.update(refPerfil, "seguidores", FieldValue.increment(-1));
                                batch.update(refActual, "seguidos", FieldValue.increment(-1));

                                batch.commit().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, "Dejaste de seguir al usuario", Toast.LENGTH_SHORT).show();

                                        // Aquí reiniciamos la actividad para refrescar la vista
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);

                                    } else {
                                        Log.e("FirestoreError", "Error al dejar de seguir: ", task.getException());
                                        Toast.makeText(this, "Error al dejar de seguir", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Error buscando tu usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("FirestoreError", "Error buscando usuario actual", e);
                            });

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error buscando perfil a dejar de seguir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error buscando perfil", e);
                });
    }

    private Map<Integer, Integer> agruparPorSemana(Map<String, Integer> tiemposPorDia) {
        Map<Integer, Integer> tiemposPorSemana = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        for (String fechaStr : tiemposPorDia.keySet()) {
            try {
                Date fecha = sdf.parse(fechaStr);
                cal.setTime(fecha);
                int semana = cal.get(Calendar.WEEK_OF_YEAR);
                int tiempo = tiemposPorDia.get(fechaStr);
                tiemposPorSemana.put(semana, tiemposPorSemana.getOrDefault(semana, 0) + tiempo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return tiemposPorSemana;
    }

    private void cargarGraficaPractica(Map<Integer, Integer> tiemposPorSemana) {
        LineChart chartPracticaSemanal = findViewById(R.id.chartPracticaSemanal);
        List<Entry> entries = new ArrayList<>();
        List<Integer> semanas = new ArrayList<>(tiemposPorSemana.keySet());
        Collections.sort(semanas);
        List<String> etiquetas = new ArrayList<>();

        for (int i = 0; i < semanas.size(); i++) {
            int semana = semanas.get(i);
            entries.add(new Entry(i, tiemposPorSemana.get(semana)));
            etiquetas.add("Semana " + semana);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Práctica semanal");
        dataSet.setColor(Color.CYAN);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        LineData data = new LineData(dataSet);
        chartPracticaSemanal.setData(data);

        XAxis xAxis = chartPracticaSemanal.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        chartPracticaSemanal.getAxisRight().setEnabled(false);
        chartPracticaSemanal.getAxisLeft().setAxisMinimum(0f);

        chartPracticaSemanal.getDescription().setEnabled(false);
        chartPracticaSemanal.animateY(1000);
        chartPracticaSemanal.invalidate();
    }

    private void cargarGraficaProgresoCursos(Map<String, List<Long>> progresoCursos) {
        BarChart chartProgresoCursos = findViewById(R.id.chartProgresoCursos);
        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int[] index = {0};

        for (Map.Entry<String, List<Long>> entry : progresoCursos.entrySet()) {
            String cursoId = entry.getKey().replace("curso_", "");
            int idCurso = Integer.parseInt(cursoId);
            int i = index[0];

            db.collection("cursos")
                    .whereEqualTo("idCurso", idCurso)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshotCurso -> {
                        if (!snapshotCurso.isEmpty()) {
                            String nombre = snapshotCurso.getDocuments().get(0).getString("titulo");
                            db.collection("clases")
                                    .whereEqualTo("idCurso", idCurso)
                                    .get()
                                    .addOnSuccessListener(snapshotClases -> {
                                        int total = snapshotClases.size();
                                        float porcentaje = total > 0 ? entry.getValue().size() * 100f / total : 0f;
                                        entries.add(new BarEntry(i, porcentaje));
                                        etiquetas.add(nombre != null ? nombre : "Curso " + cursoId);
                                        if (entries.size() == progresoCursos.size()) {
                                            mostrarGrafica(entries, etiquetas, chartProgresoCursos);
                                        }
                                    });
                        }
                    });

            index[0]++;
        }
    }

    private void mostrarGrafica(List<BarEntry> entries, List<String> etiquetas, BarChart chart) {
        BarDataSet dataSet = new BarDataSet(entries, "Progreso cursos");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return Math.round(barEntry.getY()) + "%";
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(0);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setFitBars(true);
        chart.animateY(1000);
        chart.invalidate();
    }

    private interface NivelInstrumentoCallback {
        void onResultado(Map<String, Map<String, Object>> resultado);
    }

    private void calcularNivelInstrumentos(FirebaseFirestore db, String userId, NivelInstrumentoCallback callback) {
        DocumentReference userRef = db.collection("usuarios").document(userId);
        userRef.get().addOnSuccessListener(userDoc -> {
            Map<String, Map<String, Object>> resultado = new HashMap<>();
            Map<String, Object> tiempoPorClase = (Map<String, Object>) userDoc.get("tiempoPorClase");
            Map<String, Object> progresoCursos = (Map<String, Object>) userDoc.get("progresoCursos");

            if (tiempoPorClase == null || progresoCursos == null) {
                callback.onResultado(resultado);
                return;
            }

            Map<String, Integer> tiempoPorInstrumento = new HashMap<>();
            Map<String, Integer> clasesPorInstrumento = new HashMap<>();
            List<Task<Void>> tareas = new ArrayList<>();

            for (String clave : tiempoPorClase.keySet()) {
                Map<String, Object> clase = (Map<String, Object>) tiempoPorClase.get(clave);
                int idCurso = ((Number) clase.get("idCurso")).intValue();
                int tiempo = ((Number) clase.get("tiempo")).intValue();

                Task<Void> t = db.collection("cursos").whereEqualTo("idCurso", idCurso).get()
                        .continueWith(task -> {
                            for (DocumentSnapshot cursoDoc : task.getResult()) {
                                String instrumento = cursoDoc.getString("instrumento");
                                if (instrumento != null)
                                    tiempoPorInstrumento.merge(instrumento, tiempo, Integer::sum);
                            }
                            return null;
                        });
                tareas.add(t);
            }

            for (String cursoKey : progresoCursos.keySet()) {
                int idCurso = Integer.parseInt(cursoKey.replace("curso_", ""));
                List<Long> clases = (List<Long>) progresoCursos.get(cursoKey);

                Task<Void> t = db.collection("cursos").whereEqualTo("idCurso", idCurso).get()
                        .continueWith(task -> {
                            for (DocumentSnapshot cursoDoc : task.getResult()) {
                                String instrumento = cursoDoc.getString("instrumento");
                                if (instrumento != null)
                                    clasesPorInstrumento.merge(instrumento, clases.size(), Integer::sum);
                            }
                            return null;
                        });
                tareas.add(t);
            }

            Tasks.whenAllSuccess(tareas).addOnSuccessListener(r -> {
                for (String instrumento : tiempoPorInstrumento.keySet()) {
                    int tiempo = tiempoPorInstrumento.getOrDefault(instrumento, 0);
                    int clases = clasesPorInstrumento.getOrDefault(instrumento, 0);
                    String nivel = (tiempo >= 90 || clases >= 6) ? "Avanzado"
                            : (tiempo >= 30 || clases >= 3) ? "Intermedio"
                            : "Principiante";

                    Map<String, Object> data = new HashMap<>();
                    data.put("nivel", nivel);
                    data.put("tiempoTotal", tiempo);
                    data.put("clasesVistas", clases);
                    resultado.put(instrumento, data);
                }
                callback.onResultado(resultado);
            });
        });
    }

    private void cargarGraficaNivelInstrumentos(Map<String, Map<String, Object>> datos) {
        BarChart chart = findViewById(R.id.chartNivelInstrumentos);
        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int i = 0;

        for (Map.Entry<String, Map<String, Object>> entry : datos.entrySet()) {
            String instrumento = entry.getKey();
            int minutos = ((int) entry.getValue().get("tiempoTotal")) / 60;
            entries.add(new BarEntry(i, minutos));
            etiquetas.add(instrumento);
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Nivel por instrumento");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return Math.round(barEntry.getY()) + " min";
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setFitBars(true);
        chart.animateY(1000);
        chart.invalidate();
    }

}