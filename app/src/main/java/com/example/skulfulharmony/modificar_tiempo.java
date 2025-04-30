package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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

        // Inicializamos las vistas
        seekBarDescanso = findViewById(R.id.seekBarDescanso);
        tiempoDescansoTextView = findViewById(R.id.tiempoDescansoTextView);
        guardarTiempoDescansoButton = findViewById(R.id.guardarTiempoDescanso);

        // Obtenemos la instancia de Firestore
        db = FirebaseFirestore.getInstance();

        // Mostramos el tiempo de descanso actual
        tiempoDescansoTextView.setText("Tiempo de descanso: " + seekBarDescanso.getProgress() + " minutos");

        // Configuramos el `SeekBar` para mostrar el tiempo seleccionado
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

        // Guardar el tiempo de descanso en Firestore cuando el usuario haga clic en el botón
        guardarTiempoDescansoButton.setOnClickListener(v -> {
            // Obtener el tiempo seleccionado del `SeekBar`
            int tiempoDescanso = seekBarDescanso.getProgress();

            // Obtener el ID del usuario actual
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Guardamos el tiempo de descanso en Firestore
            DocumentReference userDoc = db.collection("usuarios").document(userId);
            userDoc.update("tiempo_descanso", tiempoDescanso)
                    .addOnSuccessListener(aVoid -> {
                        // Notificar al usuario que el tiempo se guardó correctamente
                        tiempoDescansoTextView.setText("Tiempo de descanso actualizado: " + tiempoDescanso + " minutos");
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error
                    });
        });
    }
}