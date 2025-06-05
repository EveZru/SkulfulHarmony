package com.example.skulfulharmony;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class Afinador extends AppCompatActivity {

    private TextView noteTextView;
    private TextView frequencyTextView;
    private Spinner targetNoteSpinner;
    private String selectedTargetNote;

    // Parámetros para AudioRecord
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 44100; // Tasa de muestreo en Hz (importante que sea consistente)
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; // Formato PCM de 16 bits

    private int bufferSize; // Se calculará en onCreate
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 200;

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private Thread recordingThread;

    private final String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private final double A4_FREQUENCY = 440.0; // Frecuencia de A4 para cálculos de notas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Si usas EdgeToEdge, mantén esta línea
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
                // Aquí puedes agregar la lógica para usar la nota objetivo seleccionada
                // Por ejemplo, podrías actualizar algún indicador visual si la nota detectada coincide
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTargetNote = null;
            }
        });

        // Calcular el tamaño del buffer aquí, después de que los parámetros estáticos estén definidos
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (bufferSize == AudioRecord.ERROR_BAD_VALUE || bufferSize == AudioRecord.ERROR) {
            // Manejar error si no se puede obtener el tamaño mínimo del buffer
            bufferSize = 2048; // Valor predeterminado de seguridad si falla
            Toast.makeText(this, "No se pudo obtener el tamaño de buffer mínimo, usando: " + bufferSize, Toast.LENGTH_LONG).show();
        } else {
            // Es una buena práctica usar un buffer un poco más grande que el mínimo,
            // o un tamaño que sea potencia de 2 para FFT. Aquí, lo duplicamos por seguridad.
            bufferSize *= 2;
        }


        // Iniciar la grabación (y, por ende, la detección de tono) si los permisos ya están concedidos
        if (checkAudioPermission()) {
            startRecording();
        } else {
            requestAudioPermission();
        }
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permiso de audio concedido", Toast.LENGTH_LONG).show();
                // Si el permiso se concedió, iniciar la grabación
                // Comprobación adicional de permisos si es necesario, aunque ya se hizo arriba
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startRecording();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Permiso de audio denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private void startRecording() {
        if (isRecording) return; // Ya está grabando

        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Toast.makeText(this, "Error al inicializar AudioRecord", Toast.LENGTH_SHORT).show();
            return;
        }

        audioRecord.startRecording();
        isRecording = true;

        // Iniciar un nuevo hilo para leer y procesar el audio
        recordingThread = new Thread(new Runnable() {
            public void run() {
                processAudio();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false; // Detener el bucle del hilo
            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord.stop();
            }
            audioRecord.release(); // Liberar los recursos de AudioRecord
            audioRecord = null;
            recordingThread = null; // Quitar la referencia al hilo
        }
    }

    private void processAudio() {
        // short[] buffer = new short[bufferSize / 2]; // Si bufferSize es en bytes, short[] es la mitad.
        // Asumamos que bufferSize ya se calculó como el número de shorts que puede contener.
        short[] buffer = new short[bufferSize]; // Buffer para leer muestras de audio

        while (isRecording) {
            // Leer directamente en el buffer de shorts
            int shortsRead = audioRecord.read(buffer, 0, buffer.length);
            if (shortsRead > 0) {
                // Aquí es donde llamarías a tu propio algoritmo de detección de frecuencia.
                // Como ya se mencionó, findFundamentalFrequency() en su estado actual es muy básico.
                double detectedFrequency = findFundamentalFrequency(buffer, shortsRead);

                // Actualizar la UI en el hilo principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (detectedFrequency > 0) { // Asumimos que 0 o negativo significa no detectado
                            frequencyTextView.setText(String.format("Frecuencia: %.2f Hz", detectedFrequency));
                            noteTextView.setText(getNoteFromFrequency(detectedFrequency));
                        } else {
                            frequencyTextView.setText("Frecuencia: --- Hz");
                            noteTextView.setText("---");
                        }
                    }
                });
            }
        }
    }

    // Aes una IMPLEMENTACIÓN MUY BÁSICA
    // Y NO ES SUFICIENTE para un afinador preciso. Solo un marcador de posición.
    // Para un afinador real, necesitarías implementar un algoritmo de detección de tono más robusto.
    private double findFundamentalFrequency(short[] buffer, int size) {
        // La implementación actual busca el valor máximo en el buffer de tiempo.
        // Esto NO devuelve la frecuencia fundamental.
        // Necesitas un algoritmo de procesamiento de señales (ej. FFT, Autocorrelación)
        // para obtener una frecuencia real.

        // Si quieres solo ver algo en la UI, puedes devolver un valor fijo para pruebas:
        // return 440.0; // Siempre devuelve 440 Hz (Nota A4)

        // Si quieres mantener tu lógica actual (que no es una detección de frecuencia real):
        double maxAbs = 0;
        int maxIndex = 0;
        for (int i = 0; i < size; i++) {
            if (Math.abs(buffer[i]) > maxAbs) {
                maxAbs = Math.abs(buffer[i]);
                maxIndex = i;
            }
        }
        // Esta línea es conceptualmente incorrecta para la detección de frecuencia fundamental
        // y solo es un marcador de posición sin una FFT real.
        // Es un índice de tiempo, no de frecuencia.
        // Devuelve un valor que no es una frecuencia real, solo para que el UI se actualice.
        return (double) maxIndex * SAMPLE_RATE / size;
    }

    private String getNoteFromFrequency(double frequency) {
        if (frequency <= 0) return "---";

        double noteNum = 12 * Math.log(frequency / A4_FREQUENCY) / Math.log(2);
        int roundedNote = Math.round((float) noteNum);
        int octave = 4 + roundedNote / 12;
        int noteIndex = (roundedNote % 12 + 12) % 12; // Asegura un índice positivo (0-11)

        return noteNames[noteIndex] + octave;
    }

    // Métodos del ciclo de vida de la Activity
    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkAudioPermission()) {
            startRecording();
        }
    }
}