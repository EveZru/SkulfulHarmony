package com.example.skulfulharmony;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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

import java.util.List;
import java.util.Map;

public class CrearDenuncia extends AppCompatActivity {

    private Button btn_denunciar;
    private RadioGroup rg_TipoDenuncia;
    private EditText et_denuncia;

    private FirebaseFirestore firestore;
    private int idCurso;
    private int idClase;
    private int idComentario;

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

        // RadioButtons tint setup
        for (int id : new int[]{
                R.id.rb_contenidoilegal, R.id.rb_suplantacion, R.id.rb_inapropiado,
                R.id.rb_spam, R.id.rb_abuso, R.id.rb_normas_, R.id.rb_otro
        }) {
            ((RadioButton) findViewById(id)).setButtonTintList(getResources().getColorStateList(R.color.radiobutton_tint));
        }

        btn_denunciar = findViewById(R.id.btn_hacerdemanda);
        rg_TipoDenuncia = findViewById(R.id.rg_tipo_denuncia);
        et_denuncia = findViewById(R.id.et_descripciondemanda);

        btn_denunciar.setOnClickListener(view -> {
            firestore = FirebaseFirestore.getInstance();

            idCurso = getIntent().getIntExtra("idCurso", -1);
            idClase = getIntent().getIntExtra("idClase", -1);
            idComentario = getIntent().getIntExtra("idComentario", -1);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                return;
            }

            String usuario = user.getEmail();
            String txtDenuncia = et_denuncia.getText().toString();

            if (txtDenuncia.isEmpty()) {
                Toast.makeText(this, "Por favor, especifica el problema", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = rg_TipoDenuncia.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Por favor, selecciona el tipo de denuncia", Toast.LENGTH_SHORT).show();
                return;
            }

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

            // Crear objeto denuncia
            Denuncia denuncia = new Denuncia(usuario, tipoDenuncia, txtDenuncia, -1);
            denuncia.setFecha_denuncia(Timestamp.now());

            if (idComentario != -1) {
                if (idCurso != -1 && idClase != -1) {
                    // Comentario dentro de clase
                    denuncia.setIdCurso(idCurso);
                    denuncia.setIdClase(idClase);
                } else if (idCurso != -1) {
                    // Comentario de curso
                    denuncia.setIdCurso(idCurso);
                    denuncia.setIdClase(-1);
                }
                denuncia.setIdComentario(idComentario);
                denuncia.setFormato("comentario");
            } else if (idClase != -1 && idCurso != -1) {
                // Clase
                denuncia.setIdCurso(idCurso);
                denuncia.setIdClase(idClase);
                denuncia.setIdComentario(-1);
                denuncia.setFormato("clase");
            } else if (idCurso != -1) {
                // Curso
                denuncia.setIdCurso(idCurso);
                denuncia.setIdClase(-1);
                denuncia.setIdComentario(-1);
                denuncia.setFormato("curso");
            } else {
                Toast.makeText(this, "❌ Error: no se proporcionaron datos válidos para la denuncia", Toast.LENGTH_SHORT).show();
                return;
            }

            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_denuncia, null);
            TextView tipo_denuncia = dialogView.findViewById(R.id.txt_vertipo_denuncia);
            TextView denuncia_texto = dialogView.findViewById(R.id.et_vermensaje_denuncia);
            Button btn_cancelar = dialogView.findViewById(R.id.btn_cancelar_dialog_creardenuncia);
            Button btn_enviar = dialogView.findViewById(R.id.btn_enviar_dialog_creardenuncia);

            tipo_denuncia.setText("Tipo: " + tipoDenuncia);
            denuncia_texto.setText("Contenido: \n\n" + txtDenuncia);
            denuncia_texto.setMovementMethod(new ScrollingMovementMethod());

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            btn_cancelar.setOnClickListener(v -> alertDialog.dismiss());
            btn_enviar.setOnClickListener(v -> {
                firestore.collection("denuncias")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            int idDenuncia = queryDocumentSnapshots.size() + 1;
                            denuncia.setIdDenuncia(idDenuncia);
                            buscarAutorYSubir(denuncia, alertDialog);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al obtener el ID de la denuncia", Toast.LENGTH_SHORT).show();
                        });
            });

            alertDialog.show();
        });
    }

    private void buscarAutorYSubir(Denuncia denuncia, AlertDialog dialog) {
        if (denuncia.getFormato().equals("comentario")) {
            firestore.collection("usuarios")
                    .whereEqualTo("correo", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(users -> {
                        if (!users.isEmpty()) {
                            denuncia.setAutorContenido(users.getDocuments().get(0).getId());
                        }
                        subirDenuncia(denuncia, dialog);
                    });
        } else if (denuncia.getFormato().equals("clase")) {
            firestore.collection("clases")
                    .whereEqualTo("idClase", denuncia.getIdClase())
                    .whereEqualTo("idCurso", denuncia.getIdCurso())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            String uidAutor = snapshot.getDocuments().get(0).getString("creadorUid");
                            denuncia.setAutorContenido(uidAutor);
                        }
                        subirDenuncia(denuncia, dialog);
                    });
        } else if (denuncia.getFormato().equals("curso")) {
            firestore.collection("cursos")
                    .whereEqualTo("idCurso", denuncia.getIdCurso())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            String correo = snapshot.getDocuments().get(0).getString("creador");
                            firestore.collection("usuarios")
                                    .whereEqualTo("correo", correo)
                                    .get()
                                    .addOnSuccessListener(usuarioSnap -> {
                                        if (!usuarioSnap.isEmpty()) {
                                            denuncia.setAutorContenido(usuarioSnap.getDocuments().get(0).getId());
                                        }
                                        subirDenuncia(denuncia, dialog);
                                    });
                        } else {
                            subirDenuncia(denuncia, dialog);
                        }
                    });
        }
    }

    private void subirDenuncia(Denuncia denuncia, AlertDialog dialog) {
        FirebaseFirestore.getInstance().collection("denuncias")
                .add(denuncia)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(CrearDenuncia.this, "Denuncia enviada", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CrearDenuncia.this, "Error al enviar la denuncia", Toast.LENGTH_SHORT).show();
                });
    }
}
