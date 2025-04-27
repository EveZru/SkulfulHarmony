package com.example.skulfulharmony;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Contactanos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactanos); // Asegúrate de que el layout coincida
    }

    // Método para abrir Facebook
    public void openFacebook(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/SkillfulHarmony"));
        startActivity(browserIntent);
    }

    // Método para abrir Instagram
    public void openInstagram(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/SkillfulHarmony"));
        startActivity(browserIntent);
    }
}