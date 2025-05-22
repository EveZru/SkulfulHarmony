package com.example.skulfulharmony;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.google.firebase.Timestamp;
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

        {
            RadioButton rb_contenidoilegal = findViewById(R.id.rb_contenidoilegal);
            RadioButton rb_suplantacion = findViewById(R.id.rb_suplantacion);
            RadioButton rb_inapropiado = findViewById(R.id.rb_inapropiado);
            RadioButton rb_spam = findViewById(R.id.rb_spam);
            RadioButton rb_abuso = findViewById(R.id.rb_abuso);
            RadioButton rb_normas = findViewById(R.id.rb_normas_);
            RadioButton rb_otro = findViewById(R.id.rb_otro);

            rb_contenidoilegal.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            rb_suplantacion.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            rb_inapropiado.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            rb_spam.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            rb_abuso.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            rb_normas.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
            rb_otro.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
        }

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
                String txtDenuncia = et_denuncia.getText().toString();

                // Validación: Verificar que el campo de denuncia no esté vacío
                if (txtDenuncia.isEmpty()) {
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
                } else if (selectedId == R.id.rb_otro) {
                    tipoDenuncia = "Otro";
                }

                // Crea el objeto denuncia
                Denuncia denuncia = new Denuncia(usuario, tipoDenuncia, txtDenuncia,idCurso);
                denuncia.setIdClase(null);
                denuncia.setFecha_denuncia(Timestamp.now());
                View dialogView = LayoutInflater.from(CrearDenuncia.this).inflate(R.layout.dialog_crear_denuncia, null);

                // ✅ Buscar dentro del layout del diálogo, no en la actividad
                Button btn_cancelar = dialogView.findViewById(R.id.btn_cancelar_dialog_creardenuncia);
                Button btn_enviar = dialogView.findViewById(R.id.btn_enviar_dialog_creardenuncia);
                TextView tipo_denuncia = dialogView.findViewById(R.id.txt_vertipo_denuncia);
                TextView denuncia_texto = dialogView.findViewById(R.id.et_vermensaje_denuncia);

                // ✅ Establecer texto y scrolling aquí
                tipo_denuncia.setText("Tipo: " + tipoDenuncia);
                denuncia_texto.setText("Contenido: \n \n" + txtDenuncia);
                denuncia_texto.setMovementMethod(new ScrollingMovementMethod()); // Scroll funcionando

                // Crear y mostrar el diálogo
                AlertDialog alertDialog = new AlertDialog.Builder(CrearDenuncia.this)
                        .setView(dialogView)
                        .create();

                btn_cancelar.setOnClickListener(v2 -> alertDialog.dismiss());
                btn_enviar.setOnClickListener(v2 -> {
                    firestore.collection("denuncias").add(denuncia);
                    Toast.makeText(CrearDenuncia.this, "Denuncia creada:\n" +
                            "Usuario: " + denuncia.getUsuario() + "\n" +
                            "Tipo: " + denuncia.getTipo_denuncia() + "\n" +
                            "Denuncia: " + denuncia.getDenuncia(), Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                });

                alertDialog.show();


                tipo_denuncia.setText("Tipo: " + tipoDenuncia);
                denuncia_texto.setMovementMethod(new ScrollingMovementMethod());
                denuncia_texto.setText("Contenido: \n" + txtDenuncia);
                alertDialog.show();

            }//
        });





    }

}