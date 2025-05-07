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

        // Inicializamos las vistas
        seekBarDescanso = findViewById(R.id.seekBarDescanso);
        tiempoDescansoTextView = findViewById(R.id.tiempoDescansoTextView);
        guardarTiempoDescansoButton = findViewById(R.id.guardarTiempoDescanso);

        // Obtenemos la instancia de Firestore
        db = FirebaseFirestore.getInstance();

        // Mostrar el tiempo de descanso actual (si se tiene guardado)
        cargarTiempoDescanso();

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

                        // También se actualiza la variable global en la app para que se use en la lógica del conteo
                        tiempoUsuario tiempoUsuario = new tiempoUsuario(userId, getApplicationContext());
                        tiempoUsuario.cargarTiempoDeDescanso();  // Recargamos el tiempo de descanso en la app
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error
                        Log.e("ModificarTiempo", "❌ Error al guardar el tiempo de descanso", e);
                    });
        });
    }

    private void cargarTiempoDescanso() {
        // Recuperamos el tiempo de descanso desde Firestore para mostrarlo en la app
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDoc = db.collection("usuarios").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long tiempoDescansoFirestore = documentSnapshot.getLong("tiempo_descanso");
                if (tiempoDescansoFirestore != null) {
                    seekBarDescanso.setProgress(tiempoDescansoFirestore.intValue()); // Establecemos el progreso del SeekBar
                    tiempoDescansoTextView.setText("Tiempo de descanso: " + tiempoDescansoFirestore + " minutos");
                }
            }
        }).addOnFailureListener(e -> {
            // Manejar el error
            Log.e("ModificarTiempo", "❌ Error al cargar el tiempo de descanso", e);
        });
    }
}