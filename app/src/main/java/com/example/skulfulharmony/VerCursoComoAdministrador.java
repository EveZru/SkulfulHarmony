package com.example.skulfulharmony;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class VerCursoComoAdministrador extends AppCompatActivity {

    private TextView txt_tipodenuncia_curso;
    private TextView txt_textodenuncia_curso;
    private TextView txt_titulo_curso;
    private TextView txt_autor_curso;
    private TextView txt_descripcion_curso;
    private ImageView img_curso;
    private Button btn_desestimar_curso;
    private Button btn_eliminar_curso;

    private String docCurso;
    private String docDenuncia;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_curso_como_administrador);

        // Inicializar vistas
        txt_tipodenuncia_curso = findViewById(R.id.vcca_txt_tipodenuncia_curso);
        txt_textodenuncia_curso = findViewById(R.id.vcca_txt_textodenuncia_curso);
        txt_titulo_curso = findViewById(R.id.vcca_txt_titulo_curso);
        txt_autor_curso = findViewById(R.id.vcca_txt_autor_curso);
        txt_descripcion_curso = findViewById(R.id.vcca_txt_descripcion_curso);
        img_curso = findViewById(R.id.vcca_img_curso);
        btn_desestimar_curso = findViewById(R.id.vcca_btn_desestimar_curso);
        btn_eliminar_curso = findViewById(R.id.vcca_btn_eliminar_curso);

        db = FirebaseFirestore.getInstance();
        int idDenuncia = getIntent().getIntExtra("idDenuncia", -1);
        int idCurso = getIntent().getIntExtra("idCurso", -1);

        cargarDatosCurso(idDenuncia, idCurso);

        btn_desestimar_curso.setOnClickListener(v -> {
            // Inflar el layout del diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_desestimar_denuncia, null);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            // Referencias a los botones del diálogo
            Button btnCancelar = dialogView.findViewById(R.id.btn_desestimar_dialog_cancelar);
            Button btnDesestimar = dialogView.findViewById(R.id.btn_desestimar_dialog_desestimar);

            // Acción botón Cancelar
            btnCancelar.setOnClickListener(view -> dialog.dismiss());

            // Acción botón Desestimar
            btnDesestimar.setOnClickListener(view -> {
                if (docDenuncia != null) {
                    db.collection("denuncias").document(docDenuncia)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                dialog.dismiss();
                                finish();
                            });
                }
            });

            dialog.show(); // Mostrar el diálogo
        });


        btn_eliminar_curso.setOnClickListener(v -> {
            // Inflar el layout del diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(VerCursoComoAdministrador.this).inflate(R.layout.dialog_comprobacion_de_eliminar, null);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            // Referencias a los botones del diálogo
            Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar_dialog_eliminarcontenido);
            Button btnEliminar = dialogView.findViewById(R.id.btn_enviar_dialog_eliminarcontenido);

            // Acción botón Cancelar
            btnCancelar.setOnClickListener(view -> dialog.dismiss());

            // Acción botón Eliminar
            btnEliminar.setOnClickListener(view -> {
                if (docCurso != null && docDenuncia != null) {
                    db.collection("clases").whereEqualTo("idCurso", idCurso)
                            .get()
                            .addOnSuccessListener(query -> {
                                for (DocumentSnapshot doc : query.getDocuments()) {
                                    doc.getReference().delete();
                                }

                                // 🗑 Eliminar curso y denuncia
                                db.collection("cursos").document(docCurso)
                                        .delete().addOnSuccessListener(aVoid -> {
                                            db.collection("denuncias").document(docDenuncia)
                                                    .delete().addOnSuccessListener(aVoid2 -> {
                                                        dialog.dismiss();
                                                        finish();
                                                    });
                                        });
                            });
                }
            });

            dialog.show(); // Mostrar el diálogo
        });

    }

    private void cargarDatosCurso(int idDenuncia, int idCurso) {
        if (idDenuncia != -1 && idCurso != -1) {
            db.collection("denuncias").whereEqualTo("idDenuncia", idDenuncia)
                    .limit(1).get().addOnSuccessListener(query -> {
                        if (!query.isEmpty()) {
                            DocumentSnapshot doc = query.getDocuments().get(0);
                            docDenuncia = doc.getId();
                            Denuncia denuncia = doc.toObject(Denuncia.class);
                            txt_tipodenuncia_curso.setText(denuncia.getTipo_denuncia());
                            txt_textodenuncia_curso.setText(denuncia.getDenuncia());

                            db.collection("cursos").whereEqualTo("idCurso", idCurso)
                                    .limit(1).get().addOnSuccessListener(query2 -> {
                                        if (!query2.isEmpty()) {
                                            DocumentSnapshot cursoDoc = query2.getDocuments().get(0);
                                            docCurso = cursoDoc.getId();
                                            Curso curso = cursoDoc.toObject(Curso.class);
                                            txt_titulo_curso.setText(curso.getTitulo());
                                            txt_autor_curso.setText(curso.getCreador());
                                            txt_descripcion_curso.setText(curso.getDescripcion());
                                            Glide.with(this)
                                                    .load(curso.getImagen())
                                                    .placeholder(R.drawable.loading)
                                                    .error(R.drawable.img_defaultclass)
                                                    .into(img_curso);
                                        }
                                    });
                        }
                    });
        }
    }
}