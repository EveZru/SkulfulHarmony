package com.example.skulfulharmony.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skulfulharmony.R;
import com.example.skulfulharmony.VerClaseComoAdministrador;
import com.example.skulfulharmony.VerCursoComoAdministrador;
import com.example.skulfulharmony.javaobjects.courses.Clase;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.example.skulfulharmony.javaobjects.users.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterAdminVerClasesDenunciadas extends RecyclerView.Adapter<AdapterAdminVerClasesDenunciadas.ViewHolder> {
    List<Denuncia> listaClasesDenunciadas = new ArrayList<>();

    public AdapterAdminVerClasesDenunciadas(List<Denuncia> listaClases) {
        this.listaClasesDenunciadas = listaClases;
    }

    @NonNull
    @Override
    public AdapterAdminVerClasesDenunciadas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verdenuncia_clase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminVerClasesDenunciadas.ViewHolder holder, int position) {
        Denuncia denuncia = listaClasesDenunciadas.get(position);

        holder.txt_holder_denuncias_clase_fecha.setText(denuncia.getFecha_denuncia().toDate().toString());
        holder.getTxt_holder_denuncias_clase_tipo.setText(denuncia.getTipo_denuncia());
        int color;
        switch (denuncia.getTipo_denuncia()) {
            case "Contenido ilegal":
                color = R.color.rojo_oscuro;
                break;
            case "Suplantación de identidad":
            case "Contenido inapropiado":
                color = R.color.rojo;
                break;
            case "Spam":
            case "Abuso de plataforma":
                color = R.color.cafe;
                break;
            default:
                color = R.color.verde;
                break;
        }
        holder.card_holder_denuncias_clase.setCardBackgroundColor(holder.itemView.getResources().getColor(color));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clases")
                .whereEqualTo("idCurso",denuncia.getIdCurso())
                .whereEqualTo("idClase", denuncia.getIdClase())
                .limit(1)
                .get()
                .addOnSuccessListener( onQuerySnapshots -> {
                    if(!onQuerySnapshots.isEmpty()){
                        Clase clase = onQuerySnapshots.getDocuments().get(0).toObject(Clase.class);
                        holder.txt_holder_denuncias_clase_titulo.setText(clase.getTitulo());
                        db.collection("cursos")
                                .whereEqualTo("idCurso", clase.getIdCurso())
                                .limit(1)
                                .get()
                                .addOnSuccessListener( onSuperQuerySnapshots ->{
                                    if(!onSuperQuerySnapshots.isEmpty()){
                                        Curso curso = onSuperQuerySnapshots.getDocuments().get(0).toObject(Curso.class);
                                        holder.txt_holder_denuncias_clase_curso.setText(curso.getTitulo());
                                        Glide.with(holder.itemView.getContext())
                                                .load(curso.getImagen())
                                                .placeholder(R.drawable.loading)
                                                .error(R.drawable.img_defaultclass)
                                                .into(holder.img_holder_denuncias_clase_imagen);
                                        db.collection("usuarios")
                                                .whereEqualTo("correo",curso.getCreador())
                                                .limit(1)
                                                .get()
                                                .addOnSuccessListener(onSuperSuperQuerySnapshots->{
                                                    if(!onSuperSuperQuerySnapshots.isEmpty()){
                                                        Usuario user = onSuperSuperQuerySnapshots.getDocuments().get(0).toObject(Usuario.class);
                                                        holder.txt_holder_denuncias_clase_autor.setText(user.getNombre());
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Error en denuncias de clase: ", "Error al obtenr el usuario: " + e);
                                                    holder.txt_holder_denuncias_clase_autor.setText("Autor desconocido");
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Error en denuncias de clase: ", "Error al obtener el curso: " + e);
                                    holder.txt_holder_denuncias_clase_curso.setText("Curso no encontrado");
                                });
                    }
                })
                .addOnFailureListener( e -> {
                    Log.e("Error en denuncias de clase: ", "Error al obtener la clase: " + e);
                    holder.txt_holder_denuncias_clase_titulo.setText("No encontrado");
                });

        holder.itemView.setOnClickListener(v -> {

            View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_ver_datosdedenuncia, null);
            TextView tipo_denuncia = dialogView.findViewById(R.id.txt_vertipo_denuncia_admin);
            TextView denuncia_texto = dialogView.findViewById(R.id.et_vermensaje_denuncia_admin);
            Button btn_omitir = dialogView.findViewById(R.id.btn_omitir_dialog_verdenuncia_admin);
            Button btn_examinar = dialogView.findViewById(R.id.btn_examinar_dialog_verdenuncia_admin);

            String tipo = "Tipo:" + "\n"  + denuncia.getTipo_denuncia();
            String contenido = "Contenido:" + "\n" + denuncia.getDenuncia();
            tipo_denuncia.setText(tipo);
            denuncia_texto.setText(contenido);
            denuncia_texto.setMovementMethod(new ScrollingMovementMethod());

            AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext())
                    .setView(dialogView)
                    .create();

            btn_omitir.setOnClickListener(v2 -> alertDialog.dismiss());
            btn_examinar.setOnClickListener(v2 -> {
                Intent intent = new Intent(holder.itemView.getContext(), VerClaseComoAdministrador.class);
                intent.putExtra("idCurso", denuncia.getIdCurso());
                intent.putExtra("idClase", denuncia.getIdClase());
                intent.putExtra("idDenuncia", denuncia.getIdDenuncia());
                holder.itemView.getContext().startActivity(intent);
                alertDialog.dismiss();
            });

            alertDialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return listaClasesDenunciadas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_holder_denuncias_clase_titulo;
        TextView txt_holder_denuncias_clase_curso;
        TextView txt_holder_denuncias_clase_autor;
        TextView txt_holder_denuncias_clase_fecha;
        TextView getTxt_holder_denuncias_clase_tipo;
        ImageView img_holder_denuncias_clase_imagen;
        CardView card_holder_denuncias_clase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_holder_denuncias_clase_titulo = itemView.findViewById(R.id.txt_holder_denuncias_clase_titulo);
            txt_holder_denuncias_clase_curso = itemView.findViewById(R.id.txt_holder_denuncias_clase_curso);
            txt_holder_denuncias_clase_autor = itemView.findViewById(R.id.txt_holder_denuncias_clase_autor);
            txt_holder_denuncias_clase_fecha = itemView.findViewById(R.id.txt_holder_denuncias_clase_fecha);
            getTxt_holder_denuncias_clase_tipo = itemView.findViewById(R.id.txt_holder_denuncias_clase_tipo);
            img_holder_denuncias_clase_imagen = itemView.findViewById(R.id.img_holder_denuncias_clase_imagen);
            card_holder_denuncias_clase = itemView.findViewById(R.id.card_holder_denuncias_clase);
        }
    }
}
