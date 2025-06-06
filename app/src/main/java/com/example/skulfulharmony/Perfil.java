package com.example.skulfulharmony;

// 🧠 Clases internas del proyecto
import com.example.skulfulharmony.server.zip.ComprimirZip; // Para comprimir archivos
import com.example.skulfulharmony.server.config.DropboxConfig; // Configuración del cliente de Dropbox

// ☁️ Dropbox SDK
import com.dropbox.core.v2.DbxClientV2; // Cliente principal de Dropbox
import com.dropbox.core.v2.files.FileMetadata; // Metadatos de archivos
import com.dropbox.core.v2.files.UploadErrorException; // Excepción al subir archivos
import com.dropbox.core.v2.sharing.SharedLinkMetadata; // Metadatos del enlace compartido

// 📱 Android básico
import android.content.Intent; // Navegación entre actividades
import android.content.SharedPreferences;
import android.database.Cursor; // Lectura de bases de datos
import android.net.Uri; // Referencia a recursos (como imágenes)
import android.os.Bundle; // Datos entre actividades
import android.os.Handler; // Ejecutar tareas en el hilo principal
import android.os.Looper; // Obtener el hilo principal
import android.util.Log; // Logcat
import android.widget.Button; // Botón
import android.widget.ImageView; // Imagen
import android.widget.TextView; // Texto
import android.widget.Toast; // Mensajes emergentes
import android.view.View;

// 🔄 Concurrencia y ejecución en segundo plano
import java.util.concurrent.ExecutorService; // Ejecutor de hilos
import java.util.concurrent.Executors; // Utilidad para crear ejecutores

import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;

// 🖼️ Librería externa para imágenes
import com.bumptech.glide.Glide; // Cargar imágenes desde URL

// 🧩 Firebase
import com.google.firebase.auth.FirebaseAuth; // Autenticación
import com.google.firebase.auth.FirebaseUser; // Usuario actual
import com.google.firebase.firestore.DocumentReference; // Referencia a documentos
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
import android.graphics.Color;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView ivProfilePicture;
    private TextView tv_NombreUsuario, tv_correo, tv_DescripcionUsuario, tv_No_Cursos, tv_Seguidores, tv_Seguido;
    private Button btnVerVideoPrueba,btn_preguntas_incorrectas;
    private ImageView btn_gotoconfiguracion ;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId;
    private static final String ACCESS_TOKEN =  DropboxConfig.ACCESS_TOKEN;
    private LineChart chartPracticaSemanal;
    private  LineChart chartProgresoCuestionarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil); // Asegúrate de que el layout esté correctamente configurado


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
        btnVerVideoPrueba = findViewById(R.id.btnVerVideoPrueba);
        tv_correo = findViewById(R.id.tv_correo);
        tv_Seguidores = findViewById(R.id.tv_Seguidores);
        tv_Seguido = findViewById(R.id.tv_Seguido);

        btn_preguntas_incorrectas = findViewById(R.id.btn_preguntas_incorrectas);
        btn_preguntas_incorrectas.setOnClickListener(v -> startActivity(new Intent(Perfil.this, PreguntasIncorrectas.class)));

        cargarDatosUsuario();

        btn_gotoconfiguracion = findViewById(R.id.iv_gotoconfiguracion);
        btn_gotoconfiguracion.setOnClickListener(v -> showPopupMenu(v));  // Se pasa la vista del botón que se ha clickeado

        // Configuración de los botones
        ivProfilePicture.setOnClickListener(v -> seleccionarImagen());
        btnVerVideoPrueba.setOnClickListener(v -> startActivity(new Intent(Perfil.this, videos.class)));

        // Configurar la barra de navegación
        BottomNavigationView bottomNavigationView1 = findViewById(R.id.barra_navegacion1);
        bottomNavigationView1.setSelectedItemId(R.id.it_perfil);
        chartPracticaSemanal = findViewById(R.id.chartPracticaSemanal);
        chartProgresoCuestionarios = findViewById(R.id.chartProgresoCuestionarios);
        cargarGraficaErroresCuestionarios(chartProgresoCuestionarios);

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
                tv_DescripcionUsuario.setText(documentSnapshot.getString("descripcion"));

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

                // Procesar tiempos diarios para gráfica
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

    private void cargarGraficaErroresCuestionarios(LineChart chart) {
        SharedPreferences prefs = getSharedPreferences("historial_errores_Curso 1", MODE_PRIVATE);

        List<Entry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();

        int i = 0;
        while (prefs.contains("intento_" + (i + 1))) {
            i++;
            int errores = prefs.getInt("intento_" + i, 0);
            entries.add(new Entry(i - 1, errores));
            etiquetas.add("Intento " + i);
        }

        if (entries.isEmpty()) {
            chart.setVisibility(View.GONE);
            TextView tvResumen = findViewById(R.id.tvResumenErrores);
            tvResumen.setText("No hay errores registrados aún.");
            tvResumen.setTextColor(Color.GRAY);
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Errores por intento");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);

        chart.getDescription().setText("Errores en cuestionarios");
        chart.getDescription().setTextSize(14f);
        chart.getLegend().setEnabled(true);

        chart.animateY(1000);
        chart.invalidate();

        // 🔢 Mostrar resumen debajo de la gráfica
        TextView tvResumen = findViewById(R.id.tvResumenErrores);

        int totalErrores = 0;
        for (Entry entry : entries) {
            totalErrores += entry.getY();
        }

        float promedio = entries.size() > 0 ? (float) totalErrores / entries.size() : 0;
        tvResumen.setText("Promedio de errores por intento: " + String.format("%.2f", promedio));

        if (promedio >= 2.0f) {
            tvResumen.setTextColor(Color.RED); // demasiado errores
        } else if (promedio >= 1.0f) {
            tvResumen.setTextColor(Color.YELLOW); // promedio moderado
        } else {
            tvResumen.setTextColor(Color.GREEN); // buen resultado
        }
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