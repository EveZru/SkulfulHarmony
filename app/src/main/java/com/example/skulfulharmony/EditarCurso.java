package com.example.skulfulharmony;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.IOException;

public class EditarCurso extends AppCompatActivity {
    private TextView tv_nombrecurso,tv_descripcioncurso;
    private Button btn_cambiar, btn_cancelar ;
    private EditText et_nueva_descripcion;
    private FirebaseFirestore firestore;
    private int idCurso;
    private ImageView iv_fotocurso;
    private String nombreCursoActual;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri im = Uri.EMPTY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_curso);

        iv_fotocurso=findViewById(R.id.iv_fotocurso);
        btn_cambiar = findViewById(R.id.btn_updatecurso);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        tv_nombrecurso = findViewById(R.id.tv_nombrecurso);
        tv_descripcioncurso = findViewById(R.id.tv_descripantreior);
        et_nueva_descripcion = findViewById(R.id.et_descripnueva);


        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        idCurso=intent.getIntExtra("idCurso",-1);
        if(idCurso!=-1){
            cargarDatosCurso(idCurso);
            Toast.makeText(this, "idCurso: "+idCurso, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error al obtener la informacion del curso", Toast.LENGTH_SHORT).show();
        }

        btn_cancelar.setOnClickListener(v -> {
            Intent intentregresar=new Intent(EditarCurso.this,VerCursoComoCreador.class);
            intentregresar.putExtra("idCurso",idCurso);
            startActivity(intentregresar);
            finish();
        });

        btn_cambiar.setOnClickListener(v -> {


            String nuevaDescripcion = et_nueva_descripcion.getText().toString();

            firestore.collection("cursos").whereEqualTo("idCurso", idCurso)
            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentReference docRef=queryDocumentSnapshots.getDocuments().get(0).getReference();
                   // proseso para editar la descrpcion
                    if(nuevaDescripcion!=null&& nuevaDescripcion!="") {
                        Toast.makeText(EditarCurso.this,"Cambiando descripcion",Toast.LENGTH_SHORT);
                        docRef.update("descripcion", nuevaDescripcion);
                    }else{
                        Toast.makeText(EditarCurso.this,"No se realizaron cambios en la descripcion del curso ",Toast.LENGTH_SHORT);
                    }


                }

            });

        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                im = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), im);
                    iv_fotocurso.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        iv_fotocurso.setOnClickListener(v -> {
            openGalery();
        });
    }

    private void cargarDatosCurso(int id) {
        firestore.collection("cursos").whereEqualTo("idCurso", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        String nombre = queryDocumentSnapshots.getDocuments().get(0).getString("titulo");
                        String descripcion = queryDocumentSnapshots.getDocuments().get(0).getString("descripcion");

                        if (nombre != null) {
                            nombreCursoActual = nombre;
                            tv_nombrecurso.setText("_Editarás los datos de: " + nombre +" _");
                        }
                        if (descripcion != null) {
                            tv_descripcioncurso.setText("Descripción anterior: " + descripcion);
                        }
                    } else {
                        Toast.makeText(EditarCurso.this, "Curso no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarCurso.this, "Error al cargar el curso: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EditarCurso", "Error al cargar el curso", e);
                    finish();
                });
    }
    private void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

}