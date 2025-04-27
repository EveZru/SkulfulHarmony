package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class vertiempousuario extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userId;

    private TextView tiempoHoyTextView;
    private ProgressBar[] barrasDias;
    private Map<String, Long> tiemposPorDia = new HashMap<>();
    private long tiempoHoy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertiempousuario);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            // Si no hay usuario logueado, evitamos error
            finish();
            return;
        }

        tiempoHoyTextView = findViewById(R.id.tiempoHoyTextView);

        barrasDias = new ProgressBar[]{
                findViewById(R.id.barDomingo),
                findViewById(R.id.barLunes),
                findViewById(R.id.barMartes),
                findViewById(R.id.barMiercoles),
                findViewById(R.id.barJueves),
                findViewById(R.id.barViernes),
                findViewById(R.id.barSabado)
        };

        cargarDatos();
    }

    private void cargarDatos() {
        DocumentReference userDoc = db.collection("usuarios").document(userId);
        userDoc.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                actualizarPantalla();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.getData() != null) {
                String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                tiemposPorDia.clear(); // 🔥 Limpiar datos viejos
                tiempoHoy = 0;

                for (Map.Entry<String, Object> entry : documentSnapshot.getData().entrySet()) {
                    if (entry != null && entry.getKey() != null && entry.getKey().startsWith("tiempo_") && entry.getValue() instanceof Number) {
                        String fecha = entry.getKey().replace("tiempo_", "");
                        long minutos = ((Number) entry.getValue()).longValue();

                        if (fecha.equals(fechaHoy)) {
                            tiempoHoy = minutos;
                        }

                        tiemposPorDia.put(fecha, minutos);
                    }
                }

                actualizarPantalla();
            } else {
                actualizarPantalla();
            }
        });
    }
    private void actualizarPantalla() {
        tiempoHoyTextView.setText("Hoy llevas: " + tiempoUsuario.formatearMinutos(tiempoHoy));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        for (int i = 0; i < 7; i++) {
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            long minutos = tiemposPorDia.getOrDefault(fecha, 0L);

            barrasDias[i].setMax(240); // 240 minutos = 4 horas máximo
            barrasDias[i].setProgress((int) minutos);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}