package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Afinador extends AppCompatActivity {
    private TextView noteTextView;
    private TextView frequencyTextView;
    private Spinner targetNoteSpinner;
    private String selectedTargetNote;

    private final String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private final double A4_FREQUENCY = 440.0;

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
                // Aquí puedes agregar la lógica para usar la nota objetivo seleccionada
                // Por ejemplo, podrías mostrarla en el TextView de frecuencia por ahora
                frequencyTextView.setText("Nota objetivo: " + selectedTargetNote);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTargetNote = null;
                frequencyTextView.setText("Nota objetivo: ---");
            }
        });
    }

    private String getNoteFromFrequency(double frequency) {
        if (frequency <= 0) return "---";

        double noteNum = 12 * Math.log(frequency / A4_FREQUENCY) / Math.log(2);
        int roundedNote = Math.round((float) noteNum);
        int octave = 4 + roundedNote / 12;
        int noteIndex = (roundedNote % 12 + 12) % 12; // Asegura un índice positivo

        return noteNames[noteIndex] + octave;
    }
}