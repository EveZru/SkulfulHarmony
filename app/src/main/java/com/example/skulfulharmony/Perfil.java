package com.example.skulfulharmony;

// 🧠 Clases internas del proyecto
import com.example.skulfulharmony.server.config.DropboxConfig; // Configuración del cliente de Dropbox

// ☁️ Dropbox SDK
import com.dropbox.core.v2.DbxClientV2; // Cliente principal de Dropbox
import com.dropbox.core.v2.files.FileMetadata; // Metadatos de archivos
import com.dropbox.core.v2.files.UploadErrorException; // Excepción al subir archivos
import com.dropbox.core.v2.sharing.SharedLinkMetadata; // Metadatos del enlace compartido

// 📱 Android básico
import android.content.Intent; // Navegación entre actividades
import android.net.Uri; // Referencia a recursos (como imágenes)
import android.os.Bundle; // Datos entre actividades
import android.os.Handler; // Ejecutar tareas en el hilo principal
import android.os.Looper; // Obtener el hilo principal
import android.util.Log; // Logcat
import android.widget.Button; // Botón
import android.widget.ImageView; // Imagen
import android.widget.LinearLayout;
import android.widget.TextView; // Texto
import android.widget.Toast; // Mensajes emergentes
import android.view.View;

// 🔄 Concurrencia y ejecución en segundo plano
import java.util.concurrent.ExecutorService; // Ejecutor de hilos
import java.util.concurrent.Executors; // Utilidad para crear ejecutores

import android.view.MenuInflater;
import android.widget.PopupMenu;

// 🖼️ Librería externa para imágenes
import com.bumptech.glide.Glide; // Cargar imágenes desde URL

// 🧩 Firebase
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth; // Autenticación
import com.google.firebase.auth.FirebaseUser; // Usuario actual
import com.google.firebase.firestore.DocumentReference; // Referencia a documentos
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore; // Base de datos Firestore

// 💾 Manejo de archivos
import java.io.File; // Representación de archivo
import java.io.FileInputStream; // Leer archivos
import java.io.FileOutputStream; // Escribir archivos
import java.io.IOException; // Excepciones de I/O
import java.io.InputStream; // Flujo de entrada

// 🗂️ Colecciones
import java.util.HashMap; // Mapa clave-valor
import java.util.Map; // Interfaz de mapa

// 🧭 UI Material Design
import com.google.android.material.bottomnavigation.BottomNavigationView; // Barra de navegación

// 🎨 Compatibilidad
import androidx.appcompat.app.AppCompatActivity; // Actividad compatible

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.ValueFormatter;

