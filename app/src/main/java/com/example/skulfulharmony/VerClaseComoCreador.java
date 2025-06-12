package com.example.skulfulharmony;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerClaseComoCreador extends AppCompatActivity {
    private ImageView menupop;

    int idClase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        idClase = getIntent().getIntExtra("idClase",1);

        menupop=findViewById(R.id.iv_menu);

        menupop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showMenu(view);}
        });

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_clase_como_creador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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