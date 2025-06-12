//package com.example.skulfulharmony.modooffline;
//
//import android.Manifest;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Build;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.skulfulharmony.R;
//import com.example.skulfulharmony.VerClaseOffline;
//import com.example.skulfulharmony.adapters.AdapterDescarga;
//import com.example.skulfulharmony.databaseinfo.DbHelper;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.File;
//import java.util.*;
//
//public class DescargarClases extends AppCompatActivity {
//
//    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;
//
//    private RecyclerView recyclerView;
//    private AdapterDescarga adapter;
//    private List<ClaseFirebase> listaClases = new ArrayList<>();
//    private Set<String> clasesDescargadas = new HashSet<>();
//    private int cursoId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_descargar_clases);
//
//        cursoId = getIntent().getIntExtra("curso_id", -1);
//        if (cursoId == -1) {
//            Toast.makeText(this, "Error: ID de curso inválido", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        crearCanalDeNotificacion();
//        pedirPermisoDeNotificacionSiEsNecesario();
//
//        recyclerView = findViewById(R.id.recyclerViewClases);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        cargarClasesLocales();
//
//        FirebaseFirestore.getInstance()
//                .collection("Cursos")
//                .document(String.valueOf(cursoId))
//                .collection("Clases")
//                .get()
//                .addOnSuccessListener(query -> {
//                    for (DocumentSnapshot doc : query.getDocuments()) {
//                        ClaseFirebase clase = doc.toObject(ClaseFirebase.class);
//                        if (clase != null) {
//                            listaClases.add(clase);
//                        }
//                    }
//                    mostrarClases();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error al cargar clases", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void crearCanalDeNotificacion() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    "canal_descargas",
//                    "Descargas de Clases",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//    }
//
//    private void pedirPermisoDeNotificacionSiEsNecesario() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                        REQUEST_CODE_POST_NOTIFICATIONS
//                );
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permiso para notificaciones concedido", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "No se otorgó permiso para notificaciones", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void cargarClasesLocales() {
//        SQLiteDatabase db = new DbHelper(this).getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT titulo FROM clasedescargada WHERE curso_id = ?", new String[]{String.valueOf(cursoId)});
//        if (cursor.moveToFirst()) {
//            do {
//                clasesDescargadas.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//    }
//
//    private void mostrarClases() {
//        adapter = new AdapterDescarga(listaClases, clasesDescargadas, this::descargarYGuardarClase);
//        recyclerView.setAdapter(adapter);
//    }
//
//    private void descargarYGuardarClase(ClaseFirebase clase) {
//        String titulo = clase.getTitulo().replace(" ", "_");
//        mostrarProgreso(clase.getTitulo(), 0);
//
//        if (clase.getImagenUrl() != null && !clase.getImagenUrl().isEmpty()) {
//            DropboxDownloader.descargarArchivo(this, clase.getImagenUrl(), "img_" + titulo + ".jpg", new DropboxDownloader.Callback() {
//                @Override
//                public void onSuccess(File archivoImagen) {
//                    clase.setImagenUrl(archivoImagen.getAbsolutePath());
//                    mostrarProgreso(clase.getTitulo(), 33);
//                    continuarConVideo(clase, titulo);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    continuarConVideo(clase, titulo);
//                }
//            });
//        } else {
//            continuarConVideo(clase, titulo);
//        }
//    }
//
//    private void continuarConVideo(ClaseFirebase clase, String titulo) {
//        if (clase.getVideoUrl() != null && !clase.getVideoUrl().isEmpty()) {
//            DropboxDownloader.descargarArchivo(this, clase.getVideoUrl(), "video_" + titulo + ".mp4", new DropboxDownloader.Callback() {
//                @Override
//                public void onSuccess(File archivoVideo) {
//                    clase.setVideoUrl(archivoVideo.getAbsolutePath());
//                    mostrarProgreso(clase.getTitulo(), 66);
//                    continuarConDocumento(clase, titulo);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    continuarConDocumento(clase, titulo);
//                }
//            });
//        } else {
//            continuarConDocumento(clase, titulo);
//        }
//    }
//
//    private void continuarConDocumento(ClaseFirebase clase, String titulo) {
//        if (clase.getDocumentoUrl() != null && !clase.getDocumentoUrl().isEmpty()) {
//            DropboxDownloader.descargarArchivo(this, clase.getDocumentoUrl(), "doc_" + titulo + ".pdf", new DropboxDownloader.Callback() {
//                @Override
//                public void onSuccess(File archivoDoc) {
//                    clase.setDocumentoUrl(archivoDoc.getAbsolutePath());
//                    finalizarDescarga(clase);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    finalizarDescarga(clase);
//                }
//            });
//        } else {
//            finalizarDescarga(clase);
//        }
//    }
//
//    private void finalizarDescarga(ClaseFirebase clase) {
//        mostrarProgreso(clase.getTitulo(), 100);
//        guardarClaseEnLocal(clase);
//        mostrarFinalizado(clase);
//
//        runOnUiThread(() -> {
//            Toast.makeText(this, "Clase '" + clase.getTitulo() + "' descargada", Toast.LENGTH_SHORT).show();
//            clasesDescargadas.add(clase.getTitulo());
//            adapter.notifyDataSetChanged();
//        });
//    }
//
//    private void guardarClaseEnLocal(ClaseFirebase clase) {
//        SQLiteDatabase db = new DbHelper(this).getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("curso_id", cursoId);
//        values.put("titulo", clase.getTitulo());
//        values.put("documento", clase.getDocumentoUrl());
//        values.put("imagen", clase.getImagenUrl());
//        values.put("video", clase.getVideoUrl());
//        db.insert("clasedescargada", null, values);
//        db.close();
//    }
//
//    private void mostrarProgreso(String tituloClase, int progreso) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_descargas")
//                .setSmallIcon(R.drawable.ic_cloud_download)
//                .setContentTitle("Descargando clase")
//                .setContentText(tituloClase)
//                .setProgress(100, progreso, false)
//                .setPriority(NotificationCompat.PRIORITY_LOW);
//
//        NotificationManagerCompat.from(this).notify(tituloClase.hashCode(), builder.build());
//    }
//
//    private void mostrarFinalizado(ClaseFirebase clase) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
//
//        Intent intent = new Intent(this, VerClaseOffline.class);
//        intent.putExtra("titulo", clase.getTitulo());
//        intent.putExtra("documento", clase.getDocumentoUrl());
//        intent.putExtra("imagen", clase.getImagenUrl());
//        intent.putExtra("video", clase.getVideoUrl());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this,
//                clase.getTitulo().hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_descargas")
//                .setSmallIcon(R.drawable.ic_check)
//                .setContentTitle("Clase descargada")
//                .setContentText(clase.getTitulo() + " lista para usar sin conexión")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat.from(this).notify(clase.getTitulo().hashCode(), builder.build());
//    }
//}