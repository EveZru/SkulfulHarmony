package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.TreeMap;

public class vertiempousuario extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userId;
    private TextView tiempoDeActividadTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vertiempousuario);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tiempoDeActividadTextView = findViewById(R.id.tiempoDeActividadTextView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadActivityTime();
    }

    private void loadActivityTime() {
        DocumentReference userDoc = db.collection("usuarios").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                StringBuilder historialBuilder = new StringBuilder();
                Long total = 0L;

                // Usamos TreeMap para ordenar por fecha
                Map<String, Object> sortedData = new TreeMap<>(documentSnapshot.getData());

                for (Map.Entry<String, Object> entry : sortedData.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith("tiempo_")) {
                        Long tiempo = ((Number) entry.getValue()).longValue();
                        String fecha = key.replace("tiempo_", "");
                        historialBuilder.append("📅 ").append(fecha).append(": ").append(tiempo).append(" min\n");
                        total += tiempo;
                    }
                }

                historialBuilder.append("\n🧮 Total acumulado: ").append(total).append(" min");
                tiempoDeActividadTextView.setText(historialBuilder.toString());
            } else {
                tiempoDeActividadTextView.setText("No hay datos de actividad aún.");
            }
        }).addOnFailureListener(e -> {
            tiempoDeActividadTextView.setText("Error al cargar los datos de tiempo.");
        });
    }

    public void onActualizarClick(android.view.View view) {
        loadActivityTime(); // Solo recarga, no hace conteo en esta vista
    }
}