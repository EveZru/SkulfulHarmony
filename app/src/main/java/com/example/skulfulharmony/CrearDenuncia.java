package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skulfulharmony.javaobjects.miscellaneous.Demanda;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CrearDenuncia extends AppCompatActivity {

    private Button btn_denunciar;
    private RadioGroup rg_TipoDenuncia;
    private EditText et_denuncia;

    private FirebaseFirestore firestore;
    private int idCurso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creardenuncia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_denunciar = findViewById(R.id.btn_hacerdemanda);
        rg_TipoDenuncia = findViewById(R.id.rg_tipo_denuncia);
        et_denuncia = findViewById(R.id.et_descripciondemanda);

        btn_denunciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Captura la denuncia
                firestore = FirebaseFirestore.getInstance();
                // Obtener idCurso del intent
                idCurso = getIntent().getIntExtra("idCurso", -1);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String usuario = user.getEmail();
                String denuncia = et_denuncia.getText().toString().trim(); // Trim elimina espacios innecesarios



                // Validación: Verificar que el campo de denuncia no esté vacío
                if (denuncia.isEmpty()) {
                    Toast.makeText(CrearDenuncia.this, "Por favor, especifica el problema", Toast.LENGTH_SHORT).show();
                    return; // Si está vacío, salimos de la función
                }

                // Validación: Verificar que el usuario haya seleccionado un tipo de denuncia
                int selectedId = rg_TipoDenuncia.getCheckedRadioButtonId();
                if (selectedId == -1) {  // Si no se ha seleccionado ninguna opción
                    Toast.makeText(CrearDenuncia.this, "Por favor, selecciona el tipo de denuncia", Toast.LENGTH_SHORT).show();
                    return; // Si no se ha seleccionado, salimos de la función
                }


                // Determina el tipo de denuncia basado en la opción seleccionada
                String tipoDenuncia = "";
                if (selectedId == R.id.rb_contenidoilegal) {
                    tipoDenuncia = "Contenido ilegal";
                } else if (selectedId == R.id.rb_suplantacion) {
                    tipoDenuncia = "Suplantación de identidad";
                } else if (selectedId == R.id.rb_inapropiado) {
                    tipoDenuncia = "Contenido inapropiado";
                } else if (selectedId == R.id.rb_spam) {
                    tipoDenuncia = "Spam";
                } else if (selectedId == R.id.rb_abuso) {
                    tipoDenuncia = "Abuso de plataforma";
                } else if (selectedId == R.id.rb_normas_) {
                    tipoDenuncia = "Violación de normas de la plataforma";
                }

                // Crea el objeto denuncia
                Demanda demanda = new Demanda(usuario, tipoDenuncia, denuncia,idCurso);


                // Muestra un mensaje con los datos de la denuncia
                Toast.makeText(CrearDenuncia.this, "Denuncia creada:\n" +
                        "Usuario: " + demanda.getUsuario() + "\n" +
                        "Tipo: " + demanda.getTipo_denuncia() + "\n" +
                        "Denuncia: " + demanda.getDenuncia(), Toast.LENGTH_LONG).show();


            }//
        });





    }

}