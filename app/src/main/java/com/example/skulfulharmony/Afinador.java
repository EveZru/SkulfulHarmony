package com.example.skulfulharmony;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull; // Importar para onRequestPermissionsResult
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.Yin; // O MpM, etc.


public class Afinador extends AppCompatActivity {

    private TextView noteTextView;
    private TextView frequencyTextView;
    private Spinner targetNoteSpinner;
    private String selectedTargetNote;

    // Constantes para la detección de tono (usadas por TarsosDSP)
    private final static int SAMPLE_RATE = 44100; // Tasa de muestreo
    private final static int BUFFER_SIZE = 8000; // Tamaño del buffer (TarsosDSP recomienda potencias de 2)
    private final static int OVERLAP = 0; // Solapamiento entre buffers

    private AudioDispatcher dispatcher; // Manejador de audio de TarsosDSP
    private PitchDetectionHandler pdh; // Manejador de resultados de detección de tono

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 200; // Código para la solicitud de permiso

    private final String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private final double A4_FREQUENCY = 440.0; // Frecuencia de A4 para cálculos de notas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_afinador);

        noteTextView = findViewById(R.id.noteTextView);
        frequencyTextView = findViewById(R.id.frequencyTextView);
        targetNoteSpinner = findViewById(R.id.targetNoteSpinner);

        // Configurar el ArrayAdapter para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, noteNames);
        targetNoteSpinner.setAdapter(adapter);

        // Establecer el listener para obtener la nota seleccionada
        targetNoteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTargetNote = (String) parent.getItemAtPosition(position);
                Toast.makeText(Afinador.this, "Nota objetivo seleccionada: " + selectedTargetNote, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTargetNote = null;
            }
        });

        if (checkAudioPermission()) {
            startPitchDetection(); // Permiso ya concedido, iniciar
            noteTextView.setText("cargando");

        } else {
            requestAudioPermission(); // Solicitar permiso
        }
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        // Usa REQUEST_AUDIO_PERMISSION_CODE para esta solicitud
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) { // Asegúrate de usar el mismo código
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permiso de micrófono concedido", Toast.LENGTH_LONG).show();
                // Si el permiso se concedió, iniciar la detección de tono con TarsosDSP

            } else {
                Toast.makeText(getApplicationContext(), "Permiso de micrófono denegado. No se puede usar el afinador.", Toast.LENGTH_LONG).show();
                // Aquí podrías cerrar la actividad o deshabilitar la funcionalidad del afinador
            }
        }
    }

    // La anotación @RequiresPermission es para que el IDE te advierta, pero no previene el error en runtime si el permiso no está.
    // La verdadera seguridad la da el check de permisos y la llamada condicional.
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private void startPitchDetection() {
        // Siempre detén el dispatcher anterior si existe antes de iniciar uno nuevo
        // Esto previene errores si onResume se llama varias veces.
        stopPitchDetection();

        // Inicializa el PitchDetectionHandler para manejar los resultados
        pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                float pitchInHz = pitchDetectionResult.getPitch(); // Obtiene la frecuencia en Hz


                // Actualizar la UI en el hilo principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pitchInHz > 0) { // Si se detectó un tono válido
                            frequencyTextView.setText(String.format("Frecuencia: %.2f Hz", pitchInHz));
                            noteTextView.setText(getNoteFromFrequency(pitchInHz));
                        } else {
                            frequencyTextView.setText("Frecuencia: --- Hz");
                            noteTextView.setText("--");
                        }
                    }
                });
            }
        };

        // Crea el AudioDispatcher desde el micrófono
        // TarsosDSP se encarga de usar AudioRecord internamente con los permisos ya concedidos.
        try {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, OVERLAP);

            // Creamos el procesador de detección de tono (YIN es un algoritmo popular)
            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, SAMPLE_RATE, BUFFER_SIZE, pdh);

            dispatcher.addAudioProcessor(pitchProcessor);

            // Ejecutamos el dispatcher en un nuevo hilo para no bloquear el hilo principal
            new Thread(dispatcher, "Audio Dispatcher").start();
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar el micrófono: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Afinador", "Error al iniciar TarsosDSP micrófono: " + e.getMessage(), e);
            // Si hay un error al iniciar, detén cualquier dispatcher que pueda haber quedado
            stopPitchDetection();
        }
    }

    private void stopPitchDetection() {
        if (dispatcher != null) {
            dispatcher.stop(); // Detiene el procesamiento de audio y libera recursos
            dispatcher = null;
            Log.d("Afinador", "TarsosDSP dispatcher detenido.");
        }
    }

    private String getNoteFromFrequency(double frequency) {
        if (frequency <= 0) return "---";

        double noteNum = 12 * Math.log(frequency / A4_FREQUENCY) / Math.log(2);
        int roundedNote = Math.round((float) noteNum);
        int octave = 4 + (roundedNote + 9) / 12; // Ajuste para que C4 sea el centro si A4 es 440
        int noteIndex = (roundedNote % 12 + 12) % 12; // Asegura un índice positivo (0-11)

        return noteNames[noteIndex] + octave;
    }

    // Métodos del ciclo de vida de la Activity
    @Override
    protected void onPause() {
        super.onPause();
        stopPitchDetection(); // Detener la detección al pausar la Activity
        Log.d("Afinador", "Activity en onPause. Deteniendo detección.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Afinador", "Activity en onResume. Chequeando permisos para iniciar detección.");
        if (checkAudioPermission()) {
            startPitchDetection(); // Reanudar la detección al volver a la Activity
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPitchDetection(); // Asegurarse de detenerlo al destruir la Activity
        Log.d("Afinador", "Activity en onDestroy. Deteniendo detección final.");
    }
}