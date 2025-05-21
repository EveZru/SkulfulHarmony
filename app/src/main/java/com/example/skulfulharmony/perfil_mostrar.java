package com.example.skulfulharmony;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.skulfulharmony.javaobjects.users.Usuario;

public class perfil_mostrar extends AppCompatActivity {

    ImageView ivProfilePicture;
    TextView tvNombreUsuario, tvCorreo, tvDescripcion, tvNoCursos, tvSeguidores, tvSeguidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mostrar);  // nombre correcto del XML

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvNombreUsuario = findViewById(R.id.tv_NombreUsuario);
        tvCorreo = findViewById(R.id.tv_correo);
        tvDescripcion = findViewById(R.id.tv_DescripcionUsuario);
        tvNoCursos = findViewById(R.id.tv_No_Cursos);
        tvSeguidores = findViewById(R.id.tv_Seguidores);
        tvSeguidos = findViewById(R.id.tv_Seguido);

        // Obtener datos del Intent (Usuario debe implementar Serializable)
        Usuario usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        if (usuario != null) {
            Glide.with(this)
                    .load(usuario.getFotoPerfil())
                    .placeholder(R.drawable.default_profile)
                    .into(ivProfilePicture);

            tvNombreUsuario.setText(usuario.getNombre() != null ? usuario.getNombre() : "Sin Nombre");
            tvCorreo.setText(usuario.getCorreo() != null ? usuario.getCorreo() : "Sin correo");
            tvDescripcion.setText(usuario.getDescripcion() != null ? usuario.getDescripcion() : "Sin descripción");

            tvNoCursos.setText("Cursos Creados: " + usuario.getCursos());
            tvSeguidores.setText("Seguidores: " + usuario.getSeguidores());
            tvSeguidos.setText("Seguidos: " + 0);  // Actualiza si tienes info real de seguidos
        }
    }
}