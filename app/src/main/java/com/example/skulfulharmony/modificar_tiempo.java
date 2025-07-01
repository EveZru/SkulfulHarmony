package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Log;

import com.example.skulfulharmony.javaobjects.users.tiempoUsuario;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class modificar_tiempo extends AppCompatActivity {

    private SeekBar seekBarDescanso;
    private TextView tiempoDescansoTextView;
    private Button guardarTiempoDescansoButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_tiempo);

        seekBarDescanso = findViewById(R.id.seekBarDescanso);
        tiempoDescansoTextView = findViewById(R.id.tiempoDescansoTextView);
        guardarTiempoDescansoButton = findViewById(R.id.guardarTiempoDescanso);
        db = FirebaseFirestore.getInstance();
        cargarTiempoDescanso();
        seekBarDescanso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tiempoDescansoTextView.setText("Tiempo de descanso: " + progress + " minutos");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        guardarTiempoDescansoButton.setOnClickListener(v -> {

            int tiempoDescanso = seekBarDescanso.getProgress();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userDoc = db.collection("usuarios").document(userId);
            userDoc.update("tiempo_descanso", tiempoDescanso)
                    .addOnSuccessListener(aVoid -> {

                        tiempoDescansoTextView.setText("Tiempo de descanso actualizado: " + tiempoDescanso + " minutos");
                         tiempoUsuario tiempoUsuario = new tiempoUsuario(userId, getApplicationContext());
                        tiempoUsuario.cargarTiempoDeDescanso();  // Recargamos el tiempo de descanso en la app
                    })
                    .addOnFailureListener(e -> {

                        Log.e("ModificarTiempo", " Error al guardar el tiempo de descanso", e);
                    });
        });
    }

    private void cargarTiempoDescanso() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDoc = db.collection("usuarios").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long tiempoDescansoFirestore = documentSnapshot.getLong("tiempo_descanso");
                if (tiempoDescansoFirestore != null) {
                    seekBarDescanso.setProgress(tiempoDescansoFirestore.intValue());
                    tiempoDescansoTextView.setText("Tiempo de descanso: " + tiempoDescansoFirestore + " minutos");
                    finish();
                }
            }
        }).addOnFailureListener(e -> {
            // Manejar el error
            Log.e("ModificarTiempo", "Error al cargar el tiempo de descanso", e);
        });
    }
}