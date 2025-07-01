package com.example.skulfulharmony;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull; // Importar para onRequestPermissionsResult
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class Afinador extends AppCompatActivity {

    private TextView tv_nota;
    private TextView tv_frecuencia;
    private Spinner targetNoteSpinner;
    private String notaObjetivo;
    private View v_fondo;
    private Button btn_decoracion;
    private boolean notaCoincide = false;

    // Constantes para la detección de tono (usadas por TarsosDSP)
    private final static int SAMPLE_RATE = 44100; // Tasa de muestreo
    private final static int BUFFER_SIZE = 8000; // Tamaño del buffer (TarsosDSP recomienda potencias de 2)
    private final static int OVERLAP = 0; // Solapamiento entre buffers

    private AudioDispatcher dispatcher; // Manejador de audio de TarsosDSP
    private PitchDetectionHandler pdh; // Manejador de resultados de detección de tono

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 200; // Código para la solicitud de permiso

    private final String[] noteNames = {"Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si"};
    private final double A4_FREQUENCY = 440.0; // Frecuencia de A4 para cálculos de notas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_afinador);

        tv_nota = findViewById(R.id.noteTextView);
        tv_frecuencia = findViewById(R.id.frequencyTextView);
        targetNoteSpinner = findViewById(R.id.targetNoteSpinner);
        v_fondo = findViewById(R.id.v_fondo);
        btn_decoracion = findViewById(R.id.btn_decoracion);
        // Configurar el ArrayAdapter para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, noteNames);
        targetNoteSpinner.setAdapter(adapter);

        targetNoteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                notaObjetivo = (String) parent.getItemAtPosition(position);
                mostrarMensaje("Nota objetivo seleccionada: " + notaObjetivo);
                updateUIWithCurrentNote();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                notaObjetivo = null;
            }
        });

        if (checkAudioPermission()) {
            startPitchDetection(); // Permiso ya concedido, iniciar
            tv_nota.setText("cargando");

        } else {
            requestAudioPermission(); // Solicitar permiso
        }
    }
    private void updateUIWithCurrentNote() {
        String currentNote = tv_nota.getText().toString();
        if (isValidNote(currentNote)) {
            String baseNote = extraerNota(currentNote);
            cambiarColor(baseNote, notaObjetivo);
        }
    }
    private boolean isValidNote(String note) {
        return note != null && !note.isEmpty() && !note.equals("--") && !note.equals("cargando");
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

            } else {
                Toast.makeText(getApplicationContext(), "Permiso de micrófono denegado. No se puede usar el afinador.", Toast.LENGTH_LONG).show();
                 }
        }
    }
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private void startPitchDetection() {
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
                            tv_frecuencia.setText(String.format("Frecuencia: %.2f Hz", pitchInHz));
                            tv_nota.setText(getNotadeFrecuencia(pitchInHz));
                            String baseNote = extraerNota(getNotadeFrecuencia(pitchInHz));
                            cambiarColor(baseNote, notaObjetivo);
                        } else {
                            tv_frecuencia.setText("Frecuencia: --- Hz");
                            tv_nota.setText("--");
                            cambiarColor(null, null);

                        }
                    }
                });
            }
        };
         try {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, OVERLAP);

             AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, SAMPLE_RATE, BUFFER_SIZE, pdh);

            dispatcher.addAudioProcessor(pitchProcessor);

             new Thread(dispatcher, "Audio Dispatcher").start();
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar el micrófono: " + e.getMessage(), Toast.LENGTH_LONG).show();
            stopPitchDetection();
        }
    }

    private void stopPitchDetection() {
        if (dispatcher != null) {
            dispatcher.stop(); // Detiene el procesamiento de audio y libera recursos
            dispatcher = null;

        }
    }

    private String getNotadeFrecuencia(double frequency) {
        if (frequency <= 0) return "---";
        final double A4 = 440.0;
        final int SEMITONES_PER_OCTAVE = 12;

        double semitonesFromA4 = SEMITONES_PER_OCTAVE * Math.log(frequency / A4) / Math.log(2);

        int nearestSemitone = (int) Math.round(semitonesFromA4);
        int noteIndex = (nearestSemitone % SEMITONES_PER_OCTAVE + SEMITONES_PER_OCTAVE) % SEMITONES_PER_OCTAVE;
        int octave = 4 + (nearestSemitone + 9) / SEMITONES_PER_OCTAVE;
        if (noteIndex <= 1 && frequency < A4 * Math.pow(2, (noteIndex - 9) / (double)SEMITONES_PER_OCTAVE)) {
            octave--;
        }


        return noteNames[(noteIndex - 3 + 12) % 12] + octave;
    }

    private String extraerNota(String fullNoteWithOctave) {
        if (fullNoteWithOctave == null || fullNoteWithOctave.equals("---") || fullNoteWithOctave.equals("--")) {
            return null;
        }
        return fullNoteWithOctave.replaceAll("[0-9]", "");
    }
    private String ultimaNotaCoincidente = null;

    private void cambiarColor(String notaDetectada, String notaObjetivo) {
        runOnUiThread(() -> {
            try {
                boolean coincideAhora = notaObjetivo != null && notaDetectada != null &&
                        notaObjetivo.equalsIgnoreCase(notaDetectada);

                // Si antes no coincidía pero ahora sí
                if (coincideAhora && (ultimaNotaCoincidente == null ||
                        !ultimaNotaCoincidente.equals(notaDetectada))) {

                    int colorFondo = ContextCompat.getColor(Afinador.this, R.color.grayverde2);
                    int colorBoton = ContextCompat.getColor(Afinador.this, R.color.grayverde);
                    v_fondo.setBackgroundColor(colorFondo);
                    btn_decoracion.setBackgroundColor(colorBoton);
                    mostrarMensaje("Nota correcta");
                    ultimaNotaCoincidente = notaDetectada;
                }
                // Si antes coincidía pero ahora no
                else if (!coincideAhora && ultimaNotaCoincidente != null) {
                    int colorFondo = ContextCompat.getColor(Afinador.this, R.color.rojo_oscuro);
                    int colorBoton = ContextCompat.getColor(Afinador.this, R.color.rojo);
                    v_fondo.setBackgroundColor(colorFondo);
                    btn_decoracion.setBackgroundColor(colorBoton);

                    ultimaNotaCoincidente = null;
                }
            } catch (Exception e) {
             }
        });
    }
    private void mostrarMensaje(String mensaje) {
        View layout = getLayoutInflater().inflate(R.layout.holder_boton_extra, null);
        Button boton = layout.findViewById(R.id.btn_ver_mas);
        boton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.cafe1));
        boton.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPitchDetection();
       }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Afinador", "Activity en onResume. Chequeando permisos para iniciar detección.");
        if (checkAudioPermission()) {
            startPitchDetection();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPitchDetection();
         }

}