import android.graphics.Color;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_correo, tv_DescripcionUsuario, tv_No_Cursos, tv_Seguidores, tv_Seguido;
    private Button btn_preguntas_incorrectas;
    private ImageView btn_gotoconfiguracion ;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId;
    private static final String ACCESS_TOKEN =  DropboxConfig.ACCESS_TOKEN;
    private LineChart chartPracticaSemanal;
    private BarChart chartProgresoCursos;
    private BarChart chartNivelInstrumentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Obtener el ID del usuario autenticado
        } else {
            // Si el usuario no está autenticado, redirige al login
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
            return;
        }


        // Referencias UI
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tv_NombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tv_No_Cursos = findViewById(R.id.tv_No_Cursos);
        tv_DescripcionUsuario = findViewById(R.id.tv_DescripcionUsuario);
        tv_correo = findViewById(R.id.tv_correo);
        tv_Seguidores = findViewById(R.id.tv_Seguidores);
        tv_Seguido = findViewById(R.id.tv_Seguido);


        cargarDatosUsuario();

        btn_gotoconfiguracion = findViewById(R.id.iv_gotoconfiguracion);
        btn_gotoconfiguracion.setOnClickListener(v -> showPopupMenu(v));  // Se pasa la vista del botón que se ha clickeado
        btn_preguntas_incorrectas=findViewById(R.id.btn_preguntas_incorrectas);
        btn_preguntas_incorrectas.setOnClickListener(v -> {
         Intent intent;
         intent=new Intent(Perfil.this,PreguntasIncorrectas.class);
         startActivity(intent);
        });

        // Configuración de los botones
        ivProfilePicture.setOnClickListener(v -> seleccionarImagen());

        // Configurar la barra de navegación
        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_perfil);

        // Gráfica de práctica semanal
        chartPracticaSemanal = findViewById(R.id.chartPracticaSemanal);
        chartProgresoCursos = findViewById(R.id.chartProgresoCursos);
        chartNivelInstrumentos = findViewById(R.id.chartNivelInstrumentos);


        if (bottomNavigationView1 != null) {
            bottomNavigationView1.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.it_homme) {
                    startActivity(new Intent(Perfil.this, Home.class));
                    return true;
                } else if (itemId == R.id.it_new) {
                    startActivity(new Intent(Perfil.this, VerCursosCreados.class));
                    return true;
                } else if (itemId == R.id.it_seguidos) {
                    startActivity(new Intent(Perfil.this, Biblioteca.class));
                    return true;
                } else if (itemId == R.id.it_perfil) {
                    return true;
                }
                return false;
            });
        }
    }

    // Método para mostrar el menú flotante
    private void showPopupMenu(View view) {
        // Crear el PopupMenu y asociarlo con la vista 'view' (el botón de configuración)
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.config_menu, popupMenu.getMenu());  // Aquí se infla el menú desde el archivo config_menu.xml

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menuVerTiempo) {
                startActivity(new Intent(Perfil.this, vertiempousuario.class));
                return true;
            } else if (id == R.id.menuEditarCuenta) {
                startActivity(new Intent(Perfil.this, EditarPerfil.class));
                return true;
            } else if (id == R.id.menuCerrarSesion) {
                startActivity(new Intent(Perfil.this, CerrarSesion.class));
                return true;
            } else if (id == R.id.menuEliminarCuenta) {
                startActivity(new Intent(Perfil.this, EliminarCuenta.class));
                return true;
            } else if (id == R.id.menuTerminos) {
                startActivity(new Intent(Perfil.this, TerminosCondiciones.class));
                return true;
            } else if (id == R.id.menuContactanos) {
                startActivity(new Intent(Perfil.this, Contactanos.class));
                return true;
            }  else if (id == R.id.menuCreditos) {
                startActivity(new Intent(Perfil.this, creditos.class));
                return true;
            }  else if (id == R.id.menuNotificaciones) {
                startActivity(new Intent(Perfil.this, CentroNotificaciones.class));
                return true;
        }
            return false;
        });

        // Mostrar el menú
        popupMenu.show();
    }

    private void cargarDatosUsuario() {
        if (userId == null) return;

        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Cargar datos básicos
                tv_NombreUsuario.setText(documentSnapshot.getString("nombre"));
                tv_correo.setText(documentSnapshot.getString("correo"));
                tv_DescripcionUsuario.setText(
                        documentSnapshot.getString("descripcion")
                        != null && !documentSnapshot.getString("descripcion").isEmpty()
                        ? documentSnapshot.getString("descripcion")
                        : "Sin descripción");

                Long cursos = documentSnapshot.getLong("cursos");
                tv_No_Cursos.setText("Cursos Creados: " + (cursos != null ? cursos : 0));

                Long seguidores = documentSnapshot.getLong("seguidores");
                Long seguidos = documentSnapshot.getLong("seguidos");
                tv_Seguidores.setText("Seguidores: " + (seguidores != null ? seguidores : 0));
                tv_Seguido.setText("Seguidos: " + (seguidos != null ? seguidos : 0));

                String fotoUrl = documentSnapshot.getString("fotoPerfil");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this).load(fotoUrl).into(ivProfilePicture);
                }

                // Procesar tiempos diarios para gráfica de práctica semanal
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

                // 🔢 Mostrar progreso de cursos en gráfico
                Object rawProgresoCursos = documentSnapshot.get("progresoCursos");
                if (rawProgresoCursos instanceof Map) {
                    Map<String, List<Long>> progresoCursos = new HashMap<>();
                    Map<String, Object> progresoMap = (Map<String, Object>) rawProgresoCursos;

                    for (Map.Entry<String, Object> entry : progresoMap.entrySet()) {
                        String cursoId = entry.getKey();
                        Object listaRaw = entry.getValue();
                        List<Long> clases = new ArrayList<>();
                        if (listaRaw instanceof List<?>) {
                            for (Object item : (List<?>) listaRaw) {
                                if (item instanceof Number) {
                                    clases.add(((Number) item).longValue());
                                }
                            }
                        }
                        progresoCursos.put(cursoId, clases);
                    }

                    cargarGraficaProgresoCursos(progresoCursos);
                }

                // 🧠 Calcular nivel por instrumento desde Perfil directamente
                calcularNivelInstrumentos(db, userId, resultado -> {
                    for (String instrumento : resultado.keySet()) {
                        Map<String, Object> nivelData = resultado.get(instrumento);
                        String nivel = (String) nivelData.get("nivel");
                        int tiempo = (int) nivelData.get("tiempoTotal");
                        int clases = (int) nivelData.get("clasesVistas");

                        Log.d("NivelInstrumento", "🎸 " + instrumento + ": " + nivel +
                                " (Tiempo: " + tiempo + "s, Clases: " + clases + ")");
                    }

                    cargarGraficaNivelInstrumentos(resultado);
                });

            } else {
                // Crear perfil nuevo con valores predeterminados
                Map<String, Object> userData = new HashMap<>();
                userData.put("nombre", "Usuario");
                userData.put("correo", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                userData.put("descripcion", "Sin descripción");
                userData.put("cursos", 0);
                userData.put("seguidores", 0);
                userData.put("seguidos", 0);

                db.collection("usuarios").document(userId).set(userData)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Perfil creado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al crear perfil", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
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
        List<Entry> entries = new ArrayList<>();
        List<Integer> semanasOrdenadas = new ArrayList<>(tiemposPorSemana.keySet());
        Collections.sort(semanasOrdenadas);

        final List<String> etiquetasSemanas = new ArrayList<>();

        for (int i = 0; i < semanasOrdenadas.size(); i++) {
            int semana = semanasOrdenadas.get(i);
            int minutos = tiemposPorSemana.get(semana);
            entries.add(new Entry(i, minutos));
            etiquetasSemanas.add("Semana " + semana);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Minutos de práctica por semana");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        chartPracticaSemanal.setData(lineData);

        // Configurar eje X con etiquetas
        XAxis xAxis = chartPracticaSemanal.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasSemanas));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Configurar eje Y
        chartPracticaSemanal.getAxisLeft().setAxisMinimum(0f);
        chartPracticaSemanal.getAxisRight().setEnabled(false);

        // Descripción y leyenda
        chartPracticaSemanal.getDescription().setText("Minutos de práctica semanal");
        chartPracticaSemanal.getDescription().setTextSize(14f);
        chartPracticaSemanal.getLegend().setEnabled(true);

        // Animación
        chartPracticaSemanal.animateY(1000);
        chartPracticaSemanal.invalidate(); // refrescar gráfica

        // 🔍 Comparación entre semana actual y anterior
        TextView tvComparacion = findViewById(R.id.tvComparacionSemana);

        if (semanasOrdenadas.size() >= 2) {
            int semanaActual = semanasOrdenadas.get(semanasOrdenadas.size() - 1);
            int semanaAnterior = semanasOrdenadas.get(semanasOrdenadas.size() - 2);

            int tiempoActual = tiemposPorSemana.get(semanaActual);
            int tiempoAnterior = tiemposPorSemana.get(semanaAnterior);

            if (tiempoAnterior > 0) {
                float diferencia = ((float) (tiempoActual - tiempoAnterior) / tiempoAnterior) * 100;
                String mensaje = "Tu tiempo esta semana fue " + tiempoActual + " min, ";

                if (diferencia > 0) {
                    mensaje += "un " + String.format("%.1f", diferencia) + "% más que la semana anterior 🚀";
                    tvComparacion.setTextColor(Color.parseColor("#66BB6A")); // verde
                } else if (diferencia < 0) {
                    mensaje += "un " + String.format("%.1f", -diferencia) + "% menos que la semana anterior 😓";
                    tvComparacion.setTextColor(Color.parseColor("#EF5350")); // rojo
                } else {
                    mensaje += "igual que la semana anterior 😐";
                    tvComparacion.setTextColor(Color.parseColor("#FFEB3B")); // amarillo
                }

                tvComparacion.setText(mensaje);
            } else {
                tvComparacion.setText("Tu tiempo esta semana fue " + tiempoActual + " min. No hay datos anteriores.");
                tvComparacion.setTextColor(Color.GRAY);
            }
        } else {
            tvComparacion.setText("Necesitas al menos dos semanas de práctica para comparar.");
            tvComparacion.setTextColor(Color.LTGRAY);
        }
    }

    private void cargarGraficaProgresoCursos(Map<String, List<Long>> progresoCursos) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetasCursos = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int totalCursos = progresoCursos.size();
        int[] index = {0};

        for (Map.Entry<String, List<Long>> entry : progresoCursos.entrySet()) {
            String cursoIdStr = entry.getKey().replace("curso_", "");
            int cursoId = Integer.parseInt(cursoIdStr);
            List<Long> clasesCompletadas = entry.getValue();
            int currentIndex = index[0];

            db.collection("cursos")
                    .whereEqualTo("idCurso", cursoId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshotCurso -> {
                        if (!snapshotCurso.isEmpty()) {
                            String nombreCurso = snapshotCurso.getDocuments().get(0).getString("titulo");

                            db.collection("clases")
                                    .whereEqualTo("idCurso", cursoId)
                                    .get()
                                    .addOnSuccessListener(snapshotClases -> {
                                        int totalClases = snapshotClases.size();
                                        float porcentaje = totalClases > 0 ? (clasesCompletadas.size() * 100f / totalClases) : 0f;

                                        entries.add(new BarEntry(currentIndex, porcentaje));
                                        etiquetasCursos.add(nombreCurso != null ? nombreCurso : "Curso " + cursoIdStr);

                                        if (entries.size() == totalCursos) {
                                            mostrarGrafica(entries, etiquetasCursos);
                                        }
                                    }).addOnFailureListener(e ->
                                            Log.e("ProgresoCursos", "❌ Error obteniendo clases de curso " + cursoId, e)
                                    );
                        } else {
                            Log.w("ProgresoCursos", "⚠️ Curso no encontrado para ID " + cursoId);
                        }
                    });

            index[0]++;
        }
    }

    private void mostrarGrafica(List<BarEntry> entries, List<String> etiquetasCursos) {
        BarDataSet dataSet = new BarDataSet(entries, "Progreso (%) por curso");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // Mostrar solo el porcentaje dentro de la barra
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return Math.round(barEntry.getY()) + "%";
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f); // más delgado para evitar empalme

        chartProgresoCursos.setData(barData);

        // Etiquetas en el eje X
        XAxis xAxis = chartProgresoCursos.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasCursos));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0); // 🔄 sin inclinación
        xAxis.setLabelCount(etiquetasCursos.size());

        chartProgresoCursos.getAxisLeft().setAxisMinimum(0f);
        chartProgresoCursos.getAxisLeft().setAxisMaximum(100f); // porque es porcentaje
        chartProgresoCursos.getAxisRight().setEnabled(false);
        chartProgresoCursos.getDescription().setEnabled(false);
        chartProgresoCursos.getLegend().setEnabled(false);

        chartProgresoCursos.setFitBars(true);
        chartProgresoCursos.animateY(1000);
        chartProgresoCursos.invalidate(); // 🧼 refrescar vista
    }

    // 🔍 Extrae correctamente el nombre del instrumento sin importar el tipo de campo
    private String extraerInstrumento(DocumentSnapshot cursoDoc) {
        Object instrumentoObj = cursoDoc.get("instrumento");
        if (instrumentoObj instanceof String) {
            return (String) instrumentoObj;
        } else if (instrumentoObj instanceof Map) {
            Map<String, Object> instrumentoMap = (Map<String, Object>) instrumentoObj;
            if (!instrumentoMap.isEmpty()) {
                return instrumentoMap.keySet().iterator().next();
            }
        }
        return null;
    }

    private void calcularNivelInstrumentos(FirebaseFirestore db, String userId, NivelInstrumentoCallback callback) {
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(userDoc -> {
            Map<String, Map<String, Object>> resultadoFinal = new HashMap<>();

            Map<String, Object> tiempoPorClase = (Map<String, Object>) userDoc.get("tiempoPorClase");
            Map<String, Object> progresoCursos = (Map<String, Object>) userDoc.get("progresoCursos");

            if (tiempoPorClase == null || progresoCursos == null) {
                Log.w("NivelInstrumento", "⛔ No hay datos suficientes para calcular niveles");
                mostrarNivelesEnTexto(resultadoFinal);
                callback.onResultado(resultadoFinal);
                return;
            }

            Map<String, Integer> tiempoPorInstrumento = new HashMap<>();
            Map<String, Integer> clasesPorInstrumento = new HashMap<>();
            List<Task<Void>> tareas = new ArrayList<>();

            for (String clave : tiempoPorClase.keySet()) {
                try {
                    Map<String, Object> claseInfo = (Map<String, Object>) tiempoPorClase.get(clave);
                    int idCurso = ((Number) claseInfo.get("idCurso")).intValue();
                    int tiempo = ((Number) claseInfo.get("tiempo")).intValue();

                    Task<Void> tarea = db.collection("cursos")
                            .whereEqualTo("idCurso", idCurso)
                            .limit(1)
                            .get()
                            .continueWith(task -> {
                                for (DocumentSnapshot cursoDoc : task.getResult()) {
                                    String instrumento = extraerInstrumento(cursoDoc);
                                    if (instrumento != null) {
                                        tiempoPorInstrumento.merge(instrumento, tiempo, Integer::sum);
                                    } else {
                                        Log.w("NivelInstrumento", "⚠️ Curso sin instrumento válido (tiempo): " + idCurso);
                                    }
                                }
                                return null;
                            });

                    tareas.add(tarea);
                } catch (Exception e) {
                    Log.e("NivelInstrumento", "❌ Error en tiempoPorClase: " + clave, e);
                }
            }

            for (String claveCurso : progresoCursos.keySet()) {
                try {
                    int idCurso = Integer.parseInt(claveCurso.replace("curso_", ""));
                    List<Long> clasesCompletadas = (List<Long>) progresoCursos.get(claveCurso);

                    Task<Void> tarea = db.collection("cursos")
                            .whereEqualTo("idCurso", idCurso)
                            .limit(1)
                            .get()
                            .continueWith(task -> {
                                for (DocumentSnapshot cursoDoc : task.getResult()) {
                                    String instrumento = extraerInstrumento(cursoDoc);
                                    if (instrumento != null) {
                                        clasesPorInstrumento.merge(instrumento, clasesCompletadas.size(), Integer::sum);
                                    } else {
                                        Log.w("NivelInstrumento", "⚠️ Curso sin instrumento válido (progreso): " + idCurso);
                                    }
                                }
                                return null;
                            });

                    tareas.add(tarea);
                } catch (Exception e) {
                    Log.e("NivelInstrumento", "❌ Error en progresoCursos: " + claveCurso, e);
                }
            }

            Tasks.whenAllSuccess(tareas).addOnSuccessListener(result -> {
                for (String instrumento : tiempoPorInstrumento.keySet()) {
                    int tiempo = tiempoPorInstrumento.getOrDefault(instrumento, 0);
                    int clases = clasesPorInstrumento.getOrDefault(instrumento, 0);
                    String nivel;

                    if (tiempo >= 90 || clases >= 6) {
                        nivel = "Avanzado";
                    } else if (tiempo >= 30 || clases >= 3) {
                        nivel = "Intermedio";
                    } else {
                        nivel = "Principiante";
                    }

                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nivel", nivel);
                    datos.put("tiempoTotal", tiempo);
                    datos.put("clasesVistas", clases);
                    resultadoFinal.put(instrumento, datos);
                }

                Log.d("NivelInstrumento", "🔥 Subiendo nivelInstrumentos: " + resultadoFinal);

                if (!resultadoFinal.isEmpty()) {
                    userRef.update("nivelInstrumentos", resultadoFinal)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "✅ nivelInstrumentos subido"))
                            .addOnFailureListener(e -> Log.e("Firestore", "❌ Error subiendo nivelInstrumentos", e));
                }

                mostrarNivelesEnTexto(resultadoFinal); // 👈 Mostrarlo en pantalla
                callback.onResultado(resultadoFinal);
            }).addOnFailureListener(e -> {
                Log.e("NivelInstrumento", "❌ Error en whenAllSuccess", e);
                callback.onResultado(resultadoFinal);
            });
        });
    }

    private interface NivelInstrumentoCallback {
        void onResultado(Map<String, Map<String, Object>> resultado);
    }

    private void mostrarNivelesEnTexto(Map<String, Map<String, Object>> datosInstrumento) {
        LinearLayout layout = findViewById(R.id.layout_nivel_instrumentos);
        layout.removeAllViews(); // limpia antes de cargar

        for (Map.Entry<String, Map<String, Object>> entry : datosInstrumento.entrySet()) {
            String instrumento = entry.getKey();
            Map<String, Object> valores = entry.getValue();

            String nivel = (String) valores.get("nivel");
            int tiempo = (int) valores.get("tiempoTotal"); // en segundos
            int clases = (int) valores.get("clasesVistas");

            int minutos = tiempo / 60;
            int segundos = tiempo % 60;

            String info = " Instrumento: " + instrumento +
                    "\nNivel: " + nivel +
                    "\nTiempo total: " + minutos + " min " + segundos + " s" +
                    "\nClases vistas: " + clases;

            TextView texto = new TextView(this);
            texto.setText(info);
            texto.setTextSize(16f);
            texto.setPadding(8, 8, 8, 16);
            texto.setTextColor(getResources().getColor(R.color.white));
            texto.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);

            layout.addView(texto);
        }
    }

    private void cargarGraficaNivelInstrumentos(Map<String, Map<String, Object>> datosInstrumento) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetasInstrumentos = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Map<String, Object>> entry : datosInstrumento.entrySet()) {
            String instrumento = entry.getKey();
            Map<String, Object> valores = entry.getValue();
            int tiempo = (int) valores.get("tiempoTotal");

            entries.add(new BarEntry(index, tiempo / 60f)); // Mostrar en minutos
            etiquetasInstrumentos.add(instrumento);
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Minutos totales por instrumento");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // ✅ Mostrar “min” encima de cada barra
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format("%.0f min", barEntry.getY());
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        chartNivelInstrumentos.setData(barData);

        // ✅ Eje X (instrumentos)
        XAxis xAxis = chartNivelInstrumentos.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasInstrumentos));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0);
        xAxis.setLabelCount(etiquetasInstrumentos.size());

        // ✅ Eje Y con “min”
        chartNivelInstrumentos.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f min", value);
            }
        });
        chartNivelInstrumentos.getAxisLeft().setAxisMinimum(0f);

        chartNivelInstrumentos.getAxisRight().setEnabled(false);
        chartNivelInstrumentos.getDescription().setEnabled(false);
        chartNivelInstrumentos.getLegend().setEnabled(false);

        chartNivelInstrumentos.setFitBars(true);
        chartNivelInstrumentos.animateY(1000);
        chartNivelInstrumentos.invalidate();
    }

    // Método para seleccionar una imagen
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Método para copiar el URI de la imagen a un archivo temporal
    private File copiarUriAArchivoTemporal(Uri uri) {
        try {
            File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            Log.e("Archivo", "No se pudo crear archivo temporal", e);
            return null;
        }
    }

    // Método para subir la imagen a Dropbox
    private void subirImagenADropbox(File archivo) {
        if (archivo == null) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        long fileSize = archivo.length(); // Tamaño del archivo en bytes
        long maxFileSize = 10 * 1024 * 1024; // 10 MB en bytes

        if (fileSize > maxFileSize) {
            Toast.makeText(this, "La imagen es demasiado grande. El tamaño máximo permitido es 10 MB.", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DropboxConfig dropboxConfig = new DropboxConfig(ACCESS_TOKEN);
            DbxClientV2 client = dropboxConfig.getClient();

            try (FileInputStream fis = new FileInputStream(archivo)) {
                FileMetadata metadata = client.files()
                        .uploadBuilder("/Aplicaciones/skillfullharmony/FotosPerfil/" + archivo.getName())
                        .uploadAndFinish(fis);

                SharedLinkMetadata linkMetadata = client.sharing()
                        .createSharedLinkWithSettings(metadata.getPathLower());

                String urlImagen = linkMetadata.getUrl()
                        .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                        .replace("?dl=0", "");

                // Guardar URL en Firestore
                guardarFotoEnFirestore(urlImagen);

                handler.post(() -> {
                    Glide.with(this).load(urlImagen).into(ivProfilePicture);
                    Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                });

            } catch (UploadErrorException e) {
                Log.e("Dropbox", "Error al subir archivo", e);
                handler.post(() -> Toast.makeText(this, "Error al subir imagen (UploadError)", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                Log.e("Dropbox", "Error de lectura", e);
                handler.post(() -> Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e("Dropbox", "Error desconocido", e);
                handler.post(() -> Toast.makeText(this, "Error desconocido al subir imagen", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Método para guardar la foto en Firestore
    private void guardarFotoEnFirestore(String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .update("fotoPerfil", url)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "URL de foto guardada"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar URL", e));
    }

    // Método para convertir la URL de Dropbox a un enlace directo
    private String convertirLinkADirecto(String dropboxUrl) {
        return dropboxUrl.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "");
    }

    // Método para manejar el resultado de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            Log.d("DEBUG", "URI recibido: " + imageUri); // Log para ver si hay URI

            try {
                getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
            } catch (Exception e) {
                Log.e("DEBUG", "Error al tomar permisos persistentes", e);
            }

            ivProfilePicture.setImageURI(imageUri);

            File archivo = copiarUriAArchivoTemporal(imageUri);

            subirImagenADropbox(archivo);
        }
    }
}