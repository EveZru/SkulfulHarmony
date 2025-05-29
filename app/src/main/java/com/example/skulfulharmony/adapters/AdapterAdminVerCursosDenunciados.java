package com.example.skulfulharmony.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
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
import com.example.skulfulharmony.VerCursoComoAdministrador;
import com.example.skulfulharmony.Ver_cursos;
import com.example.skulfulharmony.javaobjects.courses.Curso;
import com.example.skulfulharmony.javaobjects.miscellaneous.Denuncia;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterAdminVerCursosDenunciados extends RecyclerView.Adapter<AdapterAdminVerCursosDenunciados.ViewHolder> {
    private List<Denuncia> denuncias;

    public AdapterAdminVerCursosDenunciados(List<Denuncia> denuncias) {
        this.denuncias = denuncias;
    }

    @NonNull
    @Override
    public AdapterAdminVerCursosDenunciados.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_verdenuncia_curso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminVerCursosDenunciados.ViewHolder holder, int position) {

        Denuncia denuncia = denuncias.get(position);
        holder.txt_holder_denuncias_curso_fecha.setText(denuncia.getFecha_denuncia().toDate().toString());
        holder.txt_holder_denuncias_curso_tipo.setText(denuncia.getTipo_denuncia());
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
        holder.card_holder_denuncias_curso.setCardBackgroundColor(holder.itemView.getResources().getColor(color));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("denuncias")
                .whereEqualTo("idCurso", denuncia.getIdCurso())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Curso curso = queryDocumentSnapshots.getDocuments().get(0).toObject(Curso.class);
                        Glide.with(holder.itemView.getContext())
                                .load(curso.getImagen())
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.img_defaultclass)
                                .into(holder.img_holder_denuncias_curso_imagen);

                        holder.txt_holder_denuncias_curso_titulo.setText(curso.getTitulo());

                        db.collection("usuarios")
                                .whereEqualTo("correo", curso.getCreador())
                                .limit(1)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshot -> {
                                    if (!queryDocumentSnapshot.isEmpty()) {
                                        String nombre = queryDocumentSnapshot.getDocuments().get(0).getString("nombre");
                                        holder.txt_holder_denuncias_curso_autor.setText("Publicado por: " + nombre);
                                    } else {
                                        holder.txt_holder_denuncias_curso_autor.setText("Autor desconocido");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    holder.txt_holder_denuncias_curso_autor.setText("Autor desconocido");
                                });
                    } else {
                        holder.txt_holder_denuncias_curso_titulo.setText("Curso no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.txt_holder_denuncias_curso_titulo.setText("Error al cargar curso");
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
                Intent intent = new Intent(holder.itemView.getContext(), VerCursoComoAdministrador.class);
                intent.putExtra("idCurso", denuncia.getIdCurso());
                holder.itemView.getContext().startActivity(intent);
                alertDialog.dismiss();
            });

            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return denuncias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_holder_denuncias_curso_titulo;
        TextView txt_holder_denuncias_curso_autor;
        TextView txt_holder_denuncias_curso_fecha;
        TextView txt_holder_denuncias_curso_tipo;
        ImageView img_holder_denuncias_curso_imagen;
        CardView card_holder_denuncias_curso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_holder_denuncias_curso_titulo = itemView.findViewById(R.id.txt_holder_denuncias_curso_titulo);
            txt_holder_denuncias_curso_autor = itemView.findViewById(R.id.txt_holder_denuncias_curso_autor);
            txt_holder_denuncias_curso_fecha = itemView.findViewById(R.id.txt_holder_denuncias_curso_fecha);
            txt_holder_denuncias_curso_tipo = itemView.findViewById(R.id.txt_holder_denuncias_curso_tipo);
            img_holder_denuncias_curso_imagen = itemView.findViewById(R.id.img_holder_denuncias_curso_imagen);
            card_holder_denuncias_curso = itemView.findViewById(R.id.card_holder_denuncias_curso);
        }
    }
}
