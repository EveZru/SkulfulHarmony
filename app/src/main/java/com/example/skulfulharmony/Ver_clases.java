package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Ver_clases extends AppCompatActivity {
    private Button btn_subirVideo,btn_subirArchivo,btn_subirPreguta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verclases);

    }
}