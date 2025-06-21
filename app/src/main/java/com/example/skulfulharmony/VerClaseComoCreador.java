package com.example.skulfulharmony;

import static androidx.media3.exoplayer.mediacodec.MediaCodecInfo.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skulfulharmony.adapters.AdapterPreguntasEnVerClase;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class VerClaseComoCreador extends AppCompatActivity {
    private ImageView menupop;
    private PlayerView vv_videoplayer;
    private TextView tv_titulo,tv_info;
    private ExoPlayer player;
    private RecyclerView rv_preguntas, rv_archos;
    int idClase;

    private Clase clase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_clase_como_creador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_titulo=findViewById(R.id.tv_tituloclas);
        tv_info=findViewById(R.id.tv_info_verclase);
        rv_preguntas=findViewById(R.id.rv_preguntasporclase_verclase);
        idClase = getIntent().getIntExtra("idClase",1);



        menupop=findViewById(R.id.iv_menu);
        menupop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showMenu(view);}
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clases").whereEqualTo("idClase",idClase).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // Se encontró al menos un documento
                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                    String titulo = document.getString("titulo");
                    String descripcion = document.getString("textos");
                    clase=document.toObject(Clase.class);

                    // Verificar si los valores no son nulos antes de asignarlos
                    if (titulo != null) {
                        tv_titulo.setText(titulo);
                    } else {
                        tv_titulo.setText("Título no disponible");
                           }

                    if (descripcion != null) {
                        tv_info.setText(descripcion);
                    } else {
                        tv_info.setText("Descripción no disponible");
                    }

                    vv_videoplayer=findViewById(R.id.vv_videoclase);
                    player=new ExoPlayer.Builder(this).build();
                    String videoUrl = clase.getVideoUrl();
                    if (videoUrl != null && !videoUrl.isEmpty()) {
                        Uri videoUri = Uri.parse(videoUrl);
                        MediaItem mediaItem = MediaItem.fromUri(videoUri);
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        vv_videoplayer.setPlayer(player);
                    } else {
                        Toast.makeText(this, "No hay video disponible para esta clase", Toast.LENGTH_SHORT).show();
                    }



                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    AdapterPreguntasEnVerClase adapterPreguntasEnVerClase = new AdapterPreguntasEnVerClase(clase.getPreguntas(), VerClaseComoCreador.this);
                    rv_preguntas.setLayoutManager(layoutManager);
                    rv_preguntas.setAdapter(adapterPreguntasEnVerClase);

                  } else {
                    // No se encontró ninguna clase con ese idClase
                    tv_titulo.setText("Clase no encontrada");
                    tv_info.setText("Verifique el ID de la clase.");
                    Toast.makeText(VerClaseComoCreador.this, "Clase no encontrada.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Hubo un error al obtener los datos
                tv_titulo.setText("Error al cargar");
                tv_info.setText("Intente de nuevo más tarde.");
                Toast.makeText(VerClaseComoCreador.this, "Error al cargar la clase.", Toast.LENGTH_SHORT).show();
            }
        });


                }

    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.ver_como_creador, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item->{
            int id = item.getItemId();
            if(id==R.id.it_editar){
              /*  Intent intent = new Intent(VerClaseComoCreador.this, EditarCurso.class);// cambiar por la de editar calse ya que este
                intent.putExtra("idClase", idClase);
                startActivity(intent);
                return true;*/
                Toast.makeText(this, "Cambio a vista para eliminar curso ", Toast.LENGTH_SHORT).show();
                return true;
            }else if(id==R.id.it_eliminar){
                Toast.makeText(this, "Cambio a vista para eliminar curso ", Toast.LENGTH_SHORT).show();
                return true;
            }else {
                Toast.makeText(VerClaseComoCreador.this, "No se selecciono ninguna opcion", Toast.LENGTH_SHORT).show();
            }
            return false;

        });
        popupMenu.show();
    }

